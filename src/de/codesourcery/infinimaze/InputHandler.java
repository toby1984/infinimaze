package de.codesourcery.infinimaze;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

public class InputHandler 
{
	public static final float TRANS_X = 7.5f;
	public static final float TRANS_Y = 7.5f;
	
	private final Set<Key> pressed = new HashSet<>();
	private final World world;
	
	public static enum Key {
		UP,
		LEFT,
		RIGHT,
		DOWN,
		ZOOM_IN,
		ZOOM_OUT
	}
	
	public InputHandler(World world) {
		this.world = world;
	}
	
	public void attach(JComponent component) 
	{
		component.addKeyListener( new KeyAdapter() 
		{
			public void keyPressed(java.awt.event.KeyEvent e) 
			{
				switch ( e.getKeyCode() ) {
					case KeyEvent.VK_A: pressed.add( Key.LEFT ); break; 
					case KeyEvent.VK_D: pressed.add( Key.RIGHT); break; 
					case KeyEvent.VK_W: pressed.add( Key.UP ); break; 
					case KeyEvent.VK_S: pressed.add( Key.DOWN ); break;
					case KeyEvent.VK_PLUS: pressed.add( Key.ZOOM_IN); break;
					case KeyEvent.VK_MINUS: pressed.add( Key.ZOOM_OUT); break;
					default: 
						// $$FALL-THROUGH$$
				}
			}
			
			public void keyReleased(java.awt.event.KeyEvent e) 
			{
				switch ( e.getKeyCode() ) {
					case KeyEvent.VK_A: pressed.remove( Key.LEFT ); break; 
					case KeyEvent.VK_D: pressed.remove( Key.RIGHT); break; 
					case KeyEvent.VK_W: pressed.remove( Key.UP ); break; 
					case KeyEvent.VK_S: pressed.remove( Key.DOWN ); break;
					case KeyEvent.VK_PLUS: pressed.remove( Key.ZOOM_IN); break;
					case KeyEvent.VK_MINUS: pressed.remove( Key.ZOOM_OUT); break;
					default: 
						// $$FALL-THROUGH$$
				}
			}
		} );
	}
	
	public void tick(float deltaSeconds) 
	{
		if ( pressed.contains(Key.UP ) ) {
			world.camera.translate(0,TRANS_Y*deltaSeconds);
		} else if ( pressed.contains(Key.DOWN ) ) {
			world.camera.translate(0,TRANS_Y*-deltaSeconds);
		}
		if ( pressed.contains(Key.LEFT) ) {
			world.camera.translate(TRANS_X*-deltaSeconds,0);
		} else if ( pressed.contains(Key.RIGHT) ) {
			world.camera.translate(TRANS_X*deltaSeconds,0);
		}		
		if ( pressed.contains( Key.ZOOM_IN ) ) {
			world.camera.zoom( deltaSeconds );
		} else if ( pressed.contains( Key.ZOOM_OUT ) ) {
			world.camera.zoom( -deltaSeconds );
		}
	}
}
