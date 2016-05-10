package de.codesourcery.infinimaze;

import de.codesourcery.infinimaze.Chunk.ChunkKey;

public class Camera 
{
	// chunk the camera is in
	public final ChunkKey currentChunk;
	
	// tile coordinates within the current
	// chunk [ (0,0) is a chunk's center tile ]
	// the camera is currently in
	public float cameraX; 
	public float cameraY;
	
	public float zoom = 1f;
	
	public boolean changed = true;
	
	public Camera(ChunkKey currentChunk) {
		this.currentChunk = currentChunk.copy();
	}
	
	@Override
	public String toString() {
		return "Chunk "+currentChunk+" , tile "+cameraX+","+cameraY;
	}

	public void translate(float dx, float dy) 
	{
		cameraX += dx;
		if ( cameraX > Chunk.WIDTH/2f ) {
			currentChunk.x++;
			cameraX -= Chunk.WIDTH;
		} else if ( cameraX < -Chunk.WIDTH/2f ) {
			currentChunk.x--;
			cameraX += Chunk.WIDTH;
		}
		cameraY += dy;
		if ( cameraY > Chunk.HEIGHT/2f ) {
			currentChunk.y++;
			cameraY -= Chunk.HEIGHT;
		} else if ( cameraY < -Chunk.HEIGHT/2f ) {
			currentChunk.y--;
			cameraY += Chunk.HEIGHT;
		}		
		changed =true;
	}
	
	public void zoom(float delta) {
		final float newZoom = zoom + delta;
		if ( newZoom > 0 ) {
			zoom = newZoom;
			changed = true;
		}
	}
}