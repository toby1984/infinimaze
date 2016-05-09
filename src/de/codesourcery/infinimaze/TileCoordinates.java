package de.codesourcery.infinimaze;

import de.codesourcery.infinimaze.Chunk.ChunkKey;

public class TileCoordinates 
{
	public ChunkKey key;
	public int tileX;
	public int tileY;
	
	public TileCoordinates() {
		key = new ChunkKey(0,0);
	}

	public TileCoordinates(ChunkKey key, int tileX, int tileY) 
	{
		if ( key == null ) {
			throw new IllegalArgumentException("key must not be NULL");
		}
		this.key = key;
		this.tileX = tileX;
		this.tileY = tileY;
	}
}
