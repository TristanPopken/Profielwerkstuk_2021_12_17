package me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.Tree;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.PlanetSettings.PlanetSettings;

public class Planet {
	
	PlanetSettings settings;
	PlanetFace[] faces;
	
	public Planet(PlanetSettings settings) {
		this.settings = settings;
		this.faces = new PlanetFace[6];
		for (int i = 0; i < 6; i++) {
			PlanetFace face = new PlanetFace(i);
			faces[i] = face;
		}
	}
	
	public void Update(Vector3f cameraPos) {
		for (PlanetFace face : faces) {
			face.Update(cameraPos, settings);
		}
	}
	
}
