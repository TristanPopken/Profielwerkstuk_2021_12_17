package me.TristanPopken.OpenGL.GameEngine.tools;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.PhysicsEngine.Craft;
import me.TristanPopken.OpenGL.Core.PhysicsEngine.mat4;
import me.TristanPopken.OpenGL.Core.PhysicsEngine.vec3;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Entities.Entity;

/* Java has its own Math library. However,
 * This class contains functions which are not available in it
 */

public class Maths {
	
	public static mat4 getRotationMatrix(Craft craft) {
		vec3 u = craft.getUp();
		vec3 z = craft.getForward();
		vec3 x = vec3.cross(u, z);
		vec3 y = vec3.cross(z, x);
		x = vec3.normalize(x);
		y = vec3.normalize(y);
		mat4 m = new mat4();
		m.setIdentity();
		m.m00 = z.getX(); m.m10 = z.getY(); m.m20 = z.getZ();
		m.m01 = y.getX(); m.m11 = y.getY(); m.m21 = y.getZ();
		m.m02 = x.getX(); m.m12 = x.getY(); m.m22 = x.getZ();
		return m;
	}
	
	public static mat4 getRotationMatrix(vec3 rotations) {
		double sx = Math.sin(rotations.getX()); double sy = Math.sin(rotations.getY()); double sz = Math.sin(rotations.getZ());
		double cx = Math.cos(rotations.getX()); double cy = Math.cos(rotations.getY()); double cz = Math.cos(rotations.getZ());
		mat4 m = new mat4();
		m.setIdentity();
		m.m00 = cy*cz; m.m10 = cz*sx*sy-cx*sz; m.m20 = cx*cz*sy+sx*sz;
		m.m01 = cy*sz; m.m11 = cx*cz+sx*sy*sz; m.m21 = cx*sy*sz-cz*sx;
		m.m02 =   -sy; m.m12 =          cy*sx; m.m22 =          cx*sy;
		
		return m;
	}
	
	public static Matrix4f createTransformationMatrix(Entity entity) {//Vector3f translation, float rx, float ry, float rz, float scale) {
		float scale = entity.getScale();
		
		Vector3f u = new Vector3f(entity.getUp());
		Vector3f z = new Vector3f(entity.getForward());           //forward
		Vector3f x = Vector3f.cross(u, z, null); x.normalise();   //right
		Vector3f y = Vector3f.cross(z, x, null); y.normalise();   //up
		
		Vector3f p = entity.getPosition();
		
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		
//		m.m00 = z.x; m.m10 = z.y; m.m20 = z.z; m.m30 = p.x;
//		m.m01 = y.x; m.m11 = y.y; m.m21 = y.z; m.m31 = p.y;
//		m.m02 = x.x; m.m12 = x.y; m.m22 = x.z; m.m32 = p.z;
		
		m.m00 = z.x; m.m10 = z.y; m.m20 = z.z; m.m30 = p.x;
		m.m01 = y.x; m.m11 = y.y; m.m21 = y.z; m.m31 = p.y;
		m.m02 = x.x; m.m12 = x.y; m.m22 = x.z; m.m32 = p.z;
		
		m.scale(new Vector3f(scale, scale, scale));
		return m;
	}
	
	public static Matrix4f createTransformationMatrixFromRotation(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		return matrix;
	}
	
