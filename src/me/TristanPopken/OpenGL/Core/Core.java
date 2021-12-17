package me.TristanPopken.OpenGL.Core;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.Tree.CelestialBodyHandler;
import me.TristanPopken.OpenGL.Core.PhysicsEngine.Physics;
import me.TristanPopken.OpenGL.Core.PhysicsEngine.vec3;
import me.TristanPopken.OpenGL.Core.PlanetSettings.PlanetSettings;
import me.TristanPopken.OpenGL.Core.PlanetSettings.Earth.SettingsEarth;
import me.TristanPopken.OpenGL.GameEngine.Camera;
import me.TristanPopken.OpenGL.GameEngine.GameEngineHandler;

public class Core {
	
	CelestialBodyHandler celestialBodyHandler = new CelestialBodyHandler();
	
	Physics physics = new Physics();
	
	public void start(Camera camera) {
		
		int res = 500;
		PlanetSettings settings = (PlanetSettings) new SettingsEarth();
		settings.res = res;
		
		celestialBodyHandler.addBody(settings);
		
		Thread t1 = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	while (!GameEngineHandler.windowIsClosed) {
		    		celestialBodyHandler.updateBodies(camera.getPosition());
		    	}
		    }
		});  
		t1.start();
		
	}
	
	long lastFrame = System.nanoTime();
	int frame = 0;
	
	public void loop(Camera camera, double frameTime) {
		
		double timeFactor = Math.pow(2, Camera.timeSpeed);
		frameTime *= timeFactor;
		
		boolean message = frame%100000==0;
		physics.update(frameTime, message);
		
		vec3 pos = physics.getCraftLocation();
		vec3 frw = physics.getCraftForward();
		vec3 up  = physics.getCraftUp();
		
		Vector3f pos2 = pos.getVector3f();
		Vector3f frw2 = frw.getVector3f();
		Vector3f up2  = up.getVector3f();
		
		GameEngineHandler.pos = pos2;
		GameEngineHandler.frw = frw2;
		GameEngineHandler.up  = up2;
		
		frame++;
		long newFrame = System.nanoTime();
		if (newFrame - lastFrame > 1000000000l) {
			System.out.println("");
			System.out.println("Physics Calculations per Second: "+frame);
			System.out.println("Physics time warp: "+timeFactor);
			lastFrame = newFrame;
			frame = 0;
		}
		
	}
	
	
}
