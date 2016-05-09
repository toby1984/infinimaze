package de.codesourcery.infinimaze;

import java.util.Arrays;

/**
 * Chunk of tiles.
 * 
 * Cartesian coordinates inside a chunk are based on the chunk's center tile which
 * is at (0,0) ; note that the Y axis points upwards (unlike screen coordinates where
 * the Y axis points downwards).
 * 
 * @author tgierke
 */
public class Chunk 
{
	public static final int CHUNK_WIDTH=31; // needs to be an ODD number so that tile (0,0) is the center tile
	public static final int CHUNK_HEIGHT=31; // needs to be an ODD number so that tile (0,0) is the center tile
	public static final int HALF_CHUNK_WIDTH=CHUNK_WIDTH/2; 
	public static final int HALF_CHUNK_HEIGHT=CHUNK_HEIGHT/2; 
	
	protected static final class ChunkKey 
	{
		public int x;
		public int y;
	
		public ChunkKey(int x, int y) 
		{
			this.x = x;
			this.y = y;
		}
		
		public ChunkKey(ChunkKey other) {
			this.x = other.x;
			this.y = other.y;
		}
		
		public ChunkKey copy() {
			return new ChunkKey(this);
		}

		@Override
		public int hashCode() 
		{
			return 31 * (31 + x) + y;
		}

		@Override
		public boolean equals(Object obj) 
		{
			if ( obj instanceof ChunkKey) {
				final ChunkKey other = (ChunkKey) obj;
				return this.x == other.x && this.y == other.y;
			}
			return false;
		}
		
		@Override
		public String toString() {
			return x+","+y;
		}
	}	
	
	public final ChunkKey key;
	
	public final Tile[] tiles;
	
	public Chunk(ChunkKey key)
	{
		if (key == null) {
			throw new IllegalArgumentException("key must not be NULL");
		}
		this.key = key;
		tiles = new Tile[ CHUNK_WIDTH*CHUNK_HEIGHT ];
		Arrays.fill( tiles ,  Tile.EMPTY );
	}
	
	public Tile getTile(int x,int y) 
	{
		return tiles[ptr(x,y)];
	}
	
	private static int ptr(int x,int y) {
		return (y+HALF_CHUNK_HEIGHT)*CHUNK_WIDTH+(x+HALF_CHUNK_WIDTH);
	}
	
	public void setTile(int x,int y,Tile t) 
	{
		if ( t == null ) {
			throw new IllegalArgumentException("tile must not be NULL");
		}
		tiles[ptr(x,y)] = t;
	}	
	
	@Override
	public String toString() {
		return "Chunk "+key;
	}
}