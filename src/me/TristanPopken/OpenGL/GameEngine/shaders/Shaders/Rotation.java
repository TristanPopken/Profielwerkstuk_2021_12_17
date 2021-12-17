package me.TristanPopken.OpenGL.GameEngine.shaders.Shaders;

import me.TristanPopken.OpenGL.Core.PhysicsEngine.Force;
import me.TristanPopken.OpenGL.Core.PhysicsEngine.mat4;
import me.TristanPopken.OpenGL.Core.PhysicsEngine.vec3;

public class Rotation {
	
	private boolean validRotation = false;
	private vec3 axis;
	private double angularVelocity;
	
	public Rotation(Force force, vec3 inertia, double time) {
		
		vec3 origin = force.getOrigin();
		vec3 vector = force.getDirection();
		
		if (vector.length() < 0.0001) return;
		
		vec3 unitOrigin = vec3.normalize(origin);
		vec3 unitVector = vec3.normalize(vector);
		
		double factor = 1 - Math.abs(vec3.dot(unitOrigin, unitVector));
		if (factor < 0.0001) return; //Not a valid rotation
		
		axis = vec3.cross(unitOrigin, unitVector);
		axis = vec3.normalize(axis);
		
		double torque = vector.length() * factor * origin.length();
		double inertiaX = Math.abs(vec3.dot(axis, new vec3(1, 0, 0)));
		double inertiaY = Math.abs(vec3.dot(axis, new vec3(0, 1, 0)));
		double inertiaZ = Math.abs(vec3.dot(axis, new vec3(0, 0, 1)));
		double inertiaTotal = inertiaX + inertiaY + inertiaZ;
		double angularAcceleration = torque / inertiaTotal;
		
		angularVelocity = angularAcceleration * time;
		validRotation = true;
		
//		vec3 toCoM = vec3.normalize(origin).invert();
//		double factorX = 1 - vec3.abs(vec3.dot(new vec3(1, 0, 0), toCoM));
//		double factorY = 1 - vec3.abs(vec3.dot(new vec3(0, 1, 0), toCoM));
//		double factorZ = 1 - vec3.abs(vec3.dot(new vec3(0, 0, 1), toCoM));
//		
//		//Y axis force
//		double torqueX1 = vector.getY() * factorY * origin.getZ();
//		double torqueZ1 = vector.getY() * factorY * origin.getX();
//		//Z axis force
//		double torqueY1 = vector.getZ() * factorZ * origin.getZ();
//		double torqueZ2 = vector.getZ() * factorZ * origin.getY();
//		//X axis force
//		double torqueY2 = vector.getX() * factorX * origin.getX();
//		double torqueX2 = vector.getX() * factorX * origin.getY();
//		
//		vec3 torque = new vec3(torqueX1+torqueX2, torqueY1+torqueY2, torqueZ1+torqueZ2);
//		
//		vec3 angularAcceleration = vec3.divide(torque, inertia);
//		vec3 angularVelocity = craft.getAvel();
//		angularVelocity = vec3.add(angularVelocity, vec3.scale(angularAcceleration, time));
//		craft.setAvel(angularVelocity);
		
	}
	
	public mat4 getRotationMatrix(double time) {
		double a = angularVelocity * time;
		double c = Math.cos(a);
		double s = Math.sin(a);
		mat4 r = new mat4();
		r.setIdentity();
		if (validRotation) {
			r.m00 = c+axis.getX()*axis.getX()*(1-c);             r.m10 = axis.getX()*axis.getY()*(1-c)-axis.getZ()*s; r.m20 = axis.getX()*axis.getZ()*(1-c)+axis.getY()*s;
			r.m01 = axis.getY()*axis.getX()*(1-c)+axis.getZ()*s; r.m11 = c+axis.getY()*axis.getY()*(1-c);             r.m21 = axis.getY()*axis.getZ()*(1-c)-axis.getX()*s;
			r.m02 = axis.getZ()*axis.getX()*(1-c)-axis.getY()*s; r.m12 = axis.getZ()*axis.getY()*(1-c)+axis.getX()*s; r.m22 = c+axis.getZ()*axis.getZ()*(1-c);
		}
		return r;
	}
	
	public boolean isValidRotation() {
		return validRotation;
	}
	
	public vec3 getAxis() {
		return axis;
	}
	
	public void setAxis(vec3 axis) {
		this.axis = axis;
	}
	
	public double getAngularVelocity() {
		return angularVelocity;
	}
	
	public void setAngularVelocity(double angularVelocity) {
		this.angularVelocity = angularVelocity;
	}
	
}
