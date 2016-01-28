package com.ynot.androidengine.layout;

import com.ynot.androidengine.GLGraphics;

public interface Game {

    public Audio getAudio();

    public Input getInput();

    public FileIO getFileIO();

    public GLGraphics getGLGraphics();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getStartScreen();
	
}
