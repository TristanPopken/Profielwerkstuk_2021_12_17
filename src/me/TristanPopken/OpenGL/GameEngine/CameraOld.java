package me.TristanPopken.OpenGL.GameEngine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.GameEngine.renderEngine.DisplayManager;

/* This is the old camera class,
 * This was used with pitch yaw roll system
 */

public class CameraOld {
	
	boolean escapeMode = true;
	long lastToggle = System.nanoTime();
	
	private Vector3f position = new Vector3f(0, 0, 900);
	private float pitch;
	private float yaw;
	private float roll;
	private final float wohr = 16/9;
	
	public CameraOld() {
		pitch = 0;
		yaw = 0;
		roll = 0;
	}
	
	public void move(float frameTimeMs) {
		
		float speed = 0.4f * frameTimeMs / 10f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			speed = speed / 20;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			speed = speed / 400;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			speed = speed * 20;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_CAPITAL)) {
			speed = speed * 11.766666666f;//1412 <- this not because 120fps
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {
			DisplayManager.toggleFullscreen();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.x += speed * Math.sin(Math.toRadians(yaw));
			position.z -= speed * Math.cos(Math.toRadians(yaw));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.x -= speed * Math.sin(Math.toRadians(yaw));
			position.z += speed * Math.cos(Math.toRadians(yaw));
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.z -= speed * Math.sin(Math.toRadians(yaw));
			position.x -= speed * Math.cos(Math.toRadians(yaw));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.z += speed * Math.sin(Math.toRadians(yaw));
			position.x += speed * Math.cos(Math.toRadians(yaw));
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			position.y += speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			position.y -= speed;
		}
		
		long newTime = System.nanoTime();
		if (newTime - lastToggle > 500000000) { //0.5 second
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				escapeMode = !escapeMode;
				lastToggle = newTime;
			}
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			roll -= 2;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			roll += 2;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
			roll = 0;
		}
		
		if (escapeMode == false) {
			yaw   += Mouse.getDX() / 8f / wohr;
			pitch -= Mouse.getDY() / 8f;
			if (pitch > 90) {
				pitch = 90;
			}
			if (pitch < -90) {
				pitch = -90;
			}
		}
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public void setRoll(float roll) {
		this.roll = roll;
	}
	
	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
}
