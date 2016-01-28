package com.ynot.arobsvacation;

public class Cannon extends GameObject 
{
	public float angle;
	
	/**
	 * Constructs a new Cannon object
	 * 
	 * @param x - The x value of the cannon's center
	 * @param y - The y value of the cannon's center
	 * @param width - Width of the cannon
	 * @param height - Height of the cannon
	 */
	public Cannon(float x, float y, float width, float height) 
	{
		super(x, y, width, height);
		angle = 0;
	}	
}
