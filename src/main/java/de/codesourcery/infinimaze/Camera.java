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

public class Camera 
{
	// chunk the camera is in
	public final ChunkKey currentChunk;
	
	// tile coordinates within the current
	// chunk [ (0,0) is a chunk's center tile ]
	// the camera is currently in
	public float cameraX; 
	public float cameraY;
	
	public float zoom = 3f;
	
	public boolean changed = true;
	
	public Camera(ChunkKey currentChunk) {
		this.currentChunk = currentChunk.copy();
	}
	
	@Override
	public String toString() {
		return "Chunk "+currentChunk+" , tile "+cameraX+","+cameraY;
	}

	public boolean translate(float dx, float dy,IChunkProvider provider) 
	{
		float newX = cameraX;
		float newY = cameraY;
		int chunkX = currentChunk.x;
		int chunkY = currentChunk.y;
		
		newX += dx;
		if ( newX > Chunk.WIDTH/2f ) {
			chunkX++;
			newX -= Chunk.WIDTH;
		} else if ( newX < -Chunk.WIDTH/2f ) {
			chunkX--;
			newX += Chunk.WIDTH;
		}
		newY += dy;
		if ( newY > Chunk.HEIGHT/2f ) {
			chunkY++;
			newY -= Chunk.HEIGHT;
		} else if ( newY < -Chunk.HEIGHT/2f ) {
			chunkY--;
			newY += Chunk.HEIGHT;
		}		
		
		final Chunk chunk = provider.getChunk( chunkX ,chunkY );
		
		if ( chunk.getTile( Math.round( newX ) , Math.round( newY ) ) == Tile.EMPTY ) {
			cameraX = newX;
			cameraY = newY;
			currentChunk.x = chunkX;
			currentChunk.y = chunkY;
			
			changed =true;
			return true;
		}
		return false;
	}
	
	public void zoom(float delta) {
		final float newZoom = zoom + delta;
		if ( newZoom > 0 ) {
			zoom = newZoom;
			changed = true;
		}
	}
}