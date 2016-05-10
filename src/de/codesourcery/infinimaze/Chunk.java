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
	public static final int WIDTH=31; // needs to be an ODD number so that tile (0,0) is the center tile
	public static final int HEIGHT=31; // needs to be an ODD number so that tile (0,0) is the center tile
	public static final int HALF_WIDTH=WIDTH/2; 
	public static final int HALF_HEIGHT=HEIGHT/2; 
	
	public static enum Flag 
	{
		DIRTY(1);
		
		private final int bitMask;
		
		private Flag(int bitMask) {
			this.bitMask = bitMask;
		}
		public final boolean isSet(int value) {
			return (value & bitMask) != 0;
		}
		
		public final int set(int value) {
			return (value | bitMask);
		}	
		
		public final int clear(int value) {
			return (value & ~bitMask);
		}		
	}
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
	public int flags;
	
	public static final class TileArray 
	{
		public Tile[] tiles;
		
		public TileArray() {
			tiles = new Tile[ WIDTH*HEIGHT ];
			Arrays.fill( tiles ,  Tile.ROCK );
		}
		
		public TileArray(TileArray other) 
		{
			tiles = new Tile[ WIDTH*HEIGHT ];
			populateFrom(other);
		}		
		
		public void populateFrom(TileArray other) 
		{
			System.arraycopy( other.tiles , 0 , this.tiles , 0 , other.tiles.length );
		}
		
		public Tile getTile(int x,int y) 
		{
			return tiles[ptr(x,y)];
		}
		
		public TileArray createCopy() {
			return new TileArray(this);
		}
		
		public static int ptr(int x,int y) {
			return (y+HALF_HEIGHT)*WIDTH+(x+HALF_WIDTH);
		}
		
		public void setTile(int x,int y,Tile t) 
		{
			if ( t == null ) {
				throw new IllegalArgumentException("tile must not be NULL");
			}
			tiles[ptr(x,y)] = t;
		}	
	}
	
	public TileArray tiles = new TileArray();
	
	public Chunk(ChunkKey key)
	{
		if (key == null) {
			throw new IllegalArgumentException("key must not be NULL");
		}
		this.key = key;
	}
	
	public Tile getTile(int x,int y) 
	{
		return tiles.getTile(x,y);
	}
	
	public void setTile(int x,int y,Tile t) 
	{
		tiles.setTile(x, y, t);
	}	
	
	@Override
	public String toString() {
		return "Chunk "+key;
	}
	
	public boolean isSet(Flag flag) {
		return flag.isSet( this.flags );
	}
	
	public void set(Flag flag) {
		this.flags = flag.set( this.flags );
	}	
	
	public void clear(Flag flag) {
		this.flags = flag.clear( this.flags );
	}	
	
	public boolean isDirty() {
		return isSet(Flag.DIRTY);
	}
	
	public void markDirty() 
	{
		set(Flag.DIRTY);
	}
	
	public void clearDirty() 
	{
		clear(Flag.DIRTY);
	}	
}