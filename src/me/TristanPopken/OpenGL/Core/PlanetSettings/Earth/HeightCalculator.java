package me.TristanPopken.OpenGL.Core.PlanetSettings.Earth;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.GameEngine.tools.Maths;
import me.TristanPopken.OpenGL.GameEngine.tools.OpenSimplexNoise;

public class HeightCalculator {
	
	private float terrainHeight;
	
	public float getTerrainHeight() {
		return this.terrainHeight;
	}
	
	private OpenSimplexNoise tnoise = new OpenSimplexNoise(2);
	private OpenSimplexNoise bnoise = new OpenSimplexNoise(3);
	
	public Vector3f Generate(Vector3f xyzsmall, float scale) {
		
		Vector3f xyz = new Vector3f(xyzsmall.x * 300, xyzsmall.y * 300, xyzsmall.z * 300);
		
		float mask = Maths.getNoiseHeight(bnoise, xyz, 200, 15);//7 octaves
		mask = Maths.noiseToMask(mask, 10);
		
		float height = biomeLand(xyz, mask) + biomeOcean(xyz, (1-mask));
		if (height < 0) {
			height /= 3f;
		}
		terrainHeight = height;
		
		float noiseX = height * xyzsmall.x;
		float noiseY = height * xyzsmall.y;
		float noiseZ = height * xyzsmall.z;
		return new Vector3f(noiseX, noiseY, noiseZ);
	}
	
	private float biomeOcean(Vector3f xyz, float factor) {
		return -3.7f * factor; //average ocean depth
	}
	
	private float biomeLand(Vector3f xyz, float factor) {
		
		float mask1 = (float) Math.min(Math.max(1 - Math.abs( bnoise.noise3D(xyz.x, xyz.y, xyz.z, 150)) * 4, 0), 1);
		mask1 *=      (float) Math.min(Math.max(1 - Math.abs( bnoise.noise3D(xyz.x, xyz.y, xyz.z, 25)) * 1, 0), 1);
		mask1 *=      (float) Math.min(Math.max(1 - Math.abs( bnoise.noise3D(xyz.x, xyz.y, xyz.z, 10)) * 2, 0), 1);
		mask1 += Maths.getNoiseHeight(bnoise, xyz, 18, 4) / 4.0;//4 octaves
		float mask2 = (float) Math.min(Math.max(1 - Math.abs( bnoise.noise3D(xyz.x, xyz.y, xyz.z, 82)) * 5, 0), 1);
		mask2 *=      (float) Math.min(Math.max(1 - Math.abs( bnoise.noise3D(xyz.x, xyz.y, xyz.z, 31)) * 1, 0), 1);
		mask2 += Maths.getNoiseHeight(bnoise, xyz, 15, 2) / 8.0;//4 octaves
		mask2 *= 0.6;
		float mask = Math.max(mask1, mask2);
		
//		mask = Maths.noiseToMask(mask, 100);
		mask = Maths.Smooth2fMin(mask,  1f, 0.05f);
		mask = Maths.Clamp01(mask);
		
		float Mheight = biomeMountain(xyz,    mask );
		float Pheight = biomePlains  (xyz, (1-mask));
		
		return (Mheight+Pheight) * factor;
	}
	
	private float biomeMountain(Vector3f xyz, float factor) {
		float height = Math.abs(Maths.getNoiseHeight(tnoise, xyz, 12, 9));//6 octaves
		return (height*3+0.3f) * factor;
	}
	
	private float biomePlains(Vector3f xyz, float factor) {
		
		float height = Maths.getNoiseHeight(tnoise, xyz, 15, 5) + 1;//5 octaves
		float mask1 = Maths.getNoiseHeight(tnoise, xyz, 1, 3);
		float mask2 = Maths.getNoiseHeight(tnoise, xyz, 6, 2);
		float mask = Maths.noiseToMask(mask1, 10) * Maths.noiseToMask(mask2, 10);
		
		height += hillyPlains(xyz,   mask);
		height += flatPlains (xyz, 1-mask);
		
		return height;
	}
	
	private float hillyPlains(Vector3f xyz, float factor) {
		float height = Maths.getNoiseHeight(tnoise, xyz, 1.2f, 6) * 0.25f;
		return (height+0.4f) * factor;
	}
	
	private float flatPlains(Vector3f xyz, float factor) {
		float height = Maths.getNoiseHeight(tnoise, xyz, 0.8f, 3) * 0.15f;
		return (height+0.2f) * factor;
	}
	
}
