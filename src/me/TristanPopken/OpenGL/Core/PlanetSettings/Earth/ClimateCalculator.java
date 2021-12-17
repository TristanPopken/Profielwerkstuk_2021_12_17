package me.TristanPopken.OpenGL.Core.PlanetSettings.Earth;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.GameEngine.tools.Maths;
import me.TristanPopken.OpenGL.GameEngine.tools.OpenSimplexNoise;

public class ClimateCalculator {
	
	private static OpenSimplexNoise noise = new OpenSimplexNoise(3);
	
	//Ice Tundra Boreal Decidious Rain Desert
	private static final int[] ClimateAngles = {0, 14, 26, 50, 73, 90};
	
	private static float[] tempAtAngle = new float[901];
	
	static {
		for (int A = 0; A <= 900; A++) {
			float a = A/10f;
			int largestLower = 0;
			int largestLowerIndex = 0;
			int lowestLarger = 90;
			int lowestLargerIndex = ClimateAngles.length-1;
			for (int i = 0; i < ClimateAngles.length; i++) {
				int angle = ClimateAngles[i];
				if (angle <= a && angle > largestLower) {
					largestLower = angle;
					largestLowerIndex = i;
				}
				if (angle >= a && angle < lowestLarger) {
					lowestLarger = angle;
					lowestLargerIndex = i;
				}
			}
			if (largestLowerIndex == lowestLargerIndex) { //This removes the dividing by 0 bug
				tempAtAngle[A] = largestLowerIndex * 10;
			} else {
				float delta = lowestLarger - largestLower;
				float value = (a - largestLower) / delta; //value between 0 and 1 (0 when closest to lower angle)
				tempAtAngle[A] = ((1-value)*largestLowerIndex + value*lowestLargerIndex)*10;
				//System.out.println("value: "+value+" l: "+largestLowerIndex+" L: "+lowestLargerIndex);
			}
		}
	}
	
	public static float getTemp(Vector3f coord, float height) {//6 degrees per km, 1 unit is 1 km (planet is 637km scale 1:10)
		height = Math.max(height-1, 0);
		Vector3f nCoord = new Vector3f(coord); nCoord.normalise();
		float dot = Vector3f.dot(nCoord, new Vector3f(0,1,0)); //1 at north pole, 0 at equator, -1 at south pole
		float angle = (1 - Math.abs(dot)) * 90; //0 degrees at poles, 90 degrees at equator
		float temp = tempAtAngle[Math.round(angle*10)] - height * 6 + 4f;
		float extraTemperature = Maths.getNoiseHeight(noise, nCoord, 1, 3) * 10;
		return Math.min(Math.max(temp+extraTemperature, 0),50);
	}
	
	private static final float delta = 0.3f;
	private static final float switchSpeed = 5f;
	public static float getRockiness(Vector3f coord, Vector3f normal, float temperature) {
		//https://www.desmos.com/calculator/u3p933n0xk?lang=nl
		Vector3f unitCoord  = new Vector3f(coord);  unitCoord.normalise();
		Vector3f unitNormal = new Vector3f(normal); unitNormal.normalise();
		float tempAddOn = temperature / 200f; //ranges from 0 to 0.25
		float dot = Math.abs(Vector3f.dot(unitCoord, unitNormal));//1 when both point up, 0 when perpendicular
		float x = 1 - dot + tempAddOn;
		return Maths.Clamp01(0.5f + switchSpeed * (x - delta));
	}
	
}
