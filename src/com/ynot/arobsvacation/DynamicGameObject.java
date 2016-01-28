package com.ynot.arobsvacation;

import com.ynot.androidengine.math.Vector2;

public class DynamicGameObject extends GameObject 
{
	public final Vector2 velocity;
	public final Vector2 accel;
	
	/*******************************************************
	 * Constructs a DynamicGameObject object
	 * 
	 * @param x - The x value of the DynamicGameObject's center
	 * @param y - The y value of the DynamicGameObject's center
	 * @param width - The width of the DynamicGameObject
	 * @param height - The height of the DynamicGameObject
	 *******************************************************/
	public DynamicGameObject(float x, float y, float width, float height)
	{
		super(x, y, width, height);
		
		velocity = new Vector2();
		accel = new Vector2();
	}
}
