package me.TristanPopken.OpenGL.GameEngine.renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static long lastFullscreenChange = System.nanoTime();
	
	private static final int WIDTH = 1920;//1280;
	private static final int HEIGHT = 1080;//720;
	private static final int FPS_CAP = 1200;
	
	public static void createDisplay() {
		
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), attribs);
			//Display.create(new PixelFormat().withSamples(4).withDepthBits(4), attribs);
			Display.setTitle("Game Testing");
			Display.setResizable(true);
//			GL11.glEnable(GL13.GL_MULTISAMPLE);2
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		
	}
	
	public static void toggleFullscreen() {
		try {
			long newTime = System.nanoTime();
			if (newTime - lastFullscreenChange > 500000000) { //500ms
				lastFullscreenChange = newTime;
				if (Display.isFullscreen()) {
					Display.setFullscreen(false);
				} else {
					Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
				}
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateDisplay() {
		
		Display.sync(FPS_CAP);
		Display.update();
		
	}
	
	public static void closeDisplay() {
		
		Display.destroy();
		
	}
	
	public static void updateMouseVisability(boolean hideMouse) {
		if (Display.isFullscreen()) {
			if (hideMouse) {
				Mouse.setGrabbed(true);
			} else {
				Mouse.setGrabbed(false);
			}
		} else {
			Mouse.setGrabbed(false);
		}
	}
	
}
