package me.TristanPopken.OpenGL.GameEngine.shaders.Shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.GameEngine.Camera;
import me.TristanPopken.OpenGL.GameEngine.shaders.ShaderProgram;

public class SkyboxShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "Vskybox.txt";
	private static final String FRAGMENT_FILE = "Fskybox.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = camera.getViewMatrix();
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		//Matrix4f.rotate((float)System.nanoTime() / 3000000000f, new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(130), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(62), new Vector3f(0, 0, 1), matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}
