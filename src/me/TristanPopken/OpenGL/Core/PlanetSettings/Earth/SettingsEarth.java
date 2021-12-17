package me.TristanPopken.OpenGL.Core.PlanetSettings.Earth;

import me.TristanPopken.OpenGL.Core.PlanetSettings.Parent;
import me.TristanPopken.OpenGL.Core.PlanetSettings.PlanetSettings;

public class SettingsEarth extends PlanetSettings {
	
	public SettingsEarth() {
		
		//---< Texture >---//
		
		textures = new String[]{};
		reflectivity = 0.4f;
		shineDamper = 30;//10
		
		
		//---< Position >---//
		
		parent = Parent.SUN;
		
		radius = 6371;
		
		res = 60;
		
		
	}
}
