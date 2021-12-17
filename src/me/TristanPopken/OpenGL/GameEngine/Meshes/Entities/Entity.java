package me.TristanPopken.OpenGL.GameEngine.Meshes.Entities;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.GameEngine.Meshes.models.TexturedModel;

public class Entity {
	
	private TexturedModel model;
	private Vector3f position;
	
	private Vector3f forward;
	private Vector3f up;
	
	private float scale;
	
	public boolean data;
	
	public Entity() {
		
	}
	
	public Entity(TexturedModel model, Vector3f position, Vector3f forward, Vector3f up, float scale) {
		this.model = model;
		this.position = position;
		this.scale = scale;
		this.forward = forward;
		this.up = up;
	}
	
	public TexturedModel getModel() {
		return model;
	}
	
	public void setModel(TexturedModel model) {
		this.model = model;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getForward() {
		return forward;
	}
	
	public void setForward(Vector3f forward) {
		this.forward = forward;
	}
	
	public Vector3f getUp() {
		return up;
	}
	
	public void setUp(Vector3f up) {
		this.up = up;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
}
