package me.TristanPopken.OpenGL.Core.PhysicsEngine;

import java.util.ArrayList;

import me.TristanPopken.OpenGL.GameEngine.shaders.Shaders.Rotation;
import me.TristanPopken.OpenGL.GameEngine.tools.Maths;

public class Physics {
	
	private final static vec3 up = new vec3(0, 1, 0);
	private final static vec3 forward = new vec3(1, 0, 0);
	
	private final static double G = (float) 6.674E-11;
	private final static double M = (float) 5.972E24;
	
	Craft shuttle;
	
	ArrayList<Rotation> rotations = new ArrayList<Rotation>();
	
	public Physics() {
		shuttle = new Craft();
	}
	
	public void update(double time, boolean message) {
		updateCraft(shuttle, time, message);
	}
	
	private void updateCraft(Craft craft, double time, boolean message) {
		
		//Calculate Matrices for transforamtion between world and object space
		mat4 toObjectSpace = Maths.getRotationMatrix(craft);
		mat4 toWorldSpace = new mat4(toObjectSpace);
		toWorldSpace.invert();
		
		//Delta velocity in object space, should start at 0. Forces apply delta v. Also clear old rotations
		craft.setFrameObjectDeltaVel(new vec3());
		rotations.clear();
		
		//Airspeed is the inverse of velocity. As the planet is not rotating and there is no wind
		vec3 airspeed = new vec3(craft.getVel()).invert();
		airspeed = toObjectSpace.multiply(airspeed);
		
		//-----< Apply Forces >-----//
		
		double altitude = craft.getLoc().scale(0.0001).length() - 637;
		double airdensity = 1.225 * Math.pow(0.9, 10 * altitude / 2f);//The atmosphere is 2 times as thick
		
		Force Fd = craft.getDragForce(airspeed, airdensity);   //Fdrag
		Force Fw = craft.getWingForce(airspeed, airdensity);   //Flift,wing
		Force Fl = craft.getTailForce(airspeed, airdensity);   //Flift,tail
		Force Fwr = craft.getWingRudderForce(airspeed, airdensity);
		Force Flr = craft.getTailRudderForce(airspeed, airdensity);
		Force Fp = craft.getEngineForce();                     //Fpropulsion
		
		if (airspeed.length() > 0.0001d) {
			applyForceToCraft(craft, Fd, time);
			applyForceToCraft(craft, Fw, time);
			applyForceToCraft(craft, Fl, time);
			applyForceToCraft(craft, Fwr, time);
			applyForceToCraft(craft, Flr, time);
			applyForceToCraft(craft, Fp, time);
		}
		
		applyGravityToCraft(craft, time);
		
		//-----< Apply rotation >-----//
		
		vec3 objectForward = new vec3(forward);
		vec3 objectUp = new vec3(up);
		
		mat4 rm = craft.getArm();
		
		for (Rotation rotation : rotations) {
			mat4 drm = rotation.getRotationMatrix(0.00000000001d);//No clue why this needs to be scaled down so much but it works
			rm.multiply(drm);
		}
		
		objectForward = rm.multiply(objectForward);
		objectUp      = rm.multiply(objectUp);
		
		craft.setArm(rm);
		
		vec3 worldSpaceForward = toWorldSpace.multiply(objectForward);
		vec3 worldSpaceUp      = toWorldSpace.multiply(objectUp);
		
		craft.setForward(worldSpaceForward);
		craft.setUp(worldSpaceUp);
		
		//-----< Updating Velocity and location >-----//
		
		vec3 dv = craft.getFrameObjectDeltaVel();
		dv = toWorldSpace.multiply(dv);
		
		vec3 vel = craft.getVel();
		     vel = vec3.add(vel, dv);
		vec3 loc = craft.getLoc();
		     loc = vec3.add(loc, vel.scale(time));
		craft.setVel(vel);
		craft.setLoc(loc);
		
	}
	
	private void applyGravityToCraft(Craft craft, double time) {
		vec3 forcedirection = vec3.normalize(craft.getLoc()).scale(-1);
		double r = craft.getLoc().length();
		double a = G * M / (r * r); //a = F / m and F = GMm/r^2 so a = GM/r^2
		double dv = a * time;
		vec3 dvel = vec3.scale(forcedirection, dv);
		vec3 v = craft.getVel();
		v = vec3.add(v, dvel);
		craft.setVel(v);
	}
	
