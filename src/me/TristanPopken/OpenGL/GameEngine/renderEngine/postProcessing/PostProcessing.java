package me.TristanPopken.OpenGL.GameEngine.renderEngine.postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import me.TristanPopken.OpenGL.GameEngine.Camera;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Light;
import me.TristanPopken.OpenGL.GameEngine.Meshes.models.RawModel;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Loader;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.MasterRenderer;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static AtmosphereRenderer atr;
	
	public static void init(Loader loader) {
		quad = loader.loadToVAO(POSITIONS, 2);
		atr = new AtmosphereRenderer(loader);
	}
	
	public static void doPostProcessing(Camera camera, Light light, int texture, int dtexture, float time) {
		start();
		atr.render(camera, light, texture, dtexture, time, MasterRenderer.NEAR_PLANE, MasterRenderer.FAR_PLANE);
		end();
	}
	
	public static void cleanUp() {
		atr.cleanUp();
	}
	
	private static void start() {
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
