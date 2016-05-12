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

import de.codesourcery.infinimaze.Chunk.ChunkKey;
import de.codesourcery.infinimaze.Chunk.TileArray;

public class RandomChunkProvider extends AbstractChunkProvider 
{
	public RandomChunkProvider(IChunkStorage storage) 
	{
		super(storage);
	}

	@Override
	protected Chunk createChunk(ChunkKey key) 
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
		} while ( cellsChanged != 0 && generations < 10);
		
		// cleanup
		countRockNeighbours( result.tiles , neighbours );
		for ( int i = 0 ; i < Chunk.WIDTH*Chunk.HEIGHT ; i++) 
		{
			if ( result.tiles.tiles[i] == Tile.ROCK && neighbours[ i ] <= 2 ) {
				result.tiles.tiles[i] = Tile.EMPTY;
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
		result.markDirty();
		return result;
	}
}