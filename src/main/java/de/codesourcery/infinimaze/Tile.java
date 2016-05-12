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

import java.awt.Color;
import java.awt.Graphics2D;

public enum Tile 
{
	ROCK 
	{
		@Override
		public void setup(Graphics2D ctx) {
			ctx.setColor(Color.BLACK);
		}		
	},
	EMPTY 
	{
		public void setup(Graphics2D ctx) {
			ctx.setColor( BROWN );
		}
	};
	
	private static final Color BROWN = new Color(Integer.parseInt( "911717" , 16 ) );
	
	public abstract void setup(Graphics2D ctx);
	
	public void render(Graphics2D gfx, int x0,int y0,int width,int height) {
		gfx.fillRect( x0 , y0 , width , height );
	}
}
