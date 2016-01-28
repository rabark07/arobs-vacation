package com.ynot.androidengine.math;

public class Overlap 
{
	public static boolean circles(Circle c1, Circle c2)
	{
		// Use distSquared() because squaring is faster than square rooting
		float distance = c1.center.distSquared(c2.center);
		float radiusSum = c1.radius + c2.radius;
		
		return distance <= radiusSum * radiusSum;
	}
	
	public static boolean rectangles(Rectangle r1, Rectangle r2)
	{
		if (r1.lowerLeft.x < r2.lowerLeft.x + r2.width &&
			r1.lowerLeft.x + r1.width > r2.lowerLeft.x &&
			r1.lowerLeft.y < r2.lowerLeft.y + r2.height &&
			r1.lowerLeft.y + r1.height > r2.lowerLeft.y)
			
			return true;
		else
			return false;
	}
	
	public static boolean circleRectangle(Circle c, Rectangle r)
	{
		float closestX = c.center.x;
		float closestY = c.center.y;
		
		// Find the closest X value in the rectangle to the circle's center
		if (c.center.x < r.lowerLeft.x)
		{
			closestX = r.lowerLeft.x;
		}
		else if (c.center.x > r.lowerLeft.x + r.width)
		{
			closestX = r.lowerLeft.x + r.width;
		}
		
		// Find the closest Y value in the rectangle to the circle's center
		if (c.center.y < r.lowerLeft.y)
		{
			closestY = r.lowerLeft.y;
		}
		else if (c.center.y > r.lowerLeft.y + r.height)
		{
			closestY = r.lowerLeft.y + r.height;
		}
		
		// Use distSquared() because squaring is faster than square rooting
		return c.center.distSquared(closestX, closestY) < c.radius * c.radius;
	}
	
	public static boolean pointInCircle(Circle c, Vector2 p)
	{
		return c.center.distSquared(p) < c.radius * c.radius;
	}
	
	public static boolean pointInCircle(Circle c, float x, float y)
	{
		return c.center.distSquared(x, y) < c.radius * c.radius;
	}
	
	public static boolean pointInRectangle(Rectangle r, Vector2 p)
	{
		return  r.lowerLeft.x <= p.x && r.lowerLeft.x + r.width >= p.x &&
				r.lowerLeft.y <= p.y && r.lowerLeft.y + r.height >= p.y;
	}
	
	public static boolean pointInRectangle(Rectangle r, float x, float y)
	{
		return  r.lowerLeft.x <= x && r.lowerLeft.x + r.width >= x &&
				r.lowerLeft.y <= y && r.lowerLeft.y + r.height >= y;
	}
}
