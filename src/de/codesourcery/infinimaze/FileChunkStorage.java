package de.codesourcery.infinimaze;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.codesourcery.infinimaze.Chunk.ChunkKey;

public class FileChunkStorage implements IChunkStorage 
{
	private final Path baseDir;
	private final IChunkSerializer serializer = new BinaryChunkSerializer();
	
	public FileChunkStorage(Path baseDir) {
	
		this.baseDir = baseDir;
		try {
			if ( ! Files.exists( baseDir ) ) 
			{
				Files.createDirectories( baseDir );
			}
			if ( ! Files.isDirectory( baseDir ) ) {
				throw new IOException("Not a directory: "+baseDir);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Path getPathFor(ChunkKey key) {
		return baseDir.resolve( "chunk_"+key.x+"_"+key.y+".chunk");
	}
	
	@Override
	public Map<ChunkKey, Chunk> load(Set<ChunkKey> keys) throws IOException {
		
		final Map<ChunkKey, Chunk> result = new HashMap<>();
		for ( ChunkKey key : keys ) 
		{
			final Chunk c = load(key);
			if ( c != null ) {
				result.put( key , c );
			}
		}
		return result;
	}

	@Override
	public Chunk load(ChunkKey key) throws IOException 
	{
		final Path path = getPathFor(key);
		if ( ! Files.exists( path ) ) {
			return null;
		}
		try ( InputStream in = Files.newInputStream( path ) ) {
			return serializer.read( in );
		}
	}

	@Override
	public void save(Chunk chunk) throws IOException 
	{
		if ( chunk.isDirty() ) 
		{
			final Path path = getPathFor(chunk.key);
			try ( OutputStream out = Files.newOutputStream( path , StandardOpenOption.CREATE) ) 
			{
				serializer.write( chunk , out );
				chunk.clearDirty();
			}
		}
	}
}