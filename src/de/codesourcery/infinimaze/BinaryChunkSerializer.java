package de.codesourcery.infinimaze;

import java.io.*;

import de.codesourcery.infinimaze.Chunk.ChunkKey;

public final class BinaryChunkSerializer implements IChunkSerializer 
{
	@Override
	public void write(Chunk chunk, OutputStream out) throws IOException 
	{
		writeInt( chunk.key.x , out );
		writeInt( chunk.key.y , out );
		writeInt( Chunk.Flag.DIRTY.clear( chunk.flags ) , out );
		writeIntArray( chunk.tiles.tiles , Tile::ordinal , out );
	}
	
	@Override
	public Chunk read(InputStream in) throws IOException 
	{
		final int chunkX = readInt(in);
		final int chunkY = readInt(in);
		final Chunk chunk = new Chunk(new ChunkKey(chunkX,chunkY));
		chunk.flags = readInt(in);
		
		chunk.tiles.tiles = readIntArray( ordinal -> Tile.values()[ordinal] , in );
		
		return chunk;
	}	
	
	protected interface IntMapper<T> 
	{
		public int map(T ob);
	}
	
	protected interface ObjMapper<T> 
	{
		public T map(int value);
	}	
	
	private <T> void writeIntArray(T[] array,IntMapper<T> mapper,OutputStream out) throws IOException 
	{
		writeInt( array.length , out );
		for ( int i = 0 ; i < array.length ; i++ ) {
			writeInt( mapper.map( array[i] ) , out );
		}
	}
	
	private <T> T[] readIntArray(ObjMapper<T> mapper,InputStream in) throws IOException 
	{
		final int len = readInt(in);
		@SuppressWarnings("unchecked")
		final T[] arr = (T[])new Object[ len ];
		for ( int i = 0 ; i <len ; i++ ) {
			arr[i] = mapper.map( readInt(in) );
		}
		return arr;
	}
	
	private void writeInt(int value,OutputStream out) throws IOException 
	{
		writeByte( value & 0xff , out );
		writeByte( ( value & 0xff00 ) >> 8 , out );
		writeByte( ( value & 0xff0000 ) >> 16 , out );
		writeByte( ( value & 0xff000000 ) >> 24 , out );
	}
	
	private int readInt(InputStream in) throws IOException 
	{
		int result = readByte(in);
		result = ( result << 8 ) | readByte(in);
		result = ( result << 8 ) | readByte(in);
		result = ( result << 8 ) | readByte(in);
		return result;
	}
	
	private void writeByte(int byteValue,OutputStream out) throws IOException {
		out.write( byteValue );
	}
	
	private int readByte(InputStream in) throws IOException {
		
		final int result = in.read();
		if ( result == -1 ) {
			throw new EOFException();
		}
		return result & 0xff;
	}
}
