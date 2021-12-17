package me.TristanPopken.OpenGL.Core.CelestialBodysMeshes;

import org.lwjgl.util.vector.Vector3f;

public class Data {
	
	Vector3f meshPosition;
	
	float[] vertices;
	int[] indices;
	float[] textureCoords;
	float[] normals;
	
	public Data(float[] vertices, int[] indices, float[] textureCoords, float[] normals) {
		this.vertices = vertices;
		this.indices = indices;
		this.textureCoords = textureCoords;
		this.normals = normals;
	}
	
	public void addMeshPosition(Vector3f meshPosition) {
		this.meshPosition = meshPosition;
	}
	
	public Vector3f getMeshPosition() {
		return meshPosition;
	}
	
	public Data addData(Data data) {
		
		float[] vertices2      = data.getVertices();
		int  [] indices2       = data.getIndices();
		float[] textureCoords2 = data.getTextureCoords();
		float[] normals2       = data.getNormals();
		
		float[] newvertices      = new float[vertices.length      + vertices2.length     ];
		int  [] newindices       = new int  [indices.length       + indices2.length      ];
		float[] newtextureCoords = new float[textureCoords.length + textureCoords2.length];
		float[] newnormals       = new float[normals.length       + normals2.length      ];
		
		for (int i = 0; i < vertices.length; i++) {
			newvertices[i] = vertices[i];
		}
		for (int i = vertices.length; i < newvertices.length; i++) {
			newvertices[i] = vertices2[i-vertices.length];
		}
		
		int deltaIndice = vertices.length / 3;
		for (int i = 0; i < indices.length; i++) {
			newindices[i] = indices[i];
		}
		for (int i = indices.length; i < newindices.length; i++) {
			newindices[i] = indices2[i-indices.length] + deltaIndice;
		}
		
		for (int i = 0; i < textureCoords.length; i++) {
			newtextureCoords[i] = textureCoords[i];
		}
		for (int i = textureCoords.length; i < newtextureCoords.length; i++) {
			newtextureCoords[i] = textureCoords2[i-textureCoords.length];
		}
		
		for (int i = 0; i < normals.length; i++) {
			newnormals[i] = normals[i];
		}
		for (int i = normals.length; i < newnormals.length; i++) {
			newnormals[i] = normals2[i-normals.length];
		}
		
//		int i = 0;
//		for (float element : vertices) {
//			newvertices[i] = element;
//			i++;
//		}
//		for (float element : vertices2) {
//			newvertices[i] = element;
//			i++;
//		}
//		
//		i = 0;
//		for (int element : indices) {
//			newindices[i] = element;
//			i++;
//		}
//		for (int element : indices2) {
//			newindices[i] = element;
//			i++;
//		}
//		
//		i = 0;
//		for (float element : textureCoords) {
//			newtextureCoords[i] = element;
//			i++;
//		}
//		for (float element : textureCoords2) {
//			newtextureCoords[i] = element;
//			i++;
//		}
//		
//		i = 0;
//		for (float element : normals) {
//			newnormals[i] = element;
//			i++;
//		}
//		for (float element : normals2) {
//			newnormals[i] = element;
//			i++;
//		}
		
		Data newData = new Data(newvertices, newindices, newtextureCoords, newnormals);
		newData.addMeshPosition(meshPosition);
		return newData;
		
//		this.vertices = newvertices;
//		this.indices = newindices;
//		this.textureCoords = newtextureCoords;
//		this.normals = newnormals;
		
	}
	
	public float[] getVertices() {
		return vertices;
	}
	
	public int[] getIndices() {
		return indices;
	}
	
	public float[] getTextureCoords() {
		return textureCoords;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
}
