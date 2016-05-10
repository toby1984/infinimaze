package de.codesourcery.infinimaze;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.codesourcery.infinimaze.Chunk.ChunkKey;

public interface IChunkStorage 
{
	public Map<ChunkKey,Chunk> load(Set<ChunkKey> keys) throws IOException;
	
	/**
	 * 
	 * @param key
	 * @return chunk or <code>null</code>
	 */
	public Chunk load(ChunkKey key) throws IOException;  
	
	public void save(Chunk chunk) throws IOException;
}
