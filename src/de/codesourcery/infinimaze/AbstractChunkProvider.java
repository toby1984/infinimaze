package de.codesourcery.infinimaze;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.codesourcery.infinimaze.Chunk.ChunkKey;
import de.codesourcery.infinimaze.Chunk.TileArray;

public abstract class AbstractChunkProvider implements IChunkProvider 
{
	public static final boolean SERIALIZE_CHUNKS = false;
	
	private static final class ChunkMap 
	{
		private final Map<ChunkKey,Chunk> toUnload = new HashMap<>();
		private final Map<ChunkKey,Chunk> chunks = new HashMap<>();
		
		public void add(Chunk chunk) {
			chunks.put(chunk.key,chunk);
		}
		
		public Chunk get(ChunkKey key) 
		{
			Chunk result = chunks.get(key);
			if ( result == null ) {
				result = toUnload.get(key);
				if ( result != null ) {
					toUnload.remove( key );
					chunks.put( key , result );
				}
			}
			return null;
		}
		
		public void unload(Chunk chunk) 
		{
			if ( chunks.remove( chunk.key )  != null ) 
			{
				toUnload.put( chunk.key , chunk );
			}			
		}
	}
	
	private final ChunkMap chunks = new ChunkMap();
	
	private final IChunkStorage storage;
	
	public AbstractChunkProvider(IChunkStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public Chunk getChunk(ChunkKey key) 
	{
		Chunk result = chunks.get(key);
		if ( result == null ) {
			result = loadChunk( key);
			chunks.add( result );
		}
		return result;
	}
	
	private Chunk loadChunk(ChunkKey key) 
	{
		if ( SERIALIZE_CHUNKS ) 
		{
			Chunk chunk = null;
			try {
				chunk = storage.load( key );
			} 
			catch (IOException e) 
			{
				throw new RuntimeException(e);
			}
			
			if ( chunk != null ) {
				return chunk;
			}
		}
		return createChunk( key );
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkY) 
	{
		return getChunk( new ChunkKey(chunkX,chunkY) );
	}
	
	public class XSRandom extends Random 
	{
		private long seed;

		public XSRandom(long seed) {
			this.seed = seed;
		}

		protected int next(int nbits) {
			long x = seed;
			x ^= (x << 21);
			x ^= (x >>> 35);
			x ^= (x << 4);
			seed = x;
			x &= ((1L << nbits) - 1);
			return (int) x;
		}
	}	
	
	protected static enum Direction 
	{
		N(0,1),
		NE(1,1),
		E(1,0),
		SE(1,-1),
		S(0,-1),
		SW(-1,-1),
		W(-1,0),
		NW(-1,1);
		
		public final int dx;
		public final int dy;
		
		private Direction(int dx,int dy) {
			this.dx = dx;
			this.dy = dy;
		}
	}
	
	protected interface IntPredicate 
	{
		public boolean test(int x,int y);
	}
	
	protected static void countRockNeighbours(TileArray array,int[] result) 
	{
		countNeighbours( array , result , (nx,ny) -> array.getTile( nx , ny ) == Tile.ROCK );
	}
	
	protected static void countEmptyNeighbours(TileArray array,int[] result) 
	{
		countNeighbours( array , result , (nx,ny) -> array.getTile( nx , ny ) == Tile.EMPTY );
	}	
	
	protected static void countNeighbours(TileArray array,int[] result,IntPredicate predicate) 
	{
		final Direction[] dirs = Direction.values();
		final int len = dirs.length;
		
		for ( int y = -Chunk.HALF_HEIGHT ; y <= Chunk.HALF_HEIGHT ; y++ ) 
		{
			for ( int x = -Chunk.HALF_WIDTH ; x <= Chunk.HALF_WIDTH ; x++ ) 
			{
				int neighbours = 0;
				for ( int i = 0 ; i <len ; i++) 
				{
					final Direction dir = dirs[i];
					final int nx = x+ dir.dx;
					final int ny = y+ dir.dy;
					if ( nx >= -Chunk.HALF_WIDTH && ny >= -Chunk.HALF_HEIGHT && nx <= Chunk.HALF_WIDTH && ny <= Chunk.HALF_HEIGHT ) 
					{
						if ( predicate.test(nx,ny) ) 
						{
							neighbours++;
						}
					}
				}
				result[ TileArray.ptr(x,y) ] = neighbours;
			}
		}
	}
	
	protected abstract Chunk createChunk(ChunkKey key);
	
	@Override
	public void unload(Chunk chunk) 
	{
		chunks.unload(chunk);
	}
}