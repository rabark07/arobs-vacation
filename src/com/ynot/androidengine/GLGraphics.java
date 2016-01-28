package com.ynot.androidengine;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public class GLGraphics 
{
	GLSurfaceView glView;  // SurfaceView for OpenGL
	GL10 gl;               // OpenGL ES 1.0 object
	
	/*******************************************
	 * CONSTRUCTOR
	 * 
	 * @param glsv  A View for game graphics
	 *******************************************/
	GLGraphics(GLSurfaceView glsv)
	{
		this.glView = glsv;
	}
	
	/*******************************************
	 * Gets the GL10 instance 
	 * 
	 * @return The GL10 instance
	 *******************************************/
	public GL10 getGL() { return this.gl; }
	
	/*******************************************
	 * Set the GL10 instance
	 * 
	 * @param gl  A GL10 object
	 *******************************************/
	public void setGL( GL10 gl) { this.gl = gl; }
	
	/*******************************************
	 * Gets the height of the GLSurfaceView
	 * 
	 * @return The height (in pixels) of the view
	 *******************************************/
	public int getHeight() { return this.glView.getHeight(); }
	
	/*******************************************
	 * Gets the width of the GLSurfaceView
	 * 
	 * @return The width (in pixels) of the view
	 *******************************************/
	public int getWidth() { return this.glView.getWidth(); }
}