	public static Vector3f rotateVectorAroundAxis(float angle, Vector3f v, Vector3f axis) {
		//https://stackoverflow.com/questions/6721544/circular-rotation-around-an-arbitrary-axis
		//https://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
		
		float a = (float) Math.toRadians(angle);
		float c = (float) Math.cos(a);
		float s = (float) Math.sin(a);
		
		Matrix3f r = new Matrix3f();
		r.m00 = c+axis.x*axis.x*(1-c);        r.m10 = axis.x*axis.y*(1-c)-axis.z*s; r.m20 = axis.x*axis.z*(1-c)+axis.y*s;
		r.m01 = axis.y*axis.x*(1-c)+axis.z*s; r.m11 = c+axis.y*axis.y*(1-c);        r.m21 = axis.y*axis.z*(1-c)-axis.x*s;
		r.m02 = axis.z*axis.x*(1-c)-axis.y*s; r.m12 = axis.z*axis.y*(1-c)+axis.x*s; r.m22 = c+axis.z*axis.z*(1-c);
		
		v = Matrix3f.transform(r, v, null);
		v.normalise();
		
		return v;
	}
	
//	public static vec3 rotateVector(vec3 vec, Rotation rotation) {
//		if (rotation.isValidRotation()) {
//			return rotateVectorAroundAxis(rotation.getAngularVelocity(), vec, rotation.getAxis());
//		} else {
//			return vec;
//		}
//	}
	
	public static vec3 rotateVectorAroundAxis(double angleRadii, vec3 vector, vec3 axis2) {
		
		//Otherwise you will get zero length vector error
		if (vector.length() < 0.0001d) return new vec3(0,0,0);
		
		Vector3f axis = axis2.getVector3f();
		Vector3f v = vector.getVector3f();
		
		float a = (float) angleRadii;
		float c = (float) Math.cos(a);
		float s = (float) Math.sin(a);
		
		Matrix3f r = new Matrix3f();
		r.m00 = c+axis.x*axis.x*(1-c);        r.m10 = axis.x*axis.y*(1-c)-axis.z*s; r.m20 = axis.x*axis.z*(1-c)+axis.y*s;
		r.m01 = axis.y*axis.x*(1-c)+axis.z*s; r.m11 = c+axis.y*axis.y*(1-c);        r.m21 = axis.y*axis.z*(1-c)-axis.x*s;
		r.m02 = axis.z*axis.x*(1-c)-axis.y*s; r.m12 = axis.z*axis.y*(1-c)+axis.x*s; r.m22 = c+axis.z*axis.z*(1-c);
		
		v = Matrix3f.transform(r, v, null);
		v.normalise();
		
		return new vec3(v);
	}
	
//	public static Matrix4f createViewMatrixOld(Camera camera) {
//		Matrix4f viewMatrix = new Matrix4f();
//		viewMatrix.setIdentity();
//		
//		Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
//		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
//		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
//		
//		Vector3f cameraPos = camera.getPosition();
//		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
//		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
//		return viewMatrix;
//	}
	
	public static float Smooth2fMin(float a, float b, float k) {
		float h = Math.max(Math.min( (a-b+k) / (2*k), 1), 0);
		return b*h + a*(1-h) - k*h*(1-h);
	}
	
	public static float Smooth2fMax(float a, float b, float k) {
		float h = Math.max(Math.min( (a-b-k) / (2*-k), 1), 0);
		return b*h + a*(1-h) + k*h*(1-h);
	}
	
	public static float distance(Vector3f a, Vector3f b) {
		float dx = a.getX() - b.getX();
		float dy = a.getY() - b.getY();
		float dz = a.getZ() - b.getZ();
		return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	public static float Clamp01(float value) {
		return (value>1?1:value)<0?0:value;
	}
	
	public static float getNoiseHeight(OpenSimplexNoise noise, Vector3f xyz, float startSize, int octaveCount) {
		float value = 0;
		float scale = 1;
		for (int i = 0; i < octaveCount; i++) {
			value += (float) noise.noise3D(xyz.x, xyz.y, xyz.z, startSize * scale) * scale;
			scale *= 0.5;
		}
		return value / 2f;
	}
	
	public static float noiseToMask(float mask, float switchFactor) {
		mask = Maths.Smooth2fMin(mask,  1/switchFactor, 1/switchFactor/2);
		mask = Maths.Smooth2fMax(mask, -1/switchFactor, 1/switchFactor/2);
		return 1 - (1/switchFactor + mask) * switchFactor/2f;
	}
	
	public static void invertVector(Vector3f a) {
		a.setX(a.x*-1);
		a.setY(a.y*-1);
		a.setZ(a.z*-1);
	}
	
}
