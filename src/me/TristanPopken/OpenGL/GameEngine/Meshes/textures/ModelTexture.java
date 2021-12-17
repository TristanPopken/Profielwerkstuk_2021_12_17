package me.TristanPopken.OpenGL.GameEngine.Meshes.textures;

public class ModelTexture {
	
	private int textureID;
	private int[] textureIDs;
	private int normalMap;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	public ModelTexture(int id) {
		this.textureID = id;
	}
	
	public ModelTexture(int[] ids) {
		this.textureIDs = ids;
	}
	
	public int getID() {
		return this.textureID;
	}
	
	public int[] getIDs() {
		return this.textureIDs;
	}
	
	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}
	
	public int getNormalMap() {
		return normalMap;
	}
	
	public float getShineDamper() {
		return shineDamper;
	}
	
	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}
	
	public float getReflectivity() {
		return reflectivity;
	}
	
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
}
