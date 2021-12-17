package me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.Tree;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.Data;
import me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.MeshGenerator.PlanetFaceCalculator;
import me.TristanPopken.OpenGL.Core.PlanetSettings.PlanetSettings;
import me.TristanPopken.OpenGL.GameEngine.Meshes.MeshHandler;
import me.TristanPopken.OpenGL.GameEngine.tools.Maths;

public class PlanetQuadTree {
	
	private enum Type {
		NONE, BRANCH, LEAF
	}
	
	PlanetQuadTree[] children;
	
	Vector3f MeshPos;
	Vector3f MeshCorners;
	
	Integer meshIndex;
	Type lastFrameType;
	
	int depth;
	int maxDepth;
	
	Vector3f localUp;
	float x;
	float y;
	
	boolean canUpdate = true;
	
	public PlanetQuadTree(Vector3f localUp, float x, float y, int depth, int maxDepth) {
		this.children = new PlanetQuadTree[4];
		this.depth = depth;
		this.MeshPos = calcMeshPosition(localUp, x, y);
		this.lastFrameType = Type.NONE;
		this.maxDepth = maxDepth;
		this.localUp = localUp;
		this.x = x;
		this.y = y;
	}
	
	public void Update(Vector3f CameraPos, PlanetSettings settings) {
		if (!canUpdate) {
			return;
		}
		try {
			
			float distance = Maths.distance(MeshPos, CameraPos);
			
			Type newType;
			if ((distance > 3186 * Math.pow(0.5, depth) || depth == maxDepth) && depth >= 3) {
				newType = Type.LEAF;
			} else {
				newType = Type.BRANCH;
			}
			
			switch (lastFrameType) {
				case NONE:
					if (newType == Type.BRANCH) {
						createChildren();
						updateChildren(CameraPos, settings);
					} else {
						createMesh();
						//This code is extremely fast, every mesh calculation is done in parralel. Sadly it is to quick for the meshHandler D:
						//To use this code, activate the citated code at the bottom aswell
//						Thread t1 = new Thread(new Runnable() {
//						    @Override
//						    public void run() {
//						    	canUpdate = false;
//						    	createMesh();
//						    	canUpdate = true;
//						    }
//						});
//						t1.start();
					}
					break;
				case BRANCH:
					if (newType == Type.LEAF) {//Goes from branch to leaf (Less detail)
						createMesh();
						destroyChildren();
					} else {
						updateChildren(CameraPos, settings);
					}
					break;
				case LEAF:
					if (newType == Type.BRANCH) {//Goes from leaf to branch (More detail will come)
						createChildren();
						updateChildren(CameraPos, settings);
						destroyMesh();
					}
					break;
			}
			
			lastFrameType = newType;
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error at depth "+depth);
		}
		
	}
	
	public void deleteThis() {
		
		switch (lastFrameType) {
			case LEAF:
				destroyMesh();
				break;
			case BRANCH:
				for (int i = 0; i < 4; i ++) {
					children[i].deleteThis();
				}
				break;
			case NONE:
				return;
		}
		
	}
	
	private Vector3f calcMeshPosition(Vector3f localUp, float minx, float miny) {
		
		float chunkSize = (float) Math.pow(0.5, depth) / 2f;
		
		float dx = (minx + chunkSize - .5f) * 2;
		float dy = (miny + chunkSize - .5f) * 2;
		
		Vector3f axisA = new Vector3f(localUp.y, localUp.z, localUp.x);
		Vector3f axisB = Vector3f.cross(localUp, axisA, null);
		
		float pointX = (localUp.x + dx * axisA.x + dy * axisB.x);
		float pointY = (localUp.y + dx * axisA.y + dy * axisB.y);
		float pointZ = (localUp.z + dx * axisA.z + dy * axisB.z);
		
		Vector3f point = new Vector3f(pointX, pointY, pointZ);
		
		return (Vector3f) point.normalise().scale(637);
	}
	
	private void createMesh() {
		
		PlanetFaceCalculator calculator = new PlanetFaceCalculator(637, localUp);
		Data data = calculator.generate(62, depth, x, y);
		
		data.addMeshPosition(MeshPos);
		
//		while (MeshHandler.isStoring) {
//			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
//		}
		meshIndex = MeshHandler.storePlanet(data);
	}
	
	private void destroyMesh() {
		if (meshIndex != null) {
			MeshHandler.deletePlanet(meshIndex);
		} else {
			System.out.println("MeshIndex = null");
		}
	}
	
	private void createChildren() {
		float ChunkSizeHalf = (float) Math.pow(0.5, depth) / 2f;
		
		children[0] = new PlanetQuadTree(localUp, x                , y                , depth+1, maxDepth);
		children[1] = new PlanetQuadTree(localUp, x + ChunkSizeHalf, y                , depth+1, maxDepth);
		children[2] = new PlanetQuadTree(localUp, x                , y + ChunkSizeHalf, depth+1, maxDepth);
		children[3] = new PlanetQuadTree(localUp, x + ChunkSizeHalf, y + ChunkSizeHalf, depth+1, maxDepth);
		
	}
	
	private void updateChildren(Vector3f CameraPos, PlanetSettings settings) {
		for (int i = 0; i < 4; i ++) {
			children[i].Update(CameraPos, settings);
		}
	}
	
	private void destroyChildren() {
		for (int i = 0; i < 4; i ++) {
			children[i].deleteThis();
		}
		children = new PlanetQuadTree[4];
	}
	
}
