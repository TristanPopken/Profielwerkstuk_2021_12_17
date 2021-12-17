package me.TristanPopken.OpenGL.GameEngine;

import org.lwjgl.util.vector.Vector3f;

/*
 * This class is mainly used to send data between threads and objects
 */

public class GameEngineHandler {
	
	public static float time = 0;
	
	public static Vector3f pos = new Vector3f(0,0,0);
	public static Vector3f frw = new Vector3f(0,0,0);
	public static Vector3f up  = new Vector3f(0,0,0);
	
	public static final String path = "src/me/TristanPopken/OpenGL/GameEngine";
	
	public static boolean windowIsClosed = false;
	
}
