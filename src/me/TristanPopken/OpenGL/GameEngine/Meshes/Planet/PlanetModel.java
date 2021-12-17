package me.TristanPopken.OpenGL.GameEngine.Meshes.Planet;

import me.TristanPopken.OpenGL.GameEngine.Meshes.models.RawModel;
import me.TristanPopken.OpenGL.GameEngine.Meshes.textures.ModelTexture;

public class PlanetModel {
	
	private RawModel model;
	private ModelTexture texture;
	
	public PlanetModel(RawModel model, ModelTexture texture) {
		this.model = model;
		this.texture = texture;
	}
	
	public RawModel getRawModel() {
		return model;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
}
