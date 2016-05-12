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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

public class InputHandler 
{
	public static final float TRANS_X = 7.5f;
	public static final float TRANS_Y = 7.5f;
	
	private final Set<Key> pressed = new HashSet<>();
	private final World world;
	private final IChunkProvider chunkProvider;
	private final IScreen screen;
	
	public static enum Key {
		UP,
		LEFT,
		RIGHT,
		DOWN,
		MOUSE_ZOOM_IN,
		MOUSE_ZOOM_OUT,
		ZOOM_IN,
		ZOOM_OUT,
		TOGGLE_DEBUG_MODE;
	}
	
	public InputHandler(World world,IChunkProvider chunkProvider,IScreen screen) {
		this.world = world;
		this.chunkProvider = chunkProvider;
		this.screen = screen;
	}
	
	public void attach(JComponent component) 
	{
		component.addMouseWheelListener( new MouseAdapter() {
			
			public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) 
			{
				final int clicks = e.getWheelRotation();
				if ( clicks > 0 ) {
					pressed.add( Key.MOUSE_ZOOM_IN );
				} else {
					pressed.add( Key.MOUSE_ZOOM_OUT );
				}
			}
		} );
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
					case KeyEvent.VK_F:
						if ( (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK ) != 0 ) {
							pressed.add( Key.TOGGLE_DEBUG_MODE );
						}
						break;
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
					case KeyEvent.VK_F: pressed.remove(Key.TOGGLE_DEBUG_MODE); break;
					default: 
						// $$FALL-THROUGH$$
				}
			}
		} );
	}
	
	public void tick(float deltaSeconds) 
	{
		// movement
		if ( pressed.contains(Key.UP ) ) {
			world.camera.translate(0,TRANS_Y*deltaSeconds,chunkProvider);
		} else if ( pressed.contains(Key.DOWN ) ) {
			world.camera.translate(0,TRANS_Y*-deltaSeconds,chunkProvider);
		}
		if ( pressed.contains(Key.LEFT) ) {
			world.camera.translate(TRANS_X*-deltaSeconds,0,chunkProvider);
		} else if ( pressed.contains(Key.RIGHT) ) {
			world.camera.translate(TRANS_X*deltaSeconds,0,chunkProvider);
		}		
		
		// zoom
		if ( pressed.contains( Key.ZOOM_IN ) ) {
			world.camera.zoom( deltaSeconds );
		} else if ( pressed.contains( Key.ZOOM_OUT ) ) {
			world.camera.zoom( -deltaSeconds );
		} else if ( pressed.contains( Key.MOUSE_ZOOM_IN ) ) {
			world.camera.zoom( deltaSeconds*4 );
			pressed.remove( Key.MOUSE_ZOOM_IN );
		} else if ( pressed.contains( Key.MOUSE_ZOOM_OUT ) ) {
			world.camera.zoom( -deltaSeconds*4 );
			pressed.remove( Key.MOUSE_ZOOM_OUT );
		}
		
		// misc
		if ( pressed.contains( Key.TOGGLE_DEBUG_MODE ) ) {
			System.out.println("Toggle");
			screen.setDebugRendering( ! screen.isDebugRendering() );
			pressed.remove( Key.TOGGLE_DEBUG_MODE );
		}
	}
}