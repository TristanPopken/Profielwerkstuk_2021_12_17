package me.TristanPopken.OpenGL.GameEngine.renderEngine.Renderers;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import me.TristanPopken.OpenGL.GameEngine.Meshes.Planet.PlanetEntity;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Planet.PlanetModel;
import me.TristanPopken.OpenGL.GameEngine.Meshes.models.RawModel;
import me.TristanPopken.OpenGL.GameEngine.Meshes.textures.ModelTexture;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Loader;
import me.TristanPopken.OpenGL.GameEngine.shaders.Shaders.PlanetShader;
import me.TristanPopken.OpenGL.GameEngine.tools.Maths;

public class PlanetRenderer {
	
	private PlanetShader shader;
	
	public static final String[] textures = new String[]
			{"Poles","Tundra","Boreal","Dicidious","Rainforest","Desert","Rock","Sandstone","Beach","grassNormal","rockNormal"};
	private int[] textureIDs;
	
	public PlanetRenderer(Loader loader, PlanetShader shader, Matrix4f projectionMatrix) {
		textureIDs = loader.loadTextures(textures);
		this.shader = shader;
		shader.start();
		shader.loadTextures(textureIDs);
		shader.loadprojectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<PlanetModel,List<PlanetEntity>> planets) {
//		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);//WIREFRAME on
		for (PlanetModel model : planets.keySet()) {
			preparePlanetModel(model);
			List<PlanetEntity> batch = planets.get(model);
			for (PlanetEntity planet : batch) {
				prepareInstance(planet);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
//		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);//WIREFRAME off
	}
	
	private void preparePlanetModel(PlanetModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = model.getTexture();
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); //Pixels wont Interpolate
		
		int[] ids = textureIDs;
		for (int i = 0; i < ids.length; i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+i+1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ids[i]);
		}
		
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(PlanetEntity planet) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrixFromRotation(planet.getPosition(), planet.getRotX(), planet.getRotY(), planet.getRotZ(), planet.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}
