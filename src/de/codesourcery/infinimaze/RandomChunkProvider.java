package de.codesourcery.infinimaze;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.codesourcery.infinimaze.Chunk.ChunkKey;

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

	private Chunk createChunk(ChunkKey key) 
	{
		final Chunk result = new Chunk(key);
		
		long x = key.x;
		long y = key.y;
		final long seed = ((x & 0xffffffff) <<32) | ( y & 0xffffffff);
		final Random rnd = new Random(seed);
		System.out.println("Generating new chunk "+key+" with seed "+seed);
		final Tile[] tiles = result.tiles;
		for ( int i = 0 , len = tiles.length ; i < len ; i++) 
		{
			final boolean bool = rnd.nextFloat() < 0.8f;
			 tiles[i] = bool ? Tile.ROCK : Tile.EMPTY;
		}
		
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH , Chunk.HALF_CHUNK_WIDTH, Tile.ROCK );
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH+1 , Chunk.HALF_CHUNK_WIDTH, Tile.ROCK );
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH+2 , Chunk.HALF_CHUNK_WIDTH, Tile.ROCK );
//
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH , Chunk.HALF_CHUNK_WIDTH-1, Tile.ROCK );
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH+1 , Chunk.HALF_CHUNK_WIDTH-1, Tile.ROCK );
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH+2 , Chunk.HALF_CHUNK_WIDTH-1, Tile.ROCK );
//		
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH , Chunk.HALF_CHUNK_WIDTH-2, Tile.ROCK );
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH+1 , Chunk.HALF_CHUNK_WIDTH-2, Tile.ROCK );
//		result.setTile( -Chunk.HALF_CHUNK_WIDTH+2 , Chunk.HALF_CHUNK_WIDTH-2, Tile.ROCK );
		return result;
	}
}