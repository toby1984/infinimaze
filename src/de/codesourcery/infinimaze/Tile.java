package de.codesourcery.infinimaze;

import java.awt.Color;
import java.awt.Graphics2D;

public enum Tile 
{
	ROCK 
	{
		@Override
		public void setup(Graphics2D ctx) {
			ctx.setColor(Color.GRAY);
		}		
	},
	EMPTY 
	{
		public void setup(Graphics2D ctx) {
			ctx.setColor(Color.BLACK);
		}
	};
	
	public abstract void setup(Graphics2D ctx);
	
	public void render(Graphics2D gfx, int x0,int y0,int width,int height) {
		gfx.fillRect( x0 , y0 , width , height );
	}
}
