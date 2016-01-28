package com.ynot.androidengine.math;

public class Circle 
{
	public final Vector2 center = new Vector2();
	public float radius;
	
	/*************************************************
	 * Constructs a new Circle object
	 * 
	 * @param x - The x value of the circle's center
	 * @param y - The y value of the circle's center
	 * @param radius - The radius of the circle
	 *************************************************/
	public Circle(float x, float y, float radius)
	{
		this.center.set(x, y);
		this.radius = radius;
	}
}
