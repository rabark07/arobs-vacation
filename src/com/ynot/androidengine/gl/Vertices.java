package com.ynot.androidengine.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.ynot.androidengine.GLGraphics;

public class Vertices 
{
	final GLGraphics glGraphics;
	final boolean hasColor;
	final boolean hasTexCoords;
	final int vertexSize;
	final FloatBuffer vertices;
	final ShortBuffer indices;
	
	/**
	 * Creates a new Vertices object.
	 * 
	 * @param glGraphics - The game's GLGraphics object
	 * @param maxVertices - Maximum number of vertices
	 * @param maxIndices - Maximum number of indices.  Set to zero for non-indexed rendering
	 * @param hasColor - Whether or not the vertices have a color attribute
	 * @param hasTexCoords - Whether or not the vertices have a texture attribute
	 */
	public Vertices(GLGraphics glGraphics, int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords)
	{
		this.glGraphics = glGraphics;
		this.hasColor = hasColor;
		this.hasTexCoords = hasTexCoords;
		
		// Size = ((x, y) screen coords + RGBA color value + (s, t) texture coords) * 4 bytes per float
		this.vertexSize = (2 + (hasColor?4:0) + (hasTexCoords?2:0)) * 4;
		
		// Allocate a block of native memory (not JVM memory)
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
		
		byteBuffer.order(ByteOrder.nativeOrder());  // Set byte order to the order that the CPU uses
		vertices = byteBuffer.asFloatBuffer();      // Convert to float buffer for working w/ vertices
		
		if (maxIndices > 0)
		{
			byteBuffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);  // maxIndices * 2 bytes per short
			byteBuffer.order(ByteOrder.nativeOrder());
			indices = byteBuffer.asShortBuffer();
		}
		else
		{
			indices = null;
		}
	}
	
	/**
	 * Sets the vertices according to the specified array
	 * 
	 * @param vertices - A float array of vertices
	 * @param offset - The position in the array to start adding from
	 * @param length - The number of elements to add from the array
	 */
	public void setVertices(float[] vertices, int offset, int length)
	{
		this.vertices.clear();                        // Clear the buffer
		this.vertices.put(vertices, offset, length);  // Add the vertices to the buffer
		this.vertices.flip();                         // Reset position in buffer to zero
	}
	
	/**
	 * Sets the indices according to the specified array
	 * 
	 * @param indices - A float array of indices
	 * @param offset - The position in the array to start adding from
	 * @param length - The number of elements to add from the array
	 */
	public void setIndices(short[] indices, int offset, int length)
	{
		this.indices.clear();                       // Clear the buffer
		this.indices.put(indices, offset, length);  // Add the indices to the buffer
		this.indices.flip();                        // Reset position in buffer to zero
	}
	
	/*************************************************************************************
	 * Sets OpenGL vertex, color, and texture pointers as appropriate.  Should be called
	 * before Vertices.draw()
	 *************************************************************************************/
	public void bind()
	{
		GL10 gl = glGraphics.getGL();		
		
		// Set the vertex pointer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		vertices.position(0);
		gl.glVertexPointer(
			2,              // Vertex coords are (x, y) not (x, y, z)
			GL10.GL_FLOAT,  // Vertex coords are floats 
			vertexSize,     // # of bytes between each pair of coords
			vertices        // Point to this block of memory
		);
		
		// Set the color pointer
		if (hasColor)
		{
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			vertices.position(2);
			gl.glColorPointer(
				4,              // RGBA
				GL10.GL_FLOAT,  // RGBA values are floats 
				vertexSize,     // # of bytes between each set of RGBA values
				vertices        // Point to this block of memory
			);
		}
		
		// Set the texture pointer
		if (hasTexCoords)
		{
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			vertices.position(hasColor ? 6 : 2 );
			gl.glTexCoordPointer(
				2,              // Texture coords are (s, t)
				GL10.GL_FLOAT,  // Tex coords are floats 
				vertexSize,     // # of bytes between each pair of coords
				vertices        // Point to this block of memory
			); 
		}
	}
	
	/**
	 * Draws the specified primitive using vertices and/or indices that have been defined.  
	 * Vertices.bind() should be called before draw(), and Vertices.unbind() should be 
	 * called once drawing is finished.
	 * 
	 * @param primitiveType - The primitive type to draw (i.e. GL10.GL_TRIANGLES)
	 * @param offset - Position in the vertices or indices array to start from
	 * @param numVertices - Number of vertices to draw
	 */
	public void draw(int primitiveType, int offset, int numVertices)
	{
		GL10 gl = glGraphics.getGL();		
		
		if (indices != null)
		{
			// Indexed rendering (used for efficiency when shapes share vertices)
			indices.position(offset);
			gl.glDrawElements(
				primitiveType,           // The primitive type to draw (i.e. GL10.GL_TRIANGLES)
				numVertices,             // # of vertices to draw
				GL10.GL_UNSIGNED_SHORT,  // Indices are shorts 
				indices                  // Use the vertices at these indices
			);
		}
		else
		{
			// Non-indexed rendering
			gl.glDrawArrays(
				primitiveType,  // The primitive type to draw (i.e. GL10.GL_TRIANGLES)
				offset,         // The position in the array to start reading vertices
				numVertices     // # of vertices to draw
			);
		}
	}
	
	/*************************************************************************************
	 * Disables OpenGL texture and color states as appropriate.  Should be called after 
	 * drawing with Vertices.draw() is complete.
	 *************************************************************************************/
	public void unbind()
	{
		GL10 gl = glGraphics.getGL();
		
		if (hasTexCoords)
		{
			// Disable texture state
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		
		if (hasColor)
		{
			// Disable color state
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}
}
