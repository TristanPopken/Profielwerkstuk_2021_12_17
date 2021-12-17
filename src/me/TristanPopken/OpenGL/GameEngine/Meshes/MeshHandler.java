package me.TristanPopken.OpenGL.GameEngine.Meshes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector3f;

import me.TristanPopken.OpenGL.Core.CelestialBodysMeshes.Data;
import me.TristanPopken.OpenGL.Core.PlanetSettings.PlanetSettings;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Planet.PlanetEntity;
import me.TristanPopken.OpenGL.GameEngine.Meshes.Planet.PlanetModel;
import me.TristanPopken.OpenGL.GameEngine.Meshes.models.RawModel;
import me.TristanPopken.OpenGL.GameEngine.Meshes.textures.ModelTexture;
import me.TristanPopken.OpenGL.GameEngine.renderEngine.Loader;

/* The function of this class is to connect different threads to the main thread
 * and allow them to add meshes to the game.
 * The Main threads checks every frame if there is a new mesh available to be loaded
*/

public class MeshHandler {
	
	private static int index = 0;
	
	public static int totalTriangleCount = 0;
	
	public static boolean isReadingProcessed = false;
	public static boolean readingArrayProcessed = false;//false is 1 true is 2
	
	public static boolean isReadingRemoved = false;
	public static boolean readingArrayRemoved = false;//false is 1 true is 2
	
	public static HashMap<Integer, PlanetEntity> loadedMeshes = new HashMap<Integer, PlanetEntity>();
	
	public static ArrayList<Entry<Integer, Data>> toBeProcessed1 = new ArrayList<Entry<Integer, Data>>();
	public static ArrayList<Entry<Integer, Data>> toBeProcessed2 = new ArrayList<Entry<Integer, Data>>();
	
	public static ArrayList<Integer> toBeRemoved1 = new ArrayList<Integer>();
	public static ArrayList<Integer> toBeRemoved2 = new ArrayList<Integer>();
	
	public static boolean isStoring = false;
	
	//----------------< Methods for Core Thread >----------------//
	
	public static int storePlanet(Data data) {
		isStoring = true;
		if (index > 2000000000) {
			index = 0;
		} else {
			index++;
		}
		Entry<Integer, Data> entry = Map.entry(index, data);
		if (isReadingProcessed) {
			if (readingArrayProcessed) {
				toBeProcessed2.add(entry);
			} else {
				toBeProcessed1.add(entry);
			}
		} else {
			toBeProcessed1.add(entry);
		}
		isStoring = false;
		return index;
	}
	
	public static void deletePlanet(int index) {
		if (isReadingRemoved) {
			if (readingArrayRemoved) {
				toBeRemoved2.add(index);
			} else {
				toBeRemoved1.add(index);
			}
		} else {
			toBeRemoved1.add(index);
		}
	}
	
	//----------------< Methods for Game Engine Thread >----------------//
	
	public static boolean RequiresUpdate() {
		return toBeProcessed1.size() > 0 || toBeProcessed2.size() > 0 || toBeRemoved1.size() > 0 || toBeRemoved2.size() > 0;
	}
	
	public static void update(PlanetSettings settings, Loader loader) {
		try {
		
		//----------------------------------< Reading Processed >----------------------------------//
		
		ModelTexture texture = new ModelTexture(loader.loadTextures(settings.textures));
		texture.setReflectivity(settings.reflectivity);
		texture.setShineDamper(settings.shineDamper);
		
		isReadingProcessed = true;
		//-------------< Read list 1>-------------//
		readingArrayProcessed = true;
		for (Entry<Integer, Data> entry : toBeProcessed1) {
			int index = entry.getKey();
			Data data = entry.getValue();
			RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
			PlanetModel planetmodel = new PlanetModel(model, texture);
			PlanetEntity entity = new PlanetEntity(planetmodel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1f, data.getMeshPosition());
			loadedMeshes.put(index, entity);
			totalTriangleCount += entity.getModel().getRawModel().getVertexCount() / 2;
		}
		toBeProcessed1.clear();
		//-------------< Read list 2>-------------//
		readingArrayProcessed = false;
		for (Entry<Integer, Data> entry : toBeProcessed2) {
			int index = entry.getKey();
			Data data = entry.getValue();
			RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
			PlanetModel planetmodel = new PlanetModel(model, texture);
			PlanetEntity entity = new PlanetEntity(planetmodel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1f, data.getMeshPosition());
			loadedMeshes.put(index, entity);
			totalTriangleCount += entity.getModel().getRawModel().getVertexCount() / 2;
		}
		toBeProcessed2.clear();
		isReadingProcessed = false;
		
		//----------------------------------< Reading Removed >----------------------------------//
		
		isReadingRemoved = true;
		//-------------< Read list 1>-------------//
		readingArrayRemoved = true;
		for (int index : toBeRemoved1) {
			PlanetEntity entity = loadedMeshes.get(index);
			if (entity != null) {
				loadedMeshes.remove(index);
				loader.unloadVao(entity.getModel().getRawModel().getVaoID());
				totalTriangleCount -= entity.getModel().getRawModel().getVertexCount() / 2;
			}
		}
		toBeRemoved1.clear();
		//-------------< Read list 2>-------------//
		readingArrayRemoved = false;
		for (int index : toBeRemoved2) {
			PlanetEntity entity = loadedMeshes.get(index);
			if (entity != null) {
				loadedMeshes.remove(index);
				loader.unloadVao(entity.getModel().getRawModel().getVaoID());
				totalTriangleCount -= entity.getModel().getRawModel().getVertexCount() / 2;
			}
		}
		toBeRemoved2.clear();
		isReadingRemoved = false;
		
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static Collection<PlanetEntity> getEntities() {
		return loadedMeshes.values();
	}
	
	public static void removeAllPlanets(Loader loader) {
		
		for (PlanetEntity mesh : loadedMeshes.values()) {
			loader.unloadVao(mesh.getModel().getRawModel().getVaoID());
		}
		loadedMeshes.clear();
		totalTriangleCount = 0;
	}
	
}
