package com.ynot.androidengine.gl;

import javax.microedition.khronos.opengles.GL10;

import com.ynot.androidengine.GLGraphics;
import com.ynot.androidengine.Texture;
import com.ynot.androidengine.TextureRegion;
import com.ynot.androidengine.math.Vector2;


public class SpriteBatcher 
{
	int bufferIndex;               // Position in buffer to start drawing from
	int numSprites;                // # of sprites drawn so far in this batch
	
	final float[] verticesBuffer;  // A buffer to hold vertices until they are drawn
	
	final Vertices vertices;       // A Vertices object to render vertices with
	
	/*********************************************************
	 * Constructs a new SpriteBatcher object
	 * 
	 * @param glGraphics - A GLGraphics object for rendering
	 * @param maxSprites - Max number of sprites in a batch
	 *********************************************************/
	public SpriteBatcher(GLGraphics glGraphics, int maxSprites)
	{
		// Each sprite has 4 vertices x 4 bytes per vertex
		this.verticesBuffer = new float[maxSprites * 4 * 4];		
		this.vertices = new Vertices(glGraphics, maxSprites * 4, maxSprites * 6, false, true);
		
		this.bufferIndex = 0;
		this.numSprites = 0;
		
		// Each sprite has 6 indices to render it's rectangle 
		short[] indices = new short[maxSprites * 6];
		int len = indices.length;
		short j = 0;
		
		// 6 indices and 4 vertices per sprite
		for (int i = 0; i < len; i += 6, j += 4)
		{
			// Set the indices of this 
			// sprite's vertices
			indices[i + 0] = (short) (j + 0);  // 0-----1
			indices[i + 1] = (short) (j + 1);  // |    /|
			indices[i + 2] = (short) (j + 3);  // |   / | <-- 2 triangles form the sprite's box
			indices[i + 3] = (short) (j + 1);  // |  /  |     vertices 1 and 3 are shared for efficiency
			indices[i + 4] = (short) (j + 2);  // | /   |
			indices[i + 5] = (short) (j + 3);  // 3-----2
		}
		vertices.setIndices(indices, 0, indices.length);
	}
	
	/**************************************************************************
	 * Starts a new batch of sprites for eventual rendering
	 * 
	 * @param texture - The texture that all sprites in this batch will share
	 **************************************************************************/
	public void beginBatch(Texture texture)
	{
		texture.bind();   // Bind the texture 
		numSprites = 0;   // No sprites drawn so far
		bufferIndex = 0;  // Start at beginning of vertex buffer
	}
	
	/****************************************************************
	 * Finalize and draw the current batch of sprites to the screen
	 ****************************************************************/
	public void endBatch()
	{
		vertices.setVertices(verticesBuffer, 0, bufferIndex);
		vertices.bind();
		vertices.draw(GL10.GL_TRIANGLES, 0, numSprites * 6);
		vertices.unbind();
	}
	
	/***************************************************************
	 * Adds a sprite to the batch
	 * 
	 * @param x - The x value of the sprite's center (game coords)
	 * @param y - The y value of the sprite's center (game coords)
	 * @param width - Sprite width (game units)
	 * @param height - Sprite height (game units)
	 * @param region - A TextureRegion for the sprite's texture
	 ***************************************************************/
	public void addSprite(float x, float y, float width, float height, TextureRegion region)
	{
		float halfWidth = width / 2;
		float halfHeight = height / 2;
		
		// Sprite's lower-left coords
		float x1 = x - halfWidth;
		float y1 = y - halfHeight;
		
		// Sprite's upper-right coords
		float x2 = x + halfWidth;
		float y2 = y + halfHeight;
		
		/***********************************************************************************************
		 * REMINDER: In the game world, (0,0) (x and y) is at the bottom-left corner of the screen.
		 *           In OpenGL textures, (0,0) (s and t) denotes the upper-left corner of the texture.
		 ***********************************************************************************************/
		
		// Sprite's upper-left corner ["index 0"]
		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y2;
		verticesBuffer[bufferIndex++] = region.s1;
		verticesBuffer[bufferIndex++] = region.t1;
		
		// Sprite's upper-right corner ["index 1"]
		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;
		verticesBuffer[bufferIndex++] = region.s2;
		verticesBuffer[bufferIndex++] = region.t1;
		
		// Sprite's lower-right corner ["index 2"]
		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y1;
		verticesBuffer[bufferIndex++] = region.s2;
		verticesBuffer[bufferIndex++] = region.t2;
		
		// Sprite's lower-left corner ["index 3"]
		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;
		verticesBuffer[bufferIndex++] = region.s1;
		verticesBuffer[bufferIndex++] = region.t2;
		
		numSprites++;
	}
	
	/***************************************************************
	 * Adds a sprite to the batch
	 * 
	 * @param x - The x value of the sprite's center (game coords)
	 * @param y - The y value of the sprite's center (game coords)
	 * @param width - Sprite width (game units)
	 * @param height - Sprite height (game units)
	 * @param angle - The angle (degrees) to rotate the sprite by
	 * @param region - A TextureRegion for the sprite's texture
	 ***************************************************************/
	public void addSprite(float x, float y, float width, float height, float angle, TextureRegion region)
	{
		float halfWidth = width / 2;
		float halfHeight = height / 2;
		
		float rad = angle * Vector2.TO_RADIANS;
		float cos = (float) Math.cos(rad);
		float sin = (float) Math.sin(rad);
		
		// Rotate the sprite's four corners
		
		float x1 = -halfWidth * cos - halfHeight * sin;
		float y1 = -halfWidth * sin + halfHeight * cos;
		
		float x2 = halfWidth * cos - halfHeight * sin;
		float y2 = halfWidth * sin + halfHeight * cos;
		
		float x3 = halfWidth * cos - (-halfHeight) * sin;
		float y3 = halfWidth * sin + (-halfHeight) * cos;
		
		float x4 = -halfWidth * cos - (-halfHeight) * sin;
		float y4 = -halfWidth * sin + (-halfHeight) * cos;
		
		x1 += x;
		y1 += y;
		x2 += x;
		y2 += y;
		x3 += x;
		y3 += y;
		x4 += x;
		y4 += y;
		
		/***********************************************************************************************
		 * REMINDER: In the game world, (0,0) (x and y) is at the bottom-left corner of the screen.
		 *           In OpenGL textures, (0,0) (s and t) denotes the upper-left corner of the texture.
		 ***********************************************************************************************/
		
		// Sprite's upper-left corner ["index 0"]
		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;
		verticesBuffer[bufferIndex++] = region.s1;
		verticesBuffer[bufferIndex++] = region.t1;
		
		// Sprite's upper-right corner ["index 1"]
		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;
		verticesBuffer[bufferIndex++] = region.s2;
		verticesBuffer[bufferIndex++] = region.t1;
		
		// Sprite's lower-right corner ["index 2"]
		verticesBuffer[bufferIndex++] = x3;
		verticesBuffer[bufferIndex++] = y3;
		verticesBuffer[bufferIndex++] = region.s2;
		verticesBuffer[bufferIndex++] = region.t2;
		
		// Sprite's lower-left corner ["index 3"]
		verticesBuffer[bufferIndex++] = x4;
		verticesBuffer[bufferIndex++] = y4;
		verticesBuffer[bufferIndex++] = region.s1;
		verticesBuffer[bufferIndex++] = region.t2;
		
		numSprites++;
	}
}
