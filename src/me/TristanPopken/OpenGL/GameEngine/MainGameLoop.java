package me.TristanPopken.OpenGL.GameEngine;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.Core;
import me.TristanPopken.OpenGL.Core.PlanetSettings.PlanetSettings;
import me.TristanPopken.OpenGL.Core.PlanetSettings.Earth.SettingsEarth;
import me.TristanPopken.OpenGL.GameEngine.Meshes.MeshHandler;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Entity;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Light;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Planet.PlanetEntity;
import me.TristanPopken.OpenGL.GameEngine.Meshes.models.RawModel;
import me.TristanPopken.OpenGL.GameEngine.Meshes.models.TexturedModel;
import me.TristanPopken.OpenGL.GameEngine.Meshes.textures.ModelTexture;
import me.TristanPopken.OpenGL.GameEngine.OBJparser.ModelData;
import me.TristanPopken.OpenGL.GameEngine.OBJparser.OBJFileLoader;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.DisplayManager;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Loader;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.MasterRenderer;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.postProcessing.Fbo;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.postProcessing.PostProcessing;

public class MainGameLoop {
	
	static Core core = new Core();
	
	static boolean running = true;
	
	public static void main(String[] args) throws Exception {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer(loader);
		
		Light light = new Light(new Vector3f(0, 0,-7044420f), new Vector3f(1,1,1));
		
		long lastTime = System.nanoTime();
		float passedTime = 0;
		float frameTimeMs = 0;
		long lastTimeIncrements = System.nanoTime();
		int frames = 0;
		int time = 0;
		
		Thread t1 = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	
		    	core.start(camera);
		    	
		    	long coreTime = System.nanoTime();
		    	while (running) {
		    		
		    		long currentCoretime = System.nanoTime();
		    		double frameTime = (currentCoretime - coreTime)/(double)1E9;
		    		coreTime = currentCoretime;
		    		core.loop(camera, frameTime);
		    		
		    	}
				
		    }
		});  
		t1.start();
		
		ArrayList<RawModel> models = new ArrayList<RawModel>();
		while (true) {
			ModelData data = OBJFileLoader.loadOBJ("shuttle");
			if (data == null) {
				break;
			} else {
				RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
				models.add(model);
				model.data = OBJFileLoader.isEngine;
			}
		}
		
		ModelTexture shuttleTexture = new ModelTexture(loader.loadTexture("Pallet"));
		shuttleTexture.setReflectivity(0.5f);
		shuttleTexture.setShineDamper(100);
		
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for (RawModel model : models) {
			TexturedModel shuttleModel = new TexturedModel(model, shuttleTexture);
			float truescale = 0.0001f*37f/11f;
			Vector3f forward = new Vector3f(1, 0, 0);
			Vector3f up      = new Vector3f(0, 1, 0);
			Entity entity = new Entity(shuttleModel, new Vector3f(637.004f, 0, 0), forward, up, truescale*3);
			entity.data = model.data;
			entities.add(entity);
		}
		
		PlanetSettings settings = (PlanetSettings) new SettingsEarth();
		
		//-------------------< FBO >-------------------//
		Fbo fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);
		
		boolean allowFrustumCulling = false;
		long LastFrustumToggle = System.nanoTime();
		while(!Display.isCloseRequested()) {
			
			Vector3f shuttlePos = GameEngineHandler.pos;
			Vector3f shuttleFrw = GameEngineHandler.frw;
			Vector3f shuttleUp = GameEngineHandler.up;
			
			long newTime = System.nanoTime();
			frameTimeMs = (newTime - lastTime) / 1000000l;
			passedTime += frameTimeMs;
			lastTime = newTime;
			GameEngineHandler.time = passedTime;
			
			camera.move(shuttlePos, frameTimeMs);
			
			float dist = -14088840;// * (float) (Math.sin(passedTime / 2000f + Math.PI / 4.0 * 3.0) / 2f + 0.5f + 0.016f);
			light.setPosition(new Vector3f((float)Math.cos(Camera.variable / 100f) * dist, 0, (float)Math.sin(Camera.variable / 100f) * dist));
			
			if (MeshHandler.RequiresUpdate()) {
				MeshHandler.update(settings, loader);
				if (time == 0) time = 1;
			}
			
			for (PlanetEntity planet : MeshHandler.getEntities()) {
				long FrustumTime = System.nanoTime();
				if (FrustumTime - LastFrustumToggle > 500000000) { //0.5 second
					if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
						allowFrustumCulling = !allowFrustumCulling;
						LastFrustumToggle = newTime;
					}
				}
				Vector3f delta = Vector3f.sub(new Vector3f(planet.getMeshPosition()), camera.getPosition(), null);
				Vector3f dir = new Vector3f(delta);
				dir.normalise();
				float dot = Vector3f.dot(dir, camera.getForwardVector());
				if (dot > 0.3 || delta.length() < 2 || !allowFrustumCulling) {
					renderer.addPlanet(planet);
				}
			}
			
			for (Entity entity : entities) {
				entity.setPosition(shuttlePos);
				entity.setForward(shuttleFrw);
				entity.setUp(shuttleUp);
				renderer.addEntity(entity);
			}
			
			fbo.bindFrameBuffer();
			renderer.render(light, camera);
			fbo.unbindFrameBuffer();
			PostProcessing.doPostProcessing(camera, light, fbo.getColourTexture(), fbo.getDepthTexture(), passedTime);
			
			DisplayManager.updateDisplay();
			
			frames++;
			if (time > 0) time++;
			if ((newTime - lastTimeIncrements) / 1000000l > 1000) {System.out.println("Triangles: "+MeshHandler.totalTriangleCount+", FPS: "+frames+", res: "+Display.getWidth()+"x"+Display.getHeight()); lastTimeIncrements = newTime; frames = 0; }
			
		}
		
		GameEngineHandler.windowIsClosed = true;
		PostProcessing.cleanUp();
		renderer.cleanUp();
		loader.cleanUP();
		DisplayManager.closeDisplay();
		running = false;
		
	}
	
}
