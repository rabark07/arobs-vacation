package com.ynot.androidengine;

/******************************************************************************
 * A TextureRegion represents a four-sided region of a larger Texture.  Given
 * the location of the region in pixels, a TextureRegion object will perform
 * the necessary conversions to OpenGL texture coordinates (all OpenGL texture
 * objects have a coordinate range of (0,0) to (1,1))
 * 
 * @author A.Rob
 * @credit Mario Zechner "Beginning Android Games"
 ******************************************************************************/
public class TextureRegion 
{
	public final float s1, t1;
	public final float s2, t2;
	public final Texture texture;
	
	/*********************************************************************
	 * Constructs a TextureRegion object
	 * 
	 * @param texture - A Texture object that contains the TextureRegion
	 * @param x - The pixel x value of the region's upper left corner
	 * @param y - The pixel y value of the region's upper left corner
	 * @param width - The width (px) of the region
	 * @param height - The height (px) of the region
	 *********************************************************************/
	public TextureRegion(Texture texture, float x, float y, float width, float height)
	{
		this.s1 = x / texture.width;
		this.t1 = y / texture.height;
		this.s2 = this.s1 + width / texture.width;
		this.t2 = this.t1 + height / texture.height;
		this.texture = texture;
	}
}
