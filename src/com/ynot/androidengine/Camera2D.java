package com.ynot.androidengine;

import javax.microedition.khronos.opengles.GL10;

import com.ynot.androidengine.math.Vector2;

public class Camera2D 
{
	public final Vector2 position;
	
	public float zoom;
	public final float frustumWidth;
	public final float frustumHeight;
	
	final GLGraphics glGraphics;
	
	/************************************************************************************
	 * Constructs a new Camera2D object
	 * 
	 * @param glGraphics - A GLGraphics object to set the viewport and projection with
	 * @param fWidth - The width of the view frustum
	 * @param fHeight - The height of the view frustum
	 ************************************************************************************/
	public Camera2D(GLGraphics glGraphics, float fWidth, float fHeight)
	{
		this.glGraphics = glGraphics;
		this.frustumWidth = fWidth;
		this.frustumHeight = fHeight;
		
		// Set position to the center of the frustum (bounded by: (0, 0, 1) to (fWidth, fHeight, -1))
		this.position = new Vector2(fWidth / 2, fHeight / 2);
		
		// Zoom level defaults to 100%
		this.zoom = 1.0f;
	}
	
	/*********************************************************************************
	 * Sets the viewport to fullscreen and uses orthographic projection (according to 
	 * the frustum and zoom properties that have been set) and then sets the OpenGL
	 * matrix mode to GL10.GL_MODELVIEW
	 *********************************************************************************/
	public void setViewportAndProjection()
	{
		GL10 gl = glGraphics.getGL();
		
		gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(
			position.x - frustumWidth * zoom / 2,
			position.x + frustumWidth * zoom / 2,
			position.y - frustumHeight * zoom / 2,
			position.y + frustumHeight * zoom / 2,
			1, -1
		);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	/************************************************************************
	 * Converts a user's touch coords to game world coords
	 *  
	 * @param touch - A Vector2 object representing the user's touch coords
	 ************************************************************************/
	public void touchToWorld(Vector2 touch)
	{
		// Convert touch coords to game world coords: (0, 0) to (frustumWidth, frustumHeight)
		// REMEMBER: Y-axis is pointing UP in the game world.  When Android gets the touch 
		//           coords, Y-axis is pointing DOWN. So we have to flip the Y-axis value.
		touch.x = (touch.x / (float) glGraphics.getWidth()) * frustumWidth * zoom;
		touch.y = (1 - touch.y / (float) glGraphics.getHeight()) * frustumHeight * zoom;
		
		// Rethink this line, perhaps?
		//touch.add(position).sub(frustumWidth * zoom / 2, frustumHeight * zoom / 2);
	}
}
