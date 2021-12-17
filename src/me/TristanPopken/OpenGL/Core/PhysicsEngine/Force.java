package me.TristanPopken.OpenGL.Core.PhysicsEngine;

public class Force {
	
	private vec3 origin;
	private vec3 direction;
	
	public Force(vec3 origin, vec3 direction) {
		this.origin = origin;
		this.direction = direction;
	}
	
	public void setOrigin(vec3 origin) {
		this.origin = origin;
	}
	
	public void setDirection(vec3 direction) {
		this.direction = direction;
	}
	
	public vec3 getOrigin() {
		return origin;
	}
	
	public vec3 getDirection() {
		return direction;
	}
	
}
