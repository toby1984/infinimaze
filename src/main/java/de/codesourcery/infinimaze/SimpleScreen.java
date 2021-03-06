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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

public class SimpleScreen extends JPanel implements IScreen {

	private BufferedImage buffer;
	private Graphics2D graphics;
	
	
	protected static final int DEFAULT_TILE_WIDTH = 4;
	protected static final int DEFAULT_TILE_HEIGHT = 6;
	
	protected boolean debugRendering = false;
	
	private final VisibleChunks visibleChunks;
	
	private interface IRenderFunc 
	{
		public void render(Graphics2D gfx,int width,int height);
	}
	
	public SimpleScreen(VisibleChunks visibleChunks) {
		if (visibleChunks == null) {
			throw new IllegalArgumentException("visibleChunks must not be NULL");
		}
		this.visibleChunks = visibleChunks;
		setDoubleBuffered(false);
	}
	
	private void render(IRenderFunc func) 
	{
		func.render( getBuffer() , getWidth() , getHeight() );
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		if ( buffer == null ) {
			super.paintComponent(g);
			return;
		}
		getBuffer();
		g.drawImage( buffer , 0 , 0 , null );
	}
	
	private Graphics2D getBuffer() 
	{
		if ( buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight() ) 
		{
			BufferedImage previous = null;
			if ( graphics != null ) 
			{
				previous = buffer;
				graphics.dispose();
			}
			buffer = getGraphicsConfiguration().createCompatibleImage( getWidth() , getHeight() );
			graphics = buffer.createGraphics();
			graphics.setFont( new Font(Font.MONOSPACED , Font.PLAIN ,12 ) );
			if ( previous != null ) 
			{
				graphics.drawImage( previous , 0 , 0 , null );
			}
		}
		return graphics;
	}
	
	@Override
	public void render(float deltaSeconds) 
	{
		render( (ctx,width,height) -> 
		{
			ctx.setColor( getBackground() );
			ctx.fillRect(0,0,width,height);
			
			final float cx = getWidth()/2f;
			final float cy = getHeight()/2f;
			
			World world = visibleChunks.world;
			
			final int tileWidth = Math.round (DEFAULT_TILE_WIDTH  * world.camera.zoom);
			final int tileHeight= Math.round (DEFAULT_TILE_HEIGHT * world.camera.zoom);
			
			final float offsetX = world.camera.cameraX * tileWidth;
			final float offsetY = -world.camera.cameraY * tileHeight;
			
			final float centerChunkTopLeftX = cx - Chunk.HALF_WIDTH*tileWidth   - tileWidth  / 2f - offsetX; 
			final float centerChunkTopLeftY = cy - Chunk.HALF_HEIGHT*tileHeight - tileHeight / 2f - offsetY;
			
			final List<Chunk> visibleChunks2 = visibleChunks.getVisibleChunks();
			for ( Chunk chunk : visibleChunks2 ) 
			{
				final int chunkDx = world.camera.currentChunk.x - chunk.key.x;
				final int chunkDy = world.camera.currentChunk.y - chunk.key.y;
				
				final float chunkTopLeftX = centerChunkTopLeftX - chunkDx * Chunk.WIDTH  * tileWidth;
				final float chunkTopLeftY = centerChunkTopLeftY + chunkDy * Chunk.HEIGHT * tileHeight;

				for ( int y = 0 ; y < Chunk.HEIGHT ; y++ ) 
				{				
					final float py = chunkTopLeftY + y * tileHeight;
					for ( int x = 0 ; x <  Chunk.WIDTH ; x++ ) 
					{
						final float px = chunkTopLeftX + x * tileWidth;
						final int tileX = x - Chunk.HALF_WIDTH;
						final int tileY = Chunk.HALF_HEIGHT - y;
						final Tile tile = chunk.getTile( tileX , tileY );
						
						if ( chunk.key.equals( world.camera.currentChunk ) ) {
							if ( tileX == Math.round( world.camera.cameraX ) && tileY == Math.round( world.camera.cameraY ) ) {
								ctx.setColor(Color.BLUE);
								ctx.fillRect( (int) px , (int) py , tileWidth , tileHeight );
								continue;
							}
						}
						tile.setup( ctx );
						tile.render( ctx , (int) px , (int) py , tileWidth , tileHeight );
					}
				}
				if ( debugRendering ) {
					ctx.setColor( Color.RED );
					ctx.drawRect( (int) chunkTopLeftX , (int) chunkTopLeftY , 
							Chunk.WIDTH * tileWidth , Chunk.HEIGHT * tileHeight );
				}
			}
			
			ctx.setColor( Color.RED );
			final float fps = (int) (1.0f/deltaSeconds);
			ctx.drawString( world.camera.toString()+" , "+fps+" FPS [ hit CTRL-F to toggle debug rendering mode ]" , 15 ,15 );
			
			if ( debugRendering ) {
				ctx.setColor(Color.RED);
				ctx.drawLine( (int) (cx - 5) , (int) cy     ,  (int) (cx+5) , (int) cy );
				ctx.drawLine( (int) cx       , (int) (cy-5) ,  (int) cx   , (int) (cy+5) );
			}
		});
		repaint();
		Toolkit.getDefaultToolkit().sync();
	}

	@Override
	public void setDebugRendering(boolean yesNo) {
		this.debugRendering = yesNo;
	}

	@Override
	public boolean isDebugRendering() {
		return debugRendering;
	}	
}