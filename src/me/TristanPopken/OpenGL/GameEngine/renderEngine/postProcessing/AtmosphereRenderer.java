package me.TristanPopken.OpenGL.GameEngine.renderEngine.postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import me.TristanPopken.OpenGL.GameEngine.Camera;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Light;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Loader;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.MasterRenderer;
import me.TristanPopken.OpenGL.GameEngine.shaders.Shaders.AtmosphereShader;

public class AtmosphereRenderer {
	
	private ImageRenderer renderer;
	private AtmosphereShader shader;
	
	private int waveNormalID1;
	private int waveNormalID2;
	private int waveDuDv;
	
	public AtmosphereRenderer(Loader loader) {
		waveNormalID1 = loader.loadTexture("waveNormal1");
		waveNormalID2 = loader.loadTexture("waveNormal2");
		waveDuDv = loader.loadTexture("wavesDuDv");
		shader = new AtmosphereShader();
		renderer = new ImageRenderer();
		shader.start();
		shader.loadWaveNormalTexture();
		shader.loadData();
		shader.loadProjectionMatrix(MasterRenderer.projectionMatrix);
		shader.stop();
	}
	
	public void render(Camera camera, Light light, int texture, int dtexture, float time, float min, float max) {
		shader.start();
		shader.loadDistances(min, max);
		shader.loadCameraPosition(camera.getPosition());
		shader.loadSunPosition(light.getPosition());
		shader.loadTime(time);
		shader.loadViewMatrix(camera.getViewMatrix());
		shader.loadDepthTexture();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dtexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waveNormalID1);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waveNormalID2);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waveDuDv);
		renderer.renderQuad();
		shader.stop();
	}
	
	public void cleanUp() {
		renderer.cleanUp();
		shader.cleanUp();
	}
	
	
}
