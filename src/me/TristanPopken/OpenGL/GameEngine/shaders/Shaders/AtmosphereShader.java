package me.TristanPopken.OpenGL.GameEngine.shaders.Shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.GameEngine.Camera;
import me.TristanPopken.OpenGL.GameEngine.shaders.ShaderProgram;

public class AtmosphereShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "Vpost.txt";
	private static final String FRAGMENT_FILE = "Fpost.txt";
	
	private int location_colorTexture;
	private int location_depthTexture;
	private int location_waveNormalTexture1;
	private int location_waveNormalTexture2;
	private int location_waveDuDv;
	
	private int location_cameraPos;
	private int location_negViewMatrix;
	private int location_negProjMatrix;
	
	private int location_scatteringCoefficients;
	private int location_sunLocation;
	private int location_time;
	
	private int location_min;
	private int location_max;
	private int location_variable;
	
	public AtmosphereShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_cameraPos = super.getUniformLocation("cameraPos");
		location_negViewMatrix = super.getUniformLocation("negViewMatrix");
		location_negProjMatrix = super.getUniformLocation("negProjMatrix");
		location_scatteringCoefficients = super.getUniformLocation("scatteringCoefficients");
		location_time = super.getUniformLocation("time");
		location_sunLocation = super.getUniformLocation("sunPos");
		location_colorTexture = super.getUniformLocation("colorTexture");
		location_depthTexture = super.getUniformLocation("depthTexture");
		location_waveNormalTexture1 = super.getUniformLocation("waveNormalTexture1");
		location_waveNormalTexture2 = super.getUniformLocation("waveNormalTexture2");
		location_waveDuDv = super.getUniformLocation("waveDuDv");
		location_min = super.getUniformLocation("near");
		location_max = super.getUniformLocation("far");
		location_variable = super.getUniformLocation("variable");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadWaveNormalTexture() {
		super.loadInt(location_waveNormalTexture1, 2);
		super.loadInt(location_waveNormalTexture2, 3);
		super.loadInt(location_waveDuDv, 4);
	}
	
	public void loadDepthTexture() {
		super.loadInt(location_colorTexture, 0);
		super.loadInt(location_depthTexture, 1);
	}
	
	public void loadCameraPosition(Vector3f pos) {
		super.load3DVector(location_cameraPos, pos);
		super.loadFloat(location_variable, Camera.variable);
	}
	
	public void loadSunPosition(Vector3f pos) {
		super.load3DVector(location_sunLocation, pos);
	}
	
	public void loadData() {
		
		float scatteringStrenght = 0.05f;
		
		Vector3f waveLengths = new Vector3f(700, 530, 440);
//		Vector3f waveLengths = new Vector3f(440, 500, 700);
//		Vector3f waveLengths = new Vector3f(540, 795, 440);
		
	    float scatterR = (float) (Math.pow(400 / waveLengths.x, 4) * scatteringStrenght);
	    float scatterG = (float) (Math.pow(400 / waveLengths.y, 4) * scatteringStrenght);
	    float scatterB = (float) (Math.pow(400 / waveLengths.z, 4) * scatteringStrenght);
	    
	    Vector3f scatteringCoefficients = new Vector3f(scatterR, scatterG, scatterB);
	    
		super.load3DVector(location_scatteringCoefficients, scatteringCoefficients);
		
	}
	
	public void loadDistances(float min, float max) {
		super.loadFloat(location_min, max);
		super.loadFloat(location_max, min);
	}
	
	public void loadViewMatrix(Matrix4f matrix) {
		super.loadMatrix(location_negViewMatrix, Matrix4f.invert(matrix, null));
	}
	
	public void loadTime(float time) {
		super.loadFloat(location_time, time);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_negProjMatrix, Matrix4f.invert(matrix, null));
	}
	
}
