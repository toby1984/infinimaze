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
