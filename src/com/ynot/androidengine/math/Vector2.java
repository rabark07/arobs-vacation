package com.ynot.androidengine.math;

public class Vector2 
{
	public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
	public static float TO_DEGREES = (1 / (float) Math.PI) * 180;
	public float x, y;
	
	public Vector2() {}
	
	public Vector2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2(Vector2 other)
	{
		this.x = other.x;
		this.y = other.y;
	}
	
	public Vector2 cpy()
	{
		return new Vector2(x, y);
	}
	
	public Vector2 set(float x, float y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vector2 set(Vector2 other)
	{
		this.x = other.x;
		this.y = other.y;
		return this;
	}
	
	public Vector2 add(float x, float y)
	{
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Vector2 add(Vector2 other)
	{
		this.x += other.x;
		this.y += other.y;
		return this;
	}
	
	public Vector2 sub(float x, float y)
	{
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	public Vector2 sub(Vector2 other)
	{
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}
	
	public Vector2 mul(float scalar)
	{
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}
	
	public float len()
	{
		// c^2 = a^2 + b^2
		// c = sqrt(a^2 + b^2)
		return (float) Math.sqrt(x * x + y * y);
	}
	
	/***********************************************************************
	 * Normalize this vector to a unit length of 1.  The x and y values of
	 * this vector will be divided by the vector's length; causing the new
	 * length to be equal to 1.
	 * 
	 * @return A reference to this Vector object
	 ***********************************************************************/
	public Vector2 norm()
	{
		float len = len();
		
		if (len != 0)
		{
			this.x /= len;
			this.y /= len;
		}
		
		return this;
	}
	
	/***********************************************************
	 * Calculates the angle between this vector and the x-axis 
	 * 
	 * @return The angle (or arc tangent) in degrees
	 ***********************************************************/
	public float angle()
	{
		float angle = (float) Math.atan2(y, x) * TO_DEGREES;
		
		if (angle < 0)
			angle += 360;
		
		return angle;
	}
	
	/***********************************************************
	 * Rotate this vector counterclockwise around the origin by
	 * the given angle
	 * 
	 * @param angle - The angle (degrees) to rotate by
	 * @return A reference to this Vector object
	 ***********************************************************/
	public Vector2 rotate(float angle)
	{
		float rad = angle * TO_RADIANS;
		float cos = (float) Math.cos(rad);
		float sin = (float) Math.sin(rad);
		
		// For now, just trust that these two formulas are legit.
		// If you want to know how they were derived, search the
		// Web for "orthogonal base vectors"
		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin - this.y * cos;
		
		this.x = newX;
		this.y = newY;
		
		return this;
	}
	
	/**************************************************************
	 * Calculates the distance from this vector to another vector
	 * 
	 * @param other - The other vector
	 * @return The distance
	 **************************************************************/
	public float dist(Vector2 other)
	{
		float distX = this.x - other.x;
		float distY = this.y - other.y;
		
		return (float) Math.sqrt(distX * distX + distY * distY);
	}
	
	/**************************************************************
	 * Calculates the distance from this vector to another vector
	 * 
	 * @param x - The x value of the other vector
	 * @param y - The y value of the other vector
	 * @return The distance
	 **************************************************************/
	public float dist(float x, float y)
	{
		float distX = this.x - x;
		float distY = this.y - y;
		
		return (float) Math.sqrt(distX * distX + distY * distY);
	}
	
	/******************************************************************
	 * Returns the squared distance between this vector and another.
	 * Use this method when you wish to avoid a square root operation
	 * for performance reasons
	 * 
	 * @param other - The other vector
	 * @return The squared distance
	 ******************************************************************/
	public float distSquared(Vector2 other)
	{
		float distX = this.x - other.x;
		float distY = this.y - other.y;
		
		return distX * distX + distY * distY;
	}
	
	/******************************************************************
	 * Returns the squared distance between this vector and another.
	 * Use this method when you wish to avoid a square root operation
	 * for performance reasons
	 * 
	 * @param x - The x value of the other vector
	 * @param y - The y value of the other vector
	 * @return The squared distance
	 ******************************************************************/
	public float distSquared(float x, float y)
	{
		float distX = this.x - x;
		float distY = this.y - y;
		
		return distX * distX + distY * distY;
	}
}
