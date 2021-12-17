package me.TristanPopken.OpenGL.GameEngine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.GameEngine.renderEngine.DisplayManager;
import me.TristanPopken.OpenGL.GameEngine.tools.Maths;

enum CameraMode {
	FREE, CRAFT, PLANET
}

public class Camera {
	
	public static float variable = 0;
	public static float timeSpeed = 0;
	public static int engine = 0;
	
	private final float maxPitch = 89;
	
	private final float wohr = 16/9;
	boolean escapeMode = false;
	long lastToggleEscape = System.nanoTime();
	long lastToggleMode = System.nanoTime();
	long lastToggleTime = System.nanoTime();
	
	public Vector3f forwardVector;
	public Vector3f TopVector;
	
	private Vector3f position;
	private Matrix4f viewMatrix;
	
	CameraMode mode;
	float distance = 0.1f;
	
	public Camera() {
		position = new Vector3f(0, 637.01f, 0.005f);
		forwardVector = new Vector3f(0,0,-1);
		TopVector = new Vector3f(0,1,0);
		viewMatrix = createViewMatrix(this);
		mode = CameraMode.CRAFT;
	}
	
	public void move(Vector3f craftLoc, float frameTimeMs) {
		long newTime = System.nanoTime();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {
			DisplayManager.toggleFullscreen();
			DisplayManager.updateMouseVisability(mode == CameraMode.FREE);
		}
		
		if (newTime - lastToggleTime > 500000000) {
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) timeSpeed++;
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) timeSpeed--;
			timeSpeed = timeSpeed<0?0:timeSpeed;
			lastToggleTime = newTime;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_O)) variable += frameTimeMs / 10f;
		if (Keyboard.isKeyDown(Keyboard.KEY_P)) variable -= frameTimeMs / 10f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) engine = 1;
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) engine = 0;
		
		if (newTime - lastToggleMode > 500000000) { //0.5 second
			if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
				if (mode == CameraMode.CRAFT) {
					mode = CameraMode.FREE;
				} else if (mode == CameraMode.FREE) {
					distance = 1800;
					mode = CameraMode.PLANET;
				} else {
					distance = 0.1f;
					mode = CameraMode.CRAFT;
				}
				DisplayManager.updateMouseVisability(mode == CameraMode.FREE);
				lastToggleMode = newTime;
			}
		}
		
		switch (mode) {
			case CRAFT:
				moveCraft(craftLoc, frameTimeMs);
				break;
			case FREE:
				moveFree(frameTimeMs);
				break;
			case PLANET:
				moveCraft(new Vector3f(0,10,0), frameTimeMs);
				break;
		}
		viewMatrix = createViewMatrix(this);
	}
	
	public void moveCraft(Vector3f craftLoc, float frameTimeMS) {
		
		if (mode == CameraMode.CRAFT) TopVector = (Vector3f) new Vector3f(position).normalise();
		else                          TopVector = new Vector3f(0,1,0);
		
		if (Mouse.isButtonDown(1)) {
			float dYaw   = -Mouse.getDX() / 8f / wohr;
			float dPitch =  Mouse.getDY() / 8f;
			
			float currentPitch = 90 - (float) Math.toDegrees(Vector3f.angle(forwardVector, TopVector));
			float totalPitch = currentPitch + dPitch;
			if (totalPitch >  maxPitch) dPitch =  maxPitch - currentPitch;
			if (totalPitch < -maxPitch) dPitch = -maxPitch - currentPitch;
			
			Vector3f right = Vector3f.cross(forwardVector, TopVector, null);
			right.normalise();
			
			forwardVector = Maths.rotateVectorAroundAxis(dYaw  , forwardVector, TopVector);
			forwardVector = Maths.rotateVectorAroundAxis(dPitch, forwardVector, right);
			
		}
		
		if (mode == CameraMode.CRAFT) distance += -Mouse.getDWheel() / 3000f * distance;
		else                          distance += -Mouse.getDWheel() / 3000f * (distance-637);
		Vector3f toCam = (Vector3f) new Vector3f(forwardVector).scale(-1);
		Vector3f deltaPosition = (Vector3f)new Vector3f(toCam).scale(distance);
		position = Vector3f.add(deltaPosition, craftLoc, null);
		
	}
	
	public void moveFree(float frameTimeMs) {
		float speed = frameTimeMs / 10f;
		
		float length = position.length();
		if (length > 657) {
			TopVector = new Vector3f(0, 1, 0);
		} else {
			speed /= 15f;
			TopVector = new Vector3f(position.x/length, position.y/length, position.z/length); //Pointing away from planet
		}
		if (escapeMode == false) {
			float dYaw   = -Mouse.getDX() / 8f / wohr;
			float dPitch =  Mouse.getDY() / 8f;
			forwardVector = updateCameraVectors(dPitch, dYaw);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_X))        speed /= 20f;
		if (Keyboard.isKeyDown(Keyboard.KEY_C))        speed /= 400f;
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) speed *= 20f;
		if (Keyboard.isKeyDown(Keyboard.KEY_CAPITAL))  speed *= 11.766666666f;
		
		Vector3f Top = new Vector3f(TopVector);
		Vector3f Tan = Vector3f.cross(TopVector, forwardVector, null);Tan.normalise();//tangent
		Vector3f For = new Vector3f(forwardVector);
		Top.scale(speed);
		Tan.scale(speed);
		For.scale(speed);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) Vector3f.add(position, For, position);
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) Vector3f.sub(position, For, position);
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) Vector3f.add(position, Tan, position);
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) Vector3f.sub(position, Tan, position);
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))  Vector3f.add(position, Top, position);
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) Vector3f.sub(position, Top, position);
		
		long newTime = System.nanoTime();
		if (newTime - lastToggleEscape > 500000000) { //0.5 second
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				escapeMode = !escapeMode;
				lastToggleEscape = newTime;
			}
		}
		
	}
	
	public Vector3f updateCameraVectors(float dPitch, float dYaw) {
		//https://stackoverflow.com/questions/6721544/circular-rotation-around-an-arbitrary-axis
		//https://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
		
		float currentPitch = 90 - (float) Math.toDegrees(Vector3f.angle(forwardVector, TopVector));
		float totalPitch = currentPitch + dPitch;
		if (totalPitch >  maxPitch) dPitch =  maxPitch - currentPitch;
		if (totalPitch < -maxPitch) dPitch = -maxPitch - currentPitch;
		
		Vector3f u1 = new Vector3f(TopVector);
		Vector3f u2 = Vector3f.cross(forwardVector, TopVector, null);
		u1.normalise();
		u2.normalise();
		
		float a1 = (float) Math.toRadians(dYaw);
		float a2 = (float) Math.toRadians(dPitch);
		
		float c1 = (float) Math.cos(a1); float c2 = (float) Math.cos(a2);
		float s1 = (float) Math.sin(a1); float s2 = (float) Math.sin(a2);
		
		Matrix3f R1 = new Matrix3f();
		R1.m00 = c1+sq(u1.x)*(1-c1);       R1.m10 = u1.x*u1.y*(1-c1)-u1.z*s1; R1.m20 = u1.x*u1.z*(1-c1)+u1.y*s1;
		R1.m01 = u1.y*u1.x*(1-c1)+u1.z*s1; R1.m11 = c1+sq(u1.y)*(1-c1);       R1.m21 = u1.y*u1.z*(1-c1)-u1.x*s1;
		R1.m02 = u1.z*u1.x*(1-c1)-u1.y*s1; R1.m12 = u1.z*u1.y*(1-c1)+u1.x*s1; R1.m22 = c1+sq(u1.z)*(1-c1);
		
		Matrix3f R2 = new Matrix3f();
		R2.m00 = c2+sq(u2.x)*(1-c2);       R2.m10 = u2.x*u2.y*(1-c2)-u2.z*s2; R2.m20 = u2.x*u2.z*(1-c2)+u2.y*s2;
		R2.m01 = u2.y*u2.x*(1-c2)+u2.z*s2; R2.m11 = c2+sq(u2.y)*(1-c2);       R2.m21 = u2.y*u2.z*(1-c2)-u2.x*s2;
		R2.m02 = u2.z*u2.x*(1-c2)-u2.y*s2; R2.m12 = u2.z*u2.y*(1-c2)+u2.x*s2; R2.m22 = c2+sq(u2.z)*(1-c2);
		
		Vector3f v = new Vector3f(forwardVector);
		v = Matrix3f.transform(R1, v, null);
		v = Matrix3f.transform(R2, v, null);
		v.normalise();
		
		return v;
	}
	
	public Vector3f getUpVector() {
		Vector3f tangent = Vector3f.cross(forwardVector, TopVector, null);
		tangent.normalise();
		return Maths.rotateVectorAroundAxis(90, forwardVector, tangent);
	}
	
	private static Matrix4f createViewMatrix(Camera camera) {
		//https://www.geertarien.com/blog/2017/07/30/breakdown-of-the-lookAt-function-in-OpenGL/
		
		Vector3f u = camera.getUpVector();
		Vector3f z = (Vector3f)new Vector3f(camera.getForwardVector()).scale(-1); //forward
		Vector3f x = Vector3f.cross(u, z, null); x.normalise();                   //right (tangent)
		Vector3f y = Vector3f.cross(z, x, null);                                  //up
		
		Vector3f p = camera.getPosition();
		
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		m.m00 = x.x; m.m10 = x.y; m.m20 = x.z; m.m30 = -Vector3f.dot(x, p);
		m.m01 = y.x; m.m11 = y.y; m.m21 = y.z; m.m31 = -Vector3f.dot(y, p);
		m.m02 = z.x; m.m12 = z.y; m.m22 = z.z; m.m32 = -Vector3f.dot(z, p);
		
		return m;
	}
	
	private float sq(float a) {
		return a*a;
	}
	
	public Matrix4f getViewMatrix() {
		return new Matrix4f(this.viewMatrix);
	}
	
	public Vector3f getForwardVector() {
		return this.forwardVector;
	}
	
	public void setPosition(Vector3f pos) {
		this.position = pos;
	}
	
	public Vector3f getPosition() {
		return this.position;
	}
	
}
