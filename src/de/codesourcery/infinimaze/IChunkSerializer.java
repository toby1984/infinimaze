package de.codesourcery.infinimaze;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementations need to be thread-safe !! 
 * @author tgierke
 */
public interface IChunkSerializer 
{
	public void write(Chunk chunk,OutputStream out) throws IOException;
	
	public Chunk read(InputStream out) throws IOException;
}
