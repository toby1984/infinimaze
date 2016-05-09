package de.codesourcery.infinimaze;

public class World 
{
	public final Camera camera;
	
	public World(Camera camera) {
		if (camera == null) {
			throw new IllegalArgumentException("camera must not be NULL");
		}
		this.camera = camera;
	}
}
