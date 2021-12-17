package me.TristanPopken.OpenGL.Core.PhysicsEngine;

import org.lwjgl.input.Keyboard;

import me.TristanPopken.OpenGL.GameEngine.Camera;

public class Craft {
	
	private final static double PI = Math.PI;
	//ObjectSpace
	private final static vec3 objectUp = new vec3(0, 1, 0);
	private final static vec3 objectForward = new vec3(1, 0, 0);
	private final static double standardForceFactor = 1;
	private final static double rcsStrength = 10000;
	
	//Data, center of mass is the origin of the object in object space
	final private vec3 inertia = new vec3(1.29, 9.68, 10.1).scale(1000000); //x Axis is forward in this object space
	final double mass = 78E3; //The space shuttle is 78 Tons, however, This is the dry mass;
	
	//Location and Velocity in WORLD SPACE!
	private vec3 loc;
	private vec3 vel;
	
	//Vehicle forward and up vectors in WORLD SPACE!
	private vec3 forward;
	private vec3 up;
	
	//delta angular velocity rotation matrix
	private mat4 arm;
	
	//To store delta velocity from all froces
	private vec3 frameObjectDeltaVel = new vec3();
	
	public Craft() {
		
		//Space Shuttle
//		loc = new vec3(0, 6580000, 0);
//		vel = new vec3();
		loc = new vec3(0, 6878140, 0);
		vel = new vec3(7612.6070688109, 0, 0);
		
		forward = new vec3(-1, 0, 0);
		up = new vec3(0, 1, 0);
		arm = new mat4();
		arm.setIdentity();
	}
	
	//-----< Forces are in object space >-----//
	//Up force, also includes lift from body itself
	public Force getWingForce(vec3 airspeed, double airdensity) {
		//https://sciencing.com/calculate-lift-coefficient-7463249.html
		
		//Origin of force is estimated by online center of mass and center of lift
		//Not that this changes when the mass lowers from fuel usage and cargo deployment
		vec3 origin = new vec3(-0.9, 0, 0);//-0.9 -1.4 0
		vec3 direction = new vec3(objectUp);
		
		double A = 380;                                                      //Estimated by counting 4m^2 squares from blueprint
		double angle = vec3.dot(vec3.normalize(airspeed), direction) * PI;   //Between 1 and -1, if you point down, the force is negative
		double Cd = 2 * PI * angle;
		double F = 0.5 * airdensity * vel.length() * vel.length() * Cd * A;
		
		return new Force(origin, direction.scale(F*standardForceFactor));
	}
	
	public Force getWingRudderForce(vec3 airspeed, double airdensity) {
		
		vec3 origin = new vec3(-1.2, -1.4, 0);//-0.9 -1.4 0
		vec3 direction = new vec3();
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			direction = new vec3(0,-1, 0);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			direction = new vec3(0, 1, 0);
		}
		double A = 30;
		double angle = vec3.dot(vec3.normalize(airspeed), objectForward) * PI;
		double Cd = 2 * PI * angle;
		double F = 0.5 * airdensity * vel.length() * vel.length() * Cd * A;
		
		return new Force(origin, direction.scale(F+rcsStrength));
	}
	
	//Side force, also includes lift from body itself
	public Force getTailForce(vec3 airspeed, double airdensity) {
		
		vec3 origin = new vec3(-1.2, 0, 0);//-1.2 0.3 0
		vec3 direction = new vec3(0, 0, 1);
		
		double A = 212;
		double angle = vec3.dot(vec3.normalize(airspeed), direction) * PI;
		double Cd = 2 * PI * angle;
		double F = 0.5 * airdensity * vel.length() * vel.length() * Cd * A;
		
		return new Force(origin, direction.scale(F*standardForceFactor));
	}
	
	public Force getTailRudderForce(vec3 airspeed, double airdensity) {
		
		vec3 origin = new vec3(-2, 0.6, 0);//-0.9 -1.4 0
		vec3 direction = new vec3();
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			direction = new vec3(0, 0, 1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			direction = new vec3(0, 0,-1);
		}
		double A = 0.5;
		double angle = vec3.dot(vec3.normalize(airspeed), objectForward) * PI;
		double Cd = 2 * PI * angle;
		double F = 0.5 * airdensity * vel.length() * vel.length() * Cd * A;
		
		return new Force(origin, direction.scale(F+rcsStrength));
	}
	
	public Force getDragForce(vec3 airspeed, double airdensity) { //Front force, Mainly the front, but a bit from the wings and tail aswell
		
		vec3 origin = new vec3(5, 0, 0);//5 0.1 0
		vec3 direction = new vec3(-1, 0, 0);
		
		double A = 72;
		double angle = vec3.dot(vec3.normalize(airspeed), direction) * PI;
		double Cd = 2 * PI * angle;
		double F = 0.5 * airdensity * vel.length() * vel.length() * Cd * A;
		return new Force(origin, direction.scale(F*standardForceFactor));
	}
	
	public Force getEngineForce() {
		vec3 origin = new vec3(-3, 0.0, 0);
		vec3 direction = new vec3(objectForward);
		double F = Math.max(0, Camera.engine) * 50000000d;
		return new Force(origin, direction.scale(F));
	}
	
	public vec3 getInertia() {
		return inertia;
	}
	
	public double getMass() {
		return this.mass;
	}
	
	public vec3 getLoc() {
		return loc;
	}
	
	public void setLoc(vec3 loc) {
		this.loc = loc;
	}
	
	public vec3 getVel() {
		return vel;
	}
	
	public void setVel(vec3 vel) {
		this.vel = vel;
	}
	
	public vec3 getForward() {
		return forward;
	}
	
	public void setForward(vec3 forward) {
		this.forward = forward;
	}
	
	public vec3 getUp() {
		return up;
	}
	
	public void setUp(vec3 up) {
		this.up = up;
	}
	
	public mat4 getArm() {
		return arm;
	}
	
	public void setArm(mat4 arm) {
		this.arm = arm;
	}
	
	public vec3 getFrameObjectDeltaVel() {
		return frameObjectDeltaVel;
	}

	public void setFrameObjectDeltaVel(vec3 frameObjectDeltaVel) {
		this.frameObjectDeltaVel = frameObjectDeltaVel;
	}
	
}
