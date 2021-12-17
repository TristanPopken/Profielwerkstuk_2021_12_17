package me.TristanPopken.OpenGL.GameEngine.shaders.Shaders;

import org.lwjgl.util.vector.Matrix4f;

import me.TristanPopken.OpenGL.GameEngine.Camera;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Light;
import me.TristanPopken.OpenGL.GameEngine.shaders.ShaderProgram;

public class StaticShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "V.txt";
	private static final String FRAGMENT_FILE = "F.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_cameraPos;
	
	private int location_lightPosition;
	private int location_lightColour;
	
	private int location_shineDamper;
	private int location_reflectivity;
	
	private int location_engineData;
	
	public StaticShader() {
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
		location_engineData = super.getUniformLocation("engineData");
	}
	
	public void loadEngineData(boolean engineData) {
		super.loadInt(location_engineData, Camera.engine*(engineData?1:0));
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadLight(Light light) {
		super.load3DVector(location_lightPosition, light.getPosition());
		super.load3DVector(location_lightColour, light.getColour());
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = camera.getViewMatrix();
		super.loadMatrix(location_viewMatrix, viewMatrix);
		super.load3DVector(location_cameraPos, camera.getPosition());
	}
	
	public void loadprojectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
}
