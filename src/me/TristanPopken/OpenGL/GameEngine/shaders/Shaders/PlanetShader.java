package me.TristanPopken.OpenGL.GameEngine.shaders.Shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.GameEngine.Camera;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Light;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Renderers.PlanetRenderer;
import me.TristanPopken.OpenGL.GameEngine.shaders.ShaderProgram;

public class PlanetShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "Vplanet.txt";
	private static final String FRAGMENT_FILE = "Fplanet.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	private int location_lightPosition;
	private int location_lightColour;
	
	private int location_shineDamper;
	private int location_reflectivity;
	
	private int location_cameraPos;
	private int location_cameraHeight;
	
	private int[] imgLocations;
	private int location_variable;
	
	public PlanetShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColour = super.getUniformLocation("lightColour");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_cameraPos = super.getUniformLocation("cameraPos");
		location_cameraHeight = super.getUniformLocation("cameraHeight");
		location_variable = super.getUniformLocation("variable");
		imgLocations = new int[PlanetRenderer.textures.length];
		for (int i = 0; i < imgLocations.length; i++) {
			imgLocations[i] = super.getUniformLocation("im"+(i+1));
		}
	}
	
	public void loadTextures(int[] textureIDs) {
		for (int i = 0; i < textureIDs.length; i++) {
			super.loadInt(imgLocations[i], textureIDs[i]);
		}
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadCameraPos(Vector3f cameraPos) {
		super.load3DVector(location_cameraPos, cameraPos);
		super.loadFloat(location_cameraHeight, cameraPos.length()-637);
		super.loadFloat(location_variable, Camera.variable);
	}
	
	public void loadLight(Light light) {
		super.load3DVector(location_lightPosition, light.getPosition());
		super.load3DVector(location_lightColour, light.getColour());
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = camera.getViewMatrix();
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadprojectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
}
