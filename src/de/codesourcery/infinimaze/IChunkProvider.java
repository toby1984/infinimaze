package de.codesourcery.infinimaze;

import de.codesourcery.infinimaze.Chunk.ChunkKey;

public interface IChunkProvider 
{
	public Chunk getChunk(ChunkKey key);
	
	public Chunk getChunk(int chunkX,int chunkY);
}