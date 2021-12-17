package me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.Tree;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.PlanetSettings.PlanetSettings;

public class CelestialBodyHandler {
	
	ArrayList<Planet> bodies = new ArrayList<Planet>();
	
	public void addBody(PlanetSettings settings) {
		Planet planet = new Planet(settings);
		bodies.add(planet);
	}
	
	public void updateBodies(Vector3f cameraPos) {
		for (Planet body : bodies) {
			body.Update(cameraPos);
		}
	}
	
}
