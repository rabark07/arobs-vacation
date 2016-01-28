package com.ynot.androidengine.gl;

import android.util.Log;

import com.ynot.androidengine.TextureRegion;

public class Animation 
{
	public static final int LOOPING = 0;
	public static final int NONLOOPING = 0;
	
	final TextureRegion[] keyFrames;
	public final float frameDuration;
	
	/*********************************************************************************
	 * Constructs a new Animation object
	 * 
	 * @param frameDuration - The duration (secs) for each frame of the animation
	 * @param keyFrames - A collection of TextureRegions that comprise the animation
	 *********************************************************************************/
	public Animation(float frameDuration, TextureRegion ... keyFrames)
	{
		this.frameDuration = frameDuration;
		this.keyFrames = keyFrames;
	}
	
	/******************************************************************************************
	 * Gets the appropriate frame of the animation based on the elapsed time and looping mode
	 * 
	 * @param stateTime - The time that has elapsed since this animation started
	 * @param mode - The looping mode (Animation.LOOPING or Animation.NONLOOPING)
	 * @return A TextureRegion for the appropriate frame
	 ******************************************************************************************/
	public TextureRegion getKeyFrame(float stateTime, int mode)
	{
		int frameNumber = (int) (stateTime / frameDuration);
		
		if (mode == NONLOOPING)
		{
			// Get the current frame or stay on last frame if animation is done
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
		}
		else 
		{
			// Get the current frame
			frameNumber = frameNumber % keyFrames.length;
		}
		
		return keyFrames[frameNumber];
	}
}
