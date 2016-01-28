package com.ynot.androidengine;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ynot.androidengine.layout.Audio;
import com.ynot.androidengine.layout.FileIO;
import com.ynot.androidengine.layout.Game;
import com.ynot.androidengine.layout.Input;
import com.ynot.androidengine.layout.Screen;

public abstract class AndroidGame extends Activity implements Game, Renderer
{
	enum GLGameState
	{
		Initialized,
		Running,
		Paused,
		Finished,
		Idle
	}
    GLSurfaceView glView;
    GLGraphics glGraphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    GLGameState state = GLGameState.Initialized;
    Object stateChanged = new Object();
    long startTime = System.nanoTime();   

    /***************************************************************
     * Called when the game first launches
     ***************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // Set to fullscreen and don't go to sleep
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set the view
        glView = new GLSurfaceView(this);
        glView.setRenderer(this);
        setContentView(glView);
        
        // Load game engine parts
        glGraphics = new GLGraphics(glView);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, glView, 1, 1);
    }

    /***************************************************************
     * Called when the game resumes and always follows onCreate()
     ***************************************************************/
    @Override
    public void onResume() 
    {
        super.onResume();
        glView.onResume();  // Start the rendering thread
    }
    
    /***************************************************************
     * Called by the rendering thread when the Surface is created
     ***************************************************************/
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
    	glGraphics.setGL(gl);
    	
    	synchronized (stateChanged)
    	{
    		// Get the start screen if the application is starting for the 1st time
    		if (state == GLGameState.Initialized) { screen = getStartScreen(); }
    		
    		// Change game state to Running
    		state = GLGameState.Running;
    		
    		// Tell the current Screen to resume
    		screen.resume();
    		startTime = System.nanoTime();
    	}
    }
    
    /***************************************************************
     * Called by the rendering thread.  
     ***************************************************************/
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) { /* Nothing to do */ }
    
    /***************************************************************
     * Called by the rendering thread as often as possible
     ***************************************************************/
    @Override
    public void onDrawFrame(GL10 gl)
    {
    	GLGameState state = null;
    	
    	// Get the game's state
    	synchronized (stateChanged) { state = this.state; }
    	
    	// Next part depends on what the state is
    	switch (state)
    	{
    	case Running:
    		// Get the elapsed time since the last frame (in seconds)
    		float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
    		
    		// Reset the clock
    		startTime = System.nanoTime(); 
    		
    		// Update and render the current screen
    		screen.update(deltaTime);
    		screen.present(deltaTime);
    		break;
    		
    	case Paused:
    		// Pause the current screen
    		screen.pause();
    		
    		synchronized (stateChanged)
    		{
    			this.state = GLGameState.Idle;  // Idling
    			stateChanged.notifyAll();       // Let the UI thread know it can have this.state back
    		}
    		break;
    	
    	case Finished:
    		screen.pause();    // Pause the current screen
    		screen.dispose();  // Clean up
    		
    		synchronized (stateChanged)
    		{
    			this.state = GLGameState.Idle;  // Idling
    			stateChanged.notifyAll();       // Let the UI thread know it can have this.state back
    		}
    		break;
    	}
    }

    @Override
    public void onPause() 
    {
        synchronized (stateChanged)
        {
        	// Game over, or just pausing?
        	state = (isFinishing()) ? GLGameState.Finished : GLGameState.Paused;

        	while (true)
        	{
        		try
        		{
        			stateChanged.wait();  // Wait for rendering thread to stop
        			break;                // Rendering thread has stopped
        		}
        		catch (InterruptedException ex) { /* Rendering thread still running */ }
        	}
        	glView.onPause();
        	super.onPause();
        }
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    @Override
    public GLGraphics getGLGraphics() {
        return glGraphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Screen screen) 
    {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        // Tell current Screen to cease and desist
        this.screen.pause();
        this.screen.dispose();
        
        // Start up the new Screen
        screen.resume();
        screen.update(0);
        
        // Make it the current Screen
        this.screen = screen;
    }
    
    public Screen getCurrentScreen() {

    	return screen;
    }
}