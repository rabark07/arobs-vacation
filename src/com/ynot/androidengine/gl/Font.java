package com.ynot.androidengine.gl;

import com.ynot.androidengine.Texture;
import com.ynot.androidengine.TextureRegion;

/*************************************************************************************
 * This class renders font using a specified "bitmap font".  The bitmap font must be
 * fixed-width and contain ASCII characters from 32 to 96.
 * 
 * @author A.Rob
 * @credit Mario Zechner "Beginning Android Games"
 *
 *************************************************************************************/
public class Font 
{
	public final Texture texture;
	
	public final int glyphWidth;
	public final int glyphHeight;
	
	public final TextureRegion[] glyphs = new TextureRegion[96];
	
	/**************************************************************************
	 * Creates a new Font object.
	 * 
	 * @param texture - The texture containing the bitmap font
	 * @param offsetX - The x-value of the top-left corner of the bitmap font
	 * @param offsetY - The y-value of the top-left corner of the bitmap font
	 * @param glyphsPerRow - # of characters per row in the bitmap font
	 * @param glyphWidth - The width (px) of each character
	 * @param glyphHeight - The height (px) of each character
	 * @param fontWidth - The width (game units) of each character
	 * @param fontHeight - The height (game units) of each character
	 **************************************************************************/
	public Font(Texture texture, int offsetX, int offsetY, int glyphsPerRow, int glyphWidth, int glyphHeight)
	{
		this.texture = texture;
		this.glyphWidth = glyphWidth;
		this.glyphHeight = glyphHeight;
		
		int x = offsetX;
		int y = offsetY;
		
		for (int i = 0; i < 96; i++)
		{
			// Create a new glyph with the current (x,y) values
			glyphs[i] = new TextureRegion(texture, x, y, glyphWidth, glyphHeight);
			
			// Get the (x,y) values for the next glyph
			x += glyphWidth;
			if (x == offsetX + glyphsPerRow * glyphWidth)
			{
				// Move down to the next row of glyphs
				x = offsetX;
				y += glyphHeight;
			}
		}
	}
	
	/*************************************************************************************
	 * Gets the textures for the specified string and adds them to a batch for rendering
	 * 
	 * @param batcher - A SpriteBatcher to add the string's textures to
	 * @param text - The string to render
	 * @param x - The x-value of the center of the 1st character in the string
	 * @param y - The y-value of the center of the 1st character in the string
	 * @param fontWidth - The width (game units) of each character
	 * @param fontHeight - The height (game units) of each character
	 *************************************************************************************/
	public void drawText(SpriteBatcher batcher, String text, float x, float y, float fontWidth, float fontHeight)
	{
		// Loop through each character of the string to draw
		int len = text.length();
		for (int i = 0; i < len; i++)
		{
			// Get the ASCII value of the next character in the string.
			// Subtract 32 from this value because we expect the bitmap
			// font to start at the space character.
			int c = text.charAt(i) - ' ';
			
			// Do not render characters with ASCII value outside of 32 - 96.
			// We do not have glyphs for these characters
			if (c < 0 || c > glyphs.length - 1)
			{
				continue;
			}
			
			// Get the glyph for this character
			TextureRegion glyph = glyphs[c];
			
			// Add it to batch for rendering
			batcher.addSprite(x, y, fontWidth, fontHeight, glyph);
			
			// Advance x-coord to the left by one character
			x += fontWidth;
		}
	}
}
