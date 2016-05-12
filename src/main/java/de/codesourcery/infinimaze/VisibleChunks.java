/**
 * Copyright 2016 Tobias Gierke <tobias.gierke@code-sourcery.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codesourcery.infinimaze;

import java.util.ArrayList;
import java.util.List;

public class VisibleChunks 
{
	public static final int RENDER_DISTANCE_IN_CHUNKS = 4;
	
	public static final int TOTAL_VISIBLE_CHUNKS= ( RENDER_DISTANCE_IN_CHUNKS*2 +1 )* ( RENDER_DISTANCE_IN_CHUNKS*2+1);
	
	private List<Chunk> chunks = new ArrayList<>(TOTAL_VISIBLE_CHUNKS);
	public final World world;
	public final IChunkProvider chunkProvider;
	
	public VisibleChunks(World world,IChunkProvider chunkProvider) 
	{
		if (world == null) {
			throw new IllegalArgumentException("world must not be NULL");
		}
		if ( chunkProvider == null ) {
			throw new IllegalArgumentException("chunkProvider must not be NULL");
		}
		this.chunkProvider = chunkProvider;
		this.world = world;
	}
	
	public List<Chunk> getVisibleChunks() {
		
		if ( chunks.isEmpty() || world.camera.changed ) 
		{
			updateVisibleChunks();
			world.camera.changed=false;
		}
		return chunks;
	}

	private void updateVisibleChunks() 
	{
		final List<Chunk> newChunks = new ArrayList<>( TOTAL_VISIBLE_CHUNKS );
		
		for ( int x = world.camera.currentChunk.x - RENDER_DISTANCE_IN_CHUNKS , maxX= world.camera.currentChunk.x + RENDER_DISTANCE_IN_CHUNKS ; x <= maxX ; x++ ) 
		{
			for ( int y = world.camera.currentChunk.y + RENDER_DISTANCE_IN_CHUNKS , minY = world.camera.currentChunk.y - RENDER_DISTANCE_IN_CHUNKS ; y >= minY ; y-- ) 
			{
				final Chunk newChunk = chunkProvider.getChunk( x , y );
				newChunks.add( newChunk );
			}			
		}
		for ( Chunk chunk : chunks ) 
		{
			if ( ! newChunks.contains( chunk ) ) {
				chunkProvider.unload( chunk );
			}
		}
		chunks = newChunks;
	}
	
}
