package com.ynot.androidengine;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.ynot.androidengine.layout.FileIO;
import com.ynot.androidengine.layout.Game;

public class Texture 
{
	GLGraphics glGraphics;
	FileIO fileIO;
	String fileName;
	int textureID;
	int magFilter;
	int minFilter;
	int width;
	int height;
	
	// CONSTRUCTOR
	public Texture(Game game, String fileName)
	{
		this.glGraphics = game.getGLGraphics();
		this.fileName = fileName;
		this.fileIO = game.getFileIO();
		load();
	}

	/**************************************************************************
	 * Loads the Texture object's image into VRAM as an OpenGL texture object
	 **************************************************************************/
	private void load() 
	{
		// Get OpenGL instance
		GL10 gl = glGraphics.getGL();
		
		// Generate an OpenGL texture object and get its ID
		int[] textureIDs = new int[1];
		gl.glGenTextures(1, textureIDs, 0);
		textureID = textureIDs[0];
		
		InputStream in = null;
		try
		{
			// Load the texture image as a Bitmap
			in = fileIO.readAsset(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			
			this.width = bitmap.getWidth();
			this.height = bitmap.getHeight();
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);       // Tell OpenGL to use our texture object
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);  // Upload an image to our texture object
			setFilters(GL10.GL_NEAREST, GL10.GL_NEAREST);          // Setup how the image will be expanded/contracted
			gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);               // Unbind the texture
		}
		catch (IOException ex)
		{
			throw new RuntimeException("Could not load texture: " + fileName);
		}
		finally
		{
			if (in != null)
			{
				try { in.close(); } catch (IOException ex) {}
			}
		}
	}

	/*********************************************************************************
	 * Sets the magnification and minification filters to the specified mode.  Valid
	 * modes are GL10.GL_NEAREST and GL10.GL_LINEAR.  These modes affect how the image
	 * is expanded or contracted.
	 * 
	 * @param minFilter - Minification filter mode
	 * @param magFilter - Magnification filter mode
	 *********************************************************************************/
	public void setFilters(int minFilter, int magFilter) 
	{
		this.minFilter = minFilter;
		this.magFilter = magFilter;
		GL10 gl = glGraphics.getGL();
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, magFilter);
	}
	
	/*************************************************************************
	 * Binds this texture to OpenGL.  Any OpenGL calls that modify a texture
	 * will modify this texture
	 *************************************************************************/
	public void bind()
	{
		GL10 gl = glGraphics.getGL();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
	}
	
	/*********************************************************************************
	 * Reloads this texture into OpenGL.  This method should be used when recovering
	 * from OpenGL context loss, such as when the Activity is stopped/resumed.
	 *********************************************************************************/
	public void reload()
	{
		load();
		bind();
		setFilters(minFilter, magFilter);
		glGraphics.getGL().glBindTexture(GL10.GL_TEXTURE_2D, 0);		
	}
	
	/*************************************************************************
	 * Unbinds this texture from OpenGL and removes the image data from VRAM
	 *************************************************************************/
	public void dispose()
	{
		GL10 gl = glGraphics.getGL();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
		int[] textureIDs = { textureID };
		gl.glDeleteTextures(1, textureIDs, 0);
	}
	
}
