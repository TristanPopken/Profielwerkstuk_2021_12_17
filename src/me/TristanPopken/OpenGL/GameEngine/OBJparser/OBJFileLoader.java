package me.TristanPopken.OpenGL.GameEngine.OBJparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class OBJFileLoader {
	
	public static boolean isEngine = false;
	
	private static int linePos = 0;
	private static int dv = 0;
	private static int dt = 0;
	private static int dn = 0;
	private static final String RES_LOC = "res/";
	
	private static Vector2f currentColor = new Vector2f();
	
	public static ModelData loadOBJ(String objFileName) {
		FileReader isr = null;
		File objFile = new File(RES_LOC + objFileName + ".obj");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found in res; don't use any extention");
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		int cdv = dv;
		int cdt = dt;
		int cdn = dn;
		try {
			int length = 34368;
			if ((length - linePos) < 3) {
				return null;
			}
			for (int i = 0; i < linePos; i++) {
				reader.readLine();
			}
			while (true) {
				line = reader.readLine();
				linePos++;
				if (line.startsWith("o ")) {
					String[] currentLine = line.split(" ");
					switch (currentLine[1].substring(0, Math.min(12, currentLine[1].length()))) {
						case "Cylinder.002":
						case "Cylinder.003":
						case "Cylinder.004":
						case "Cylinder.005":
						case "Cylinder.006":
						case "Cylinder.016":
						case "Cylinder.017":
						case "Cylinder.018":
						case "Cylinder.019":
							isEngine = true;
							break;
						default:
							isEngine = false;
					}
				} else if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f(
							(float) Float.valueOf(currentLine[1])+1.45f,
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);
					dv++;
				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
					dt++;
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					normals.add(normal);
					dn++;
				} else if (line.startsWith("usemtl")) {
					String[] currentLine = line.split(" ");
					switch(currentLine[1]) {
						case "Material.001":
							currentColor = new Vector2f(0.25f, 0.75f);
							break;
						case "Material.002":
							currentColor = new Vector2f(0.75f, 0.75f);
							break;
						case "Material.003":
							currentColor = new Vector2f(0.25f, 0.25f);
							break;
						case "metall":
							currentColor = new Vector2f(0.75f, 0.25f);
							break;
						case "Material":
							currentColor = new Vector2f(0.50f, 0.50f);
							break;
					}
				} else if (line.startsWith("f ")) {
					linePos--;
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, vertices, indices, cdv, cdt, cdn, textures);
				processVertex(vertex2, vertices, indices, cdv, cdt, cdn, textures);
				processVertex(vertex3, vertices, indices, cdv, cdt, cdn, textures);
				line = reader.readLine();
				linePos++;
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
				texturesArray, normalsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
				furthest);
		return data;
	}
	
	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices, int dv, int dt, int dn, List<Vector2f> tex) {
		int index = Integer.parseInt(vertex[0]) - 1 - dv;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1 - dt;
		int normalIndex = Integer.parseInt(vertex[2]) - 1 - dn;
		tex.set(textureIndex, new Vector2f(currentColor));
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else {
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
					vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
			List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
						indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}

		}
	}
	
	private static void removeUnusedVertices(List<Vertex> vertices){
		for(Vertex vertex:vertices){
			if(!vertex.isSet()){
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

}