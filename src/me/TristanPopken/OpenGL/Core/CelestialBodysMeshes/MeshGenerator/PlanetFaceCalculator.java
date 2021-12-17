package me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.MeshGenerator;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.Data;
import me.TristanPopken.OpenGL.Core.PlanetSettings.Earth.ClimateCalculator;
import me.TristanPopken.OpenGL.Core.PlanetSettings.Earth.HeightCalculator;
import me.TristanPopken.OpenGL.GameEngine.tools.Maths;

public class PlanetFaceCalculator {
	
	private float scale;
	private Vector3f localUp;
	private Vector3f axisA;
	private Vector3f axisB;
	
	public PlanetFaceCalculator(float scale, Vector3f localUp) {
		this.scale = scale;
		this.localUp = localUp;
		
		axisA = new Vector3f(localUp.y, localUp.z, localUp.x);
		axisB = Vector3f.cross(localUp, axisA, null);
		
	}
	
	public Data generate(int resolution, int lod, float minx, float miny) {
		
		HeightCalculator t = new HeightCalculator();
		
		float chunkSize = (float) Math.pow(0.5, lod);
		
		float[] vertices      = new float[ resolution      *  resolution      * 3];
		int  [] indices       = new int  [(resolution - 1) * (resolution - 1) * 6];
		float[] textureCoords = new float[ resolution      *  resolution      * 2];
		float[] normals       = new float[ resolution      *  resolution      * 3];
		
		int verticeIndex = 0;
		int indiceIndex = 0;
		int textureIndex = 0;
		
		for (int y = 0; y < resolution; y++) {
			for (int x = 0; x < resolution; x++) {
				
				float dx1 = x / (resolution-1f) * chunkSize;
				float dy1 = y / (resolution-1f) * chunkSize;
				
				float dx = (dx1 + minx - .5f) * 2;
				float dy = (dy1 + miny - .5f) * 2;
				
				float pointX = (localUp.x + dx * axisA.x + dy * axisB.x);
				float pointY = (localUp.y + dx * axisA.y + dy * axisB.y);
				float pointZ = (localUp.z + dx * axisA.z + dy * axisB.z);
				
				Vector3f pointOnUnitSphere = new Vector3f(pointX, pointY, pointZ).normalise(null);
				
				Vector3f heights = t.Generate(pointOnUnitSphere, scale);
				
				float pointFX = pointOnUnitSphere.x * scale + heights.x;
				float pointFY = pointOnUnitSphere.y * scale + heights.y;
				float pointFZ = pointOnUnitSphere.z * scale + heights.z;
				
				vertices[verticeIndex*3]   = pointFX;
				vertices[verticeIndex*3+1] = pointFY;
				vertices[verticeIndex*3+2] = pointFZ;
				
				if (x != resolution - 1 && y != resolution - 1) {
					indices[indiceIndex]   = verticeIndex;
					indices[indiceIndex+1] = verticeIndex + resolution + 1;
					indices[indiceIndex+2] = verticeIndex + resolution;
					indices[indiceIndex+3] = verticeIndex;
					indices[indiceIndex+4] = verticeIndex + 1;
					indices[indiceIndex+5] = verticeIndex + resolution + 1;
					indiceIndex += 6;
				}
				
				final float height = t.getTerrainHeight();
				
				float temp = ClimateCalculator.getTemp(pointOnUnitSphere, height);
				textureCoords[textureIndex] = temp;
				
				textureIndex += 2;
				verticeIndex++;
			}
		}
		
		verticeIndex = 0;
		textureIndex = 0;
		
		for (int y = 0; y < resolution; y++) {
			for (int x = 0; x < resolution; x++) {
				
				Vector3f mid = new Vector3f(vertices[verticeIndex], vertices[verticeIndex+1], vertices[verticeIndex+2]);
				
				Vector3f dx, dz;
				
				boolean neg = false;
				if (x != 0) {
					dx = new Vector3f(vertices[verticeIndex - 3], vertices[verticeIndex - 2], vertices[verticeIndex - 1]);
				} else {
					neg = true;
					dx = new Vector3f(vertices[verticeIndex + 3], vertices[verticeIndex + 4], vertices[verticeIndex + 5]);
				}
				
				if (y != 0) {
					dz = new Vector3f(vertices[verticeIndex - resolution*3], vertices[verticeIndex+1 - resolution*3], vertices[verticeIndex+2 - resolution*3]);
				} else {
					neg = !neg;
					dz = new Vector3f(vertices[verticeIndex + resolution*3], vertices[verticeIndex+1 + resolution*3], vertices[verticeIndex+2 + resolution*3]);
				}
				
				Vector3f v1 = Vector3f.sub(mid, dx, null);
				Vector3f v2 = Vector3f.sub(mid, dz, null);
				
				Vector3f normal = Vector3f.cross(v2, v1, null);
				normal.normalise();
				
				if (neg) {
					Maths.invertVector(normal);
				}
				
				normals[verticeIndex]   = normal.x;
				normals[verticeIndex+1] = normal.y;
				normals[verticeIndex+2] = normal.z;
				
				Vector3f sphereNormal = new Vector3f(vertices[verticeIndex], vertices[verticeIndex+1], vertices[verticeIndex+2]);
				sphereNormal.normalise();
				
				//texture based on slope
				float temp = textureCoords[textureIndex];
				textureCoords[textureIndex+1] = ClimateCalculator.getRockiness(sphereNormal, normal, temp);
				
				textureIndex += 2;
				verticeIndex += 3;
				
			}
		}
		
		return new Data(vertices, indices, textureCoords, normals);
		
	}
	
}