	private void applyForceToCraft(Craft craft, Force force, double time) {
		
		vec3 inertia = craft.getInertia();
		vec3 vector = force.getDirection();
		
		rotations.add(new Rotation(force, inertia, time));
		
		vec3 acceleration = vec3.divide(vector, craft.getMass());
		vec3 velocity = craft.getFrameObjectDeltaVel();
		velocity = vec3.add(velocity, vec3.scale(acceleration, time));
		craft.setFrameObjectDeltaVel(velocity);
		
	}
	
	public vec3 getCraftLocation() {
		return shuttle.getLoc().scale(0.0001);
	}
	
	public vec3 getCraftForward() {
		return shuttle.getForward();
	}
	
	public vec3 getCraftUp() {
		return shuttle.getUp();
	}
	
}

//-----< Converting to object space >-----//

//vec3 worldSpaceForward = craft.getForward();
//vec3 worldSpaceUp = craft.getUp();
//vec3 airspeed = new vec3(craft.getVel()).invert(); //Because there is no wind lol (And the planet isnt really rotating...)
//
////Gives a rotation so that the up vector points up in object space
//vec3 rotationAxis1 = vec3.cross(worldSpaceForward, worldSpaceUp);
//rotationAxis1 = vec3.normalize(rotationAxis1);
//double rotationAmount1 = vec3.angleRadians(worldSpaceUp, up);
//
//vec3 currentForward = Maths.rotateVectorAroundAxis(rotationAmount1, worldSpaceForward, rotationAxis1);
//vec3 currentUp      = Maths.rotateVectorAroundAxis(rotationAmount1, worldSpaceUp, rotationAxis1);
//
//airspeed = Maths.rotateVectorAroundAxis(rotationAmount1, airspeed, rotationAxis1);
//
////Gives a rotation so that the forward, this is not currently used (Except for the airspeed) because we already
////know what the forward and up vectors are. This is used for the transformation back to world space
//vec3 rotationAxis2 = new vec3(up);
//double rotationAmount2 = vec3.angleRadians(currentForward, forward);
//
//currentUp = Maths.rotateVectorAroundAxis(rotationAmount2, currentUp, rotationAxis2);
////if (message) currentUp.systemlog("UP");
//
//airspeed = Maths.rotateVectorAroundAxis(rotationAmount2, airspeed, rotationAxis2);

//-----< Converting to world space >-----//

//objectForward = Maths.rotateVectorAroundAxis(-rotationAmount2, objectForward, rotationAxis2);
//objectUp      = Maths.rotateVectorAroundAxis(-rotationAmount2, objectUp     , rotationAxis2);
//dv            = Maths.rotateVectorAroundAxis(-rotationAmount2, dv           , rotationAxis2);
//
//worldSpaceForward = Maths.rotateVectorAroundAxis(-rotationAmount1, objectForward, rotationAxis1);
//worldSpaceUp      = Maths.rotateVectorAroundAxis(-rotationAmount1, objectUp     , rotationAxis1);
//dv                = Maths.rotateVectorAroundAxis(-rotationAmount1, dv           , rotationAxis1);
//
//worldSpaceForward = vec3.normalize(worldSpaceForward);
//worldSpaceUp      = vec3.normalize(worldSpaceUp);
//
//craft.setForward(worldSpaceForward);
//craft.setUp(worldSpaceUp);


//objectForward = Maths.rotateVectorAroundAxis(rotations.getX() * time, objectForward, XAxis);
//objectForward = Maths.rotateVectorAroundAxis(rotations.getY() * time, objectForward, YAxis);
//objectForward = Maths.rotateVectorAroundAxis(rotations.getZ() * time, objectForward, ZAxis);

//objectUp = Maths.rotateVectorAroundAxis(rotations.getX() * time, objectUp, XAxis);
//objectUp = Maths.rotateVectorAroundAxis(rotations.getY() * time, objectUp, YAxis);
//objectUp = Maths.rotateVectorAroundAxis(rotations.getZ() * time, objectUp, ZAxis);
