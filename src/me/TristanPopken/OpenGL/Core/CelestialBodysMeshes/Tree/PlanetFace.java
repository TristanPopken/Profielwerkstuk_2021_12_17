package me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.Tree;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.PlanetSettings.PlanetSettings;

public class PlanetFace {
	
	public static Vector3f[] directions = {
			new Vector3f( 1,  0,  0),
			new Vector3f(-1,  0,  0),
			new Vector3f( 0,  1,  0),
			new Vector3f( 0, -1,  0),
			new Vector3f( 0,  0,  1),
			new Vector3f( 0,  0, -1)
	};
	
	final int maxDepth = 11;
	
	private PlanetQuadTree tree;
	
	public PlanetFace(int faceIndex) {
		Vector3f dir = directions[faceIndex];
		this.tree = new PlanetQuadTree(dir, 0, 0, 0, maxDepth);
	}
	
	public void Update(Vector3f cameraPos, PlanetSettings settings) {
		tree.Update(cameraPos, settings);
	}
	
	
}
