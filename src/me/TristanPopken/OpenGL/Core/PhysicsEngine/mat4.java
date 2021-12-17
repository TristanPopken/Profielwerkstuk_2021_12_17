package me.TristanPopken.OpenGL.Core.PhysicsEngine;

import org.lwjgl.util.vector.Matrix4f;

public class mat4 {
	
	public double m00 = 0; public double m10 = 0; public double m20 = 0; public double m30 = 0;
	public double m01 = 0; public double m11 = 0; public double m21 = 0; public double m31 = 0;
	public double m02 = 0; public double m12 = 0; public double m22 = 0; public double m32 = 0;
	public double m03 = 0; public double m13 = 0; public double m23 = 0; public double m33 = 0;
	
	public mat4() {
		
	}
	
	public mat4(mat4 m) {
		m00 = m.m00; m10 = m.m10; m20 = m.m20; m30 = m.m30;
		m01 = m.m01; m11 = m.m11; m21 = m.m21; m31 = m.m31;
		m02 = m.m02; m12 = m.m12; m22 = m.m22; m32 = m.m32;
		m03 = m.m03; m13 = m.m13; m23 = m.m23; m33 = m.m33;
	}
	
	public void setIdentity() {
		m00 = 1; m10 = 0; m20 = 0; m30 = 0;
		m01 = 0; m11 = 1; m21 = 0; m31 = 0;
		m02 = 0; m12 = 0; m22 = 1; m32 = 0;
		m03 = 0; m13 = 0; m23 = 0; m33 = 1;
	}
	
	public vec3 multiply(vec3 v) {
		double x = m00*v.getX() + m10*v.getY() + m20*v.getZ();
		double y = m01*v.getX() + m11*v.getY() + m21*v.getZ();
		double z = m02*v.getX() + m12*v.getY() + m22*v.getZ();
		return new vec3(x, y, z);
	}
	
	public void multiply(mat4 n) {
		Matrix4f a = this.getMatrix4f();
		Matrix4f b = n.getMatrix4f();
		Matrix4f m = Matrix4f.mul(a, b, null);
		m00 = m.m00; m10 = m.m10; m20 = m.m20; m30 = m.m30;
		m01 = m.m01; m11 = m.m11; m21 = m.m21; m31 = m.m31;
		m02 = m.m02; m12 = m.m12; m22 = m.m22; m32 = m.m32;
		m03 = m.m03; m13 = m.m13; m23 = m.m23; m33 = m.m33;
	}
	
	public Matrix4f getMatrix4f() {
		Matrix4f m = new Matrix4f();
		m.m00 = (float)m00;    m.m10 = (float)m10;    m.m20 = (float)m20;    m.m30 = (float)m30;
		m.m01 = (float)m01;    m.m11 = (float)m11;    m.m21 = (float)m21;    m.m31 = (float)m31;
		m.m02 = (float)m02;    m.m12 = (float)m12;    m.m22 = (float)m22;    m.m32 = (float)m32;
		m.m03 = (float)m03;    m.m13 = (float)m13;    m.m23 = (float)m23;    m.m33 = (float)m33;
		return m;
	}
	
	public void invert() {
		Matrix4f m = this.getMatrix4f();
		m.invert();
		m00 = m.m00; m10 = m.m10; m20 = m.m20; m30 = m.m30;
		m01 = m.m01; m11 = m.m11; m21 = m.m21; m31 = m.m31;
		m02 = m.m02; m12 = m.m12; m22 = m.m22; m32 = m.m32;
		m03 = m.m03; m13 = m.m13; m23 = m.m23; m33 = m.m33;
	}
	
	public void sysout(String name) {
		String empty = "";
		for (int i = 0; i < name.length(); i++) {
			empty = empty + " ";
		}
		System.out.println(name +" = ["+m00+", "+m10+", "+m20+"]");
		System.out.println(empty+" = ["+m01+", "+m11+", "+m21+"]");
		System.out.println(empty+" = ["+m02+", "+m12+", "+m22+"]");
	}
	
	
	
}
