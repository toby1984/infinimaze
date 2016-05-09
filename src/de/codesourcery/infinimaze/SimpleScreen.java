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
	
	private int tileWidth=4; // in pixels
	private int tileHeight=6; // in pixels
	
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
	public void render() 
	{
		render( (ctx,width,height) -> 
		{
			ctx.setColor( getBackground() );
			ctx.fillRect(0,0,width,height);
			
			final float cx = getWidth()/2f;
			final float cy = getHeight()/2f;
			
			World world = visibleChunks.world;
			
			final float offsetX = world.camera.cameraX * tileWidth;
			final float offsetY = -world.camera.cameraY * tileHeight;
			
			final float centerChunkTopLeftX = cx - Chunk.HALF_CHUNK_WIDTH*tileWidth   - tileWidth  / 2f - offsetX; 
			final float centerChunkTopLeftY = cy - Chunk.HALF_CHUNK_HEIGHT*tileHeight - tileHeight / 2f - offsetY;
			
			final List<Chunk> visibleChunks2 = visibleChunks.getVisibleChunks();
			for ( Chunk chunk : visibleChunks2 ) 
			{
				final int chunkDx = world.camera.currentChunk.x - chunk.key.x;
				final int chunkDy = world.camera.currentChunk.y - chunk.key.y;
				
				final float chunkTopLeftX = centerChunkTopLeftX - chunkDx * Chunk.CHUNK_WIDTH  * tileWidth;
				final float chunkTopLeftY = centerChunkTopLeftY + chunkDy * Chunk.CHUNK_HEIGHT * tileHeight;

				for ( int y = 0 ; y < Chunk.CHUNK_HEIGHT ; y++ ) 
				{				
					final float py = chunkTopLeftY + y * tileHeight;
					for ( int x = 0 ; x <  Chunk.CHUNK_WIDTH ; x++ ) 
					{
						final float px = chunkTopLeftX + x * tileWidth;
						final Tile tile = chunk.getTile( x - Chunk.HALF_CHUNK_WIDTH , Chunk.HALF_CHUNK_HEIGHT - y );
						tile.setup( ctx );
						tile.render( ctx , (int) px , (int) py , tileWidth , tileHeight );
					}
				}
				ctx.setColor( Color.RED );
				ctx.drawRect( (int) chunkTopLeftX , (int) chunkTopLeftY , 
						Chunk.CHUNK_WIDTH * tileWidth , Chunk.CHUNK_HEIGHT * tileHeight );
			}
			
			ctx.setColor( Color.RED );
			ctx.drawString( world.camera.toString() , 15 ,15 );
			
			ctx.setColor(Color.RED);
			ctx.drawLine( (int) (cx - 5) , (int) cy     ,  (int) (cx+5) , (int) cy );
			ctx.drawLine( (int) cx       , (int) (cy-5) ,  (int) cx   , (int) (cy+5) );			
		});
		repaint();
		Toolkit.getDefaultToolkit().sync();
	}	
}