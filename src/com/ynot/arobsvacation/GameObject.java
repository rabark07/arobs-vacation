package com.ynot.arobsvacation;

import com.ynot.androidengine.math.Rectangle;
import com.ynot.androidengine.math.Vector2;

public class GameObject 
{
	public final Vector2 position;  // The center of the GameObject
	public final Rectangle bounds;  // Bounding rectangle for collision detection
	
	/*******************************************************
	 * Constructs a GameObject object
	 * 
	 * @param x - The x value of the GameObject's center
	 * @param y - The y value of the GameObject's center
	 * @param width - The width of the GameObject
	 * @param height - The height of the GameObject
	 *******************************************************/
	public GameObject(float x, float y, float width, float height)
	{
		this.position = new Vector2(x, y);
		this.bounds = new Rectangle(x - width/2, y - height/2, width, height);
	}
}
