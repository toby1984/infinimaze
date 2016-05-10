package de.codesourcery.infinimaze;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import de.codesourcery.infinimaze.Chunk.ChunkKey;

public class Main extends JFrame
{
	private final Camera camera = new Camera( new ChunkKey(0,0 ) );	
	private final IChunkStorage storage = new FileChunkStorage( new File("/home/tgierke/tmp/chunks").toPath() );  
	private final IChunkProvider chunkProvider = new RandomChunkProvider(storage);
	private final World world = new World( camera );
	private final InputHandler inputHandler = new InputHandler( world , chunkProvider );	
	private SimpleScreen screen;
	
	public Main() {
		super("InfiniMaze");
	}
	
	public static void main(String[] args) throws InvocationTargetException, InterruptedException 
	{
		SwingUtilities.invokeAndWait( () -> new Main().run() );
	}

	private void run() 
	{
		Chunk chunk = chunkProvider.getChunk( camera.currentChunk );
		if ( chunk.getTile( (int) camera.cameraX , (int) camera.cameraY ) != Tile.EMPTY ) 
		{
			final Random rnd = new Random( 0xdeadbeef );
			do {
				camera.cameraX = -Chunk.HALF_WIDTH + rnd.nextInt( Chunk.WIDTH ); 
				camera.cameraY = -Chunk.HALF_HEIGHT + rnd.nextInt( Chunk.HEIGHT );
			} while ( chunk.getTile( (int) camera.cameraX , (int) camera.cameraY ) != Tile.EMPTY );
		}
		
		screen = new SimpleScreen( new VisibleChunks(world , chunkProvider ) );
		screen.setFocusable( true );
		screen.setRequestFocusEnabled( true );
		screen.requestFocus();
		
		getContentPane().setLayout(new GridBagLayout());
		final GridBagConstraints cnstrs = new GridBagConstraints();
		cnstrs.fill = GridBagConstraints.BOTH;
		cnstrs.weightx=1; cnstrs.weighty=1;
		cnstrs.gridx = 0 ; cnstrs.gridy = 0;
		cnstrs.gridwidth = 1 ; cnstrs.gridheight = 1;
		getContentPane().add( screen , cnstrs );

		setPreferredSize( new Dimension(640,480) );
		pack();
		setLocationRelativeTo( null );
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		inputHandler.attach( screen );
		final Timer timer = new Timer(16 , ev -> gameLoop() );
		timer.start();
	}
	
	private long lastNanos=0;
	private void gameLoop() 
	{
		final long now = System.nanoTime();
		if ( lastNanos == 0 ) {
			lastNanos = now;
		} else {
			float deltaSeconds = (now - lastNanos)* (1/ 1_000_000_000f);
			lastNanos = now;
			inputHandler.tick(deltaSeconds);
		}
		screen.render();
	}
}
