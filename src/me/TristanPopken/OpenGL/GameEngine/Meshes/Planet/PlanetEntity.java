package me.TristanPopken.OpenGL.GameEngine.Meshes.Planet;

import org.lwjgl.util.vector.Vector3f;

public class PlanetEntity {
	
	private Vector3f meshPosition;
	
	private PlanetModel model;
	private Vector3f position;
	private Vector3f rotation;
	private float scale;
	
	public PlanetEntity(PlanetModel model, Vector3f position, Vector3f rotation, float scale, Vector3f meshPosition) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.meshPosition = meshPosition;
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void increaseRotation(float dx, float dy, float dz) {
		this.rotation.x += dx;
		this.rotation.y += dy;
		this.rotation.z += dz;
	}
	
	public Vector3f getMeshPosition() {
		return meshPosition;
	}
	
	public PlanetModel getModel() {
		return model;
	}
	
	public void setModel(PlanetModel model) {
		this.model = model;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getRot() {
		return rotation;
	}
	
	public float getRotX() {
		return rotation.x;
	}
	
	public void setRotX(float rotX) {
		this.rotation.x = rotX;
	}
	
	public float getRotY() {
		return rotation.y;
	}
	
	public void setRotY(float rotY) {
		this.rotation.y = rotY;
	}
	
	public float getRotZ() {
		return rotation.z;
	}
	
	public void setRotZ(float rotZ) {
		this.rotation.z = rotZ;
	}
	
	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
}
