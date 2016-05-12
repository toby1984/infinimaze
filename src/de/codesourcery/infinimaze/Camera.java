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
	
	public float zoom = 3f;
	
	public boolean changed = true;
	
	public Camera(ChunkKey currentChunk) {
		this.currentChunk = currentChunk.copy();
	}
	
	@Override
	public String toString() {
		return "Chunk "+currentChunk+" , tile "+cameraX+","+cameraY;
	}

	public boolean translate(float dx, float dy,IChunkProvider provider) 
	{
		float newX = cameraX;
		float newY = cameraY;
		int chunkX = currentChunk.x;
		int chunkY = currentChunk.y;
		
		newX += dx;
		if ( newX > Chunk.WIDTH/2f ) {
			chunkX++;
			newX -= Chunk.WIDTH;
		} else if ( newX < -Chunk.WIDTH/2f ) {
			chunkX--;
			newX += Chunk.WIDTH;
		}
		newY += dy;
		if ( newY > Chunk.HEIGHT/2f ) {
			chunkY++;
			newY -= Chunk.HEIGHT;
		} else if ( newY < -Chunk.HEIGHT/2f ) {
			chunkY--;
			newY += Chunk.HEIGHT;
		}		
		
		final Chunk chunk = provider.getChunk( chunkX ,chunkY );
		
		if ( chunk.getTile( Math.round( newX ) , Math.round( newY ) ) == Tile.EMPTY ) {
			cameraX = newX;
			cameraY = newY;
			currentChunk.x = chunkX;
			currentChunk.y = chunkY;
			
			changed =true;
			return true;
		}
		return false;
	}
	
	public void zoom(float delta) {
		final float newZoom = zoom + delta;
		if ( newZoom > 0 ) {
			zoom = newZoom;
			changed = true;
		}
	}
}