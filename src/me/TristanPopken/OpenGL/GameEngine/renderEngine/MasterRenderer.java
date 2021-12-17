package me.TristanPopken.OpenGL.GameEngine.renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import me.TristanPopken.OpenGL.GameEngine.Camera;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Entity;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Light;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Planet.PlanetEntity;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Planet.PlanetModel;
import me.TristanPopken.OpenGL.GameEngine.Meshes.models.TexturedModel;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Renderers.EntityRenderer;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Renderers.PlanetRenderer;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Renderers.SkyboxRenderer;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Renderers.normalMapRenderer;
import me.TristanPopken.OpenGL.GameEngine.shaders.Shaders.NormalMappingShader;
import me.TristanPopken.OpenGL.GameEngine.shaders.Shaders.PlanetShader;
import me.TristanPopken.OpenGL.GameEngine.shaders.Shaders.SkyboxShader;
import me.TristanPopken.OpenGL.GameEngine.shaders.Shaders.StaticShader;

public class MasterRenderer {
	
	private static final float FOV = 94;
	public static float NEAR_PLANE = 0.001f;
	public static float FAR_PLANE = 100000;
	
	public static final float RED = 0.1f;
	public static final float GREEN = 0.4f;
	public static final float BLUE = 0.2f;
	
	public static Matrix4f projectionMatrix;
	
	//--------------------< Render Data >--------------------//
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private Map<PlanetModel, List<PlanetEntity>> planets = new HashMap<PlanetModel, List<PlanetEntity>>();
	
	//--------------------< Renderers >--------------------//
	
	private SkyboxRenderer skyboxRenderer;
	private normalMapRenderer normalMapRenderer;
	private EntityRenderer entityRenderer;
	private PlanetRenderer planetRenderer;
	
	//--------------------< Shaders >--------------------//
	
	private SkyboxShader skyShader = new SkyboxShader();
	private NormalMappingShader normalShader = new NormalMappingShader();
	private StaticShader shader = new StaticShader();
	private PlanetShader planetShader = new PlanetShader();
	
	public MasterRenderer(Loader loader) {
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(shader, projectionMatrix);
		planetRenderer = new PlanetRenderer(loader, planetShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(skyShader, projectionMatrix, loader);
		normalMapRenderer = new normalMapRenderer(normalShader, projectionMatrix);
	}
	
	public void render(Light sun, Camera camera) {
		prepare();
		
		skyShader.start();
		skyShader.loadViewMatrix(camera);
		skyboxRenderer.render(camera);
		skyShader.stop();
		
		shader.start();
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
		entityRenderer.render(entities);
		shader.stop();
		
		planetShader.start();
		planetShader.loadLight(sun);
		planetShader.loadCameraPos(camera.getPosition());
		planetShader.loadViewMatrix(camera);
		planetRenderer.render(planets);
		planetShader.stop();
		
		normalShader.start();
		normalShader.loadLight(sun);
		normalShader.loadViewMatrix(camera);
		normalMapRenderer.render(normalMapEntities);
		normalShader.stop();
		
		planets.clear();
		entities.clear();
		normalMapEntities.clear();
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void addPlanet(PlanetEntity planet) {
		PlanetModel planetModel = planet.getModel();
		List<PlanetEntity> batch = planets.get(planetModel);
		if (batch != null) {
			batch.add(planet);
		} else {
			List<PlanetEntity> newBatch = new ArrayList<PlanetEntity>();
			newBatch.add(planet);
			planets.put(planetModel, newBatch);
		}
	}
	
	public void addEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void addNormalMapEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}
	
	public void cleanUp() {
		shader.cleanUp();
		planetShader.cleanUp();
		normalShader.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public static void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
//		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
}
