package de.codesourcery.infinimaze;

public interface IScreen 
{
	public void render();
	
	public void setDebugRendering(boolean yesNo);
	
	public boolean isDebugRendering();
}