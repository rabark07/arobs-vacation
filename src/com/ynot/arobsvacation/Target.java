package com.ynot.arobsvacation;

public class Target extends GameObject 
{
	public enum TargetState {
		MISSED,
		HIT
	}
	
	public float hitTime;
	public TargetState state;
	
	/**************************************************
	 * Constructs a new Target object
	 * 
	 * @param x - The x value of the cannon's center
	 * @param y - The y value of the cannon's center
	 * @param width - Width of the cannon
	 * @param height - Height of the cannon
	 **************************************************/
	public Target(float x, float y, float width, float height) 
	{
		super(x, y, width, height);
		
		this.hitTime = 0;
		this.state = TargetState.MISSED;
	}
	
	/************************************************************
	 * Updates the target.  If the target was hit, increases the
	 * time it has spent being hit.
	 * 
	 * @param deltaTime - The elapsed time since the last frame
	 ************************************************************/
	public void update(float deltaTime)
	{
		if (state == TargetState.HIT)
		{
			hitTime += deltaTime;
		}
	}
}
