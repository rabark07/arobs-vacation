package com.ynot.androidengine.math;

public class Rectangle 
{
	public final Vector2 lowerLeft;
	public float width, height;
	
	/***************************************************
	 * Constructs a new Rectangle object
	 * 
	 * @param x - The x value of the lower left corner
	 * @param y - The y value of the lower left corner
	 * @param width - Rectangle's width
	 * @param height - Rectangle's height
	 ***************************************************/
	public Rectangle(float x, float y, float width, float height)
	{
		this.lowerLeft = new Vector2(x, y);
		this.width = width;
		this.height = height;
	}
}
