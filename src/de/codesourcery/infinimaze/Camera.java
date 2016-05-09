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
		if ( cameraX > Chunk.CHUNK_WIDTH/2f ) {
			currentChunk.x++;
			cameraX -= Chunk.CHUNK_WIDTH;
		} else if ( cameraX < -Chunk.CHUNK_WIDTH/2f ) {
			currentChunk.x--;
			cameraX += Chunk.CHUNK_WIDTH;
		}
		cameraY += dy;
		if ( cameraY > Chunk.CHUNK_HEIGHT/2f ) {
			currentChunk.y++;
			cameraY -= Chunk.CHUNK_HEIGHT;
		} else if ( cameraY < -Chunk.CHUNK_HEIGHT/2f ) {
			currentChunk.y--;
			cameraY += Chunk.CHUNK_HEIGHT;
		}		
		System.out.println("Camera: "+this);
		changed =true;
	}
}