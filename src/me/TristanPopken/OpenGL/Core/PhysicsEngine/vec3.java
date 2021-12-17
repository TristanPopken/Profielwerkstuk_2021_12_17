package me.TristanPopken.OpenGL.Core.PhysicsEngine;

import org.lwjgl.util.vector.Vector3f;

public class vec3 {
	
	private double x;
	private double y;
	private double z;
	
	public vec3() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public vec3(Vector3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public vec3(vec3 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	//---< Math >---//
	
	public static vec3 sub(vec3 a, vec3 b) {
		return new vec3(a.x-b.x, a.y-b.y, a.z-b.z);
	}
	
	public static vec3 add(vec3 a, vec3 b) {
		return new vec3(a.x+b.x, a.y+b.y, a.z+b.z);
	}
	
	public static vec3 add(vec3 a, vec3 b, vec3 c) {
		vec3 d = add(b, c);
		return add(a, d);
	}
	
	public static vec3 add(vec3 a, vec3 b, vec3 c, vec3 d) {
		vec3 e = add(a, b);
		vec3 f = add(c, d);
		return add(e, f);
	}
	
	public static vec3 scale(vec3 a, double b) {
		return new vec3(a.x*b, a.y*b, a.z*b);
	}
	
	public static double dot(vec3 a, vec3 b) {
		return a.x*b.x+a.y*b.y+a.z*b.z;
	}
	
	public static vec3 cross(vec3 a, vec3 b) {
		return new vec3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
	}
	
	public static double angleRadians(vec3 a, vec3 b) {
		return Math.acos((a.x*b.x+a.y*b.y+a.z*b.z) / (length(a) * length(b)));
	}
	
	public static double angleRadiansIgnoreY(vec3 a, vec3 b) {
		double lengthFactor = Math.sqrt(Math.pow(a.x,2)+Math.pow(a.z,2)) * Math.sqrt(Math.pow(b.x,2)+Math.pow(b.z,2));
		return Math.acos((a.x*b.x+a.z*b.z)/lengthFactor);
	}
	
	public static double angleRadiansIgnoreZ(vec3 a, vec3 b) {
		double lengthFactor = Math.sqrt(Math.pow(a.x,2)+Math.pow(a.y,2)) * Math.sqrt(Math.pow(b.x,2)+Math.pow(b.y,2));
		return Math.acos((a.x*b.x+a.y*b.y)/lengthFactor);
	}
	
	public static vec3 normalize(vec3 a) {
		double l = length(a);
		return scale(a, 1d/l);
	}
	
	public static double abs(double a) {
		return a<0?-a:a;
	}
	
	public static double length(vec3 a) {
		return Math.sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
	}
	
	public static vec3 multiply(vec3 a, vec3 b) {
		return new vec3(a.x*b.x, a.y*b.y, a.z*b.z);
	}
	
	public static vec3 multiply(vec3 a, vec3 b, vec3 c) {
		return new vec3(a.x*b.x*c.x, a.y*b.y*c.y, a.z*b.z*c.z);
	}
	
	public static vec3 divide(vec3 a, vec3 b) {
		return new vec3(a.x/b.x, a.y/b.y, a.z/b.z);
	}
	
	public static vec3 divide(vec3 a, double b) {
		return new vec3(a.x/b, a.y/b, a.z/b);
	}
	
	//---< Math that uses (And potentially changes) data from this object >---//
	
	public vec3 invert() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	
	public vec3 square() {
		x *= x;
		y *= y;
		z *= z;
		return this;
	}
	
	public vec3 getXcomponentAsVector() {
		return new vec3(x, 0, 0);
	}
	
	public vec3 getYcomponentAsVector() {
		return new vec3(0, y, 0);
	}
	
	public vec3 getZcomponentAsVector() {
		return new vec3(0, 0, z);
	}
	
	public double length() {
		return length(this);
	}
	
	public vec3 scale(double a) {
		return scale(this, a);
	}
	
	public Vector3f getVector3f() {
		return new Vector3f((float)x, (float)y, (float)z);
	}
	
	public Vector3f getVector3fInDegrees() {
		return new Vector3f((float)Math.toDegrees(x), (float)Math.toDegrees(y), (float)Math.toDegrees(z));
	}
	
	public void systemlog(String name) {
		double x = Math.round(this.x * 1000) / 1000d;
		double y = Math.round(this.y * 1000) / 1000d;
		double z = Math.round(this.z * 1000) / 1000d;
		System.out.println(name+" = ["+x+", "+y+", "+z+"]");
	}
	
}
