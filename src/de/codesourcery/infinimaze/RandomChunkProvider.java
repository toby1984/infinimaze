package de.codesourcery.infinimaze;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.codesourcery.infinimaze.Chunk.ChunkKey;
import de.codesourcery.infinimaze.Chunk.TileArray;

public class RandomChunkProvider implements IChunkProvider 
{
	private final Map<ChunkKey,Chunk> chunks = new HashMap<>();
	
	@Override
	public Chunk getChunk(ChunkKey key) {
		Chunk result = chunks.get(key);
		if ( result == null ) {
			result = createChunk( key);
			chunks.put( key , result );
		}
		return result;
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
	
	private static enum Direction 
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
	
	private interface IntPredicate 
	{
		public boolean test(int x,int y);
	}
	
	private static void countRockNeighbours(TileArray array,int[] result) 
	{
		countNeighbours( array , result , (nx,ny) -> array.getTile( nx , ny ) == Tile.ROCK );
	}
	
	private static void countEmptyNeighbours(TileArray array,int[] result) 
	{
		countNeighbours( array , result , (nx,ny) -> array.getTile( nx , ny ) == Tile.EMPTY );
	}	
	
	private static void countNeighbours(TileArray array,int[] result,IntPredicate predicate) 
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
	private Chunk createChunk(ChunkKey key) 
	{
		final Chunk result = new Chunk(key);
		
		long sx = key.x;
		long sy = key.y;
		final long seed = (31+31*sx)*31+sy*31;
		
		final XSRandom rnd = new XSRandom(~seed*2);
		
		for ( int i = 0 , len = Chunk.WIDTH*Chunk.HEIGHT ; i < len ; i++ ) 
		{
			result.tiles.tiles[i] = rnd.nextBoolean() ? Tile.ROCK : Tile.EMPTY;
		}
		
		final int[] neighbours = new int[ Chunk.WIDTH*Chunk.HEIGHT];
		
		TileArray tmp2 = new TileArray();
		
		int generations = 0;
		int cellsChanged = 0;
		do
		{
			tmp2.populateFrom( result.tiles );
			countRockNeighbours( tmp2 , neighbours );
			// In the former, this means that cells survive from one generation to the next if they have at least one and at most five neighbours.
			for ( int i = 0 ; i < Chunk.WIDTH*Chunk.HEIGHT ; i++) 
			{
				final int neighbourCount = neighbours[ i ];
				if ( tmp2.tiles[i] == Tile.ROCK ) 
				{
					if ( neighbourCount < 1 || neighbourCount > 5 ) 
					{
						tmp2.tiles[i]=Tile.EMPTY;
						cellsChanged++;
					}
				} 
			}
			TileArray tmp = result.tiles;
			result.tiles = tmp2;
			tmp2 = tmp;
			generations++;
		} while ( cellsChanged != 0 && generations < 300 );
		
		// cleanup
		for ( int j = 0 ; j < 1 ; j++) {
			countRockNeighbours( result.tiles , neighbours );
			for ( int i = 0 ; i < Chunk.WIDTH*Chunk.HEIGHT ; i++) 
			{
				if ( result.tiles.tiles[i] == Tile.ROCK && neighbours[ i ] <= 2 ) {
					result.tiles.tiles[i] = Tile.EMPTY;
				}
			}
		}
		
		// flood-fill
//		boolean out = false;
//		for ( int y = -Chunk.HALF_HEIGHT ; y <= Chunk.HALF_HEIGHT ; y++ ) 
//		{
//			for ( int x = -Chunk.HALF_WIDTH; x <= Chunk.HALF_WIDTH; x++ ) 
//			{
//				if ( x == 0 ) {
//					out = result.getTile( x , y ) == Tile.EMPTY;
//				} 
//				else 
//				{
//					if ( result.getTile( x , y ) != Tile.EMPTY ) {
//						out = ! out;
//					} else if ( ! out ) {
//						result.setTile(x,y,Tile.ROCK);
//					}
//				}
//			}
//		}
		return result;
	}
}