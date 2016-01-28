package com.ynot.arobsvacation;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.ynot.androidengine.AndroidGame;
import com.ynot.androidengine.Camera2D;
import com.ynot.androidengine.FPSCounter;
import com.ynot.androidengine.GLGraphics;
import com.ynot.androidengine.SpatialHashGrid;
import com.ynot.androidengine.Texture;
import com.ynot.androidengine.TextureRegion;
import com.ynot.androidengine.gl.Animation;
import com.ynot.androidengine.gl.Font;
import com.ynot.androidengine.gl.SpriteBatcher;
import com.ynot.androidengine.layout.Game;
import com.ynot.androidengine.layout.Input.TouchEvent;
import com.ynot.androidengine.layout.Screen;
import com.ynot.androidengine.layout.Sound;
import com.ynot.androidengine.math.Overlap;
import com.ynot.androidengine.math.Vector2;

public class CannonTest extends AndroidGame
{
	@Override
	public Screen getStartScreen() 
	{
		return new CannonScreen(this);
	}
	
	class CannonScreen extends Screen
	{
		final int NUM_TARGETS = 20;
		int playerScore = 0;
		int hitStreak = 0;
		
		final float WORLD_WIDTH = 9.6f;
		final float WORLD_HEIGHT = 6.4f;
		final float FONT_WIDTH = 0.2f;
		final float FONT_HEIGHT = 0.5f;
		
		GLGraphics glGraphics;
		
		Cannon cannon;	
		
		DynamicGameObject ball;
		
		List<Target> targets;
		List<Target> hitTargets;
		
		SpatialHashGrid grid;
		
		Vector2 touchPos = new Vector2();
		Vector2 gravity = new Vector2(0, -10);
		
		Camera2D camera;
		
		Texture texture;
		
		TextureRegion cannonRegion;
		TextureRegion ballRegion;
		TextureRegion targetRegion;
		
		SpriteBatcher batcher;
		
		Animation targetHit;
		
		FPSCounter fps = new FPSCounter();
		
		Font font ;
		
		Sound fireSound;
		Sound hitSound;
		
		public CannonScreen(Game game) 
		{
			super(game);
			glGraphics = game.getGLGraphics();
			
			// Set up the camera
			camera = new Camera2D(glGraphics, WORLD_WIDTH, WORLD_HEIGHT);
			
			// Create cannon, cannon ball, and targets
			cannon = new Cannon(0, 0, 1, 0.5f);
			ball = new DynamicGameObject(0, 0, 0.2f, 0.2f);
			targets = new ArrayList<Target>(NUM_TARGETS);
			hitTargets = new ArrayList<Target>(NUM_TARGETS);
			
			// Set up grid for collision detection
			grid = new SpatialHashGrid(WORLD_WIDTH, WORLD_HEIGHT, 2.5f);
			
			// Create all targets
			for (int i = 0; i < NUM_TARGETS; i++)
			{
				// Create target in a random location
				Target target = new Target(
					(float) Math.random() * WORLD_WIDTH, 
					(float) Math.random() * WORLD_HEIGHT, 
					0.5f, 
					0.5f
				);	
				grid.insertStaticObject(target);  // Add the target to the grid
				targets.add(target);              // Add target to list of targets
			}
			
			// Create batcher for up to 100 sprites
			batcher = new SpriteBatcher(glGraphics, 100);
		}

		@Override
		public void update(float deltaTime) 
		{
			List<TouchEvent> touches = game.getInput().getTouchEvents();
			
			int len = touches.size();
			for (int i = 0; i < len; i++)
			{
				TouchEvent touch = touches.get(i);
				
				// Convert touch coords to game world coords: (0, 0) to (WORLD_WIDTH, WORLD_HEIGHT)
				camera.touchToWorld(touchPos.set(touch.x, touch.y));
				
				// Get the cannon's angle
				cannon.angle = touchPos.sub(cannon.position).angle();  
				
				// Fire the cannon on TOUCH_UP
				if (touch.type == TouchEvent.TOUCH_UP)
				{
					float radians = cannon.angle * Vector2.TO_RADIANS;  // Convert cannon angle to radians
					float ballSpeed = touchPos.len() * 2;               // Set the cannon ball speed
					
					// Set the cannon ball's position and velocity
					ball.position.set(cannon.position);
					ball.velocity.x = (float) (Math.cos(radians) * ballSpeed);
					ball.velocity.y = (float) (Math.sin(radians) * ballSpeed);
					
					// Set collision bounds of the cannon ball
					ball.bounds.lowerLeft.set(ball.position.x - 0.1f, ball.position.y - 0.1f);
					
					// Play firing sound effect
					fireSound.play(1);
				}
			}
			
			// Apply gravity and velocity to cannon ball
			ball.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
			ball.position.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);
			ball.bounds.lowerLeft.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);

			// Check for collisions
			List<GameObject> colliders = grid.getPotentialCollisions(ball);
			len = colliders.size();
			for (int i = 0; i < len; i++)
			{
				Target collider = (Target) colliders.get(i);
				if (Overlap.rectangles(ball.bounds, collider.bounds))
				{
					// This target has been hit					
					collider.state = Target.TargetState.HIT;
					
					grid.removeObject(collider);  // Remove it from collision grid
					targets.remove(collider);     // Remove it from target list
					hitTargets.add(collider);     // Add it to the list of targets that have been hit
					
					// Update the streak
					hitStreak += 1;
					
					// Update score
					playerScore += (10 * hitStreak);
					
					// Play target hit sound effect
					hitSound.play(1);
				}
			}
			
			// Update targets that have been hit
			len = hitTargets.size();
			for (int i = 0; i < len; i++)
			{
				Target target = hitTargets.get(i);
				target.update(deltaTime);
				
				// Is the hit animation done playing?
				if (target.hitTime > targetHit.frameDuration * 4)
				{
					// Remove it from the list
					hitTargets.remove(i);
					len -= 1;
				}
			}
			
			// Move the camera as appropriate
			if (ball.position.y > 0)
			{
				camera.position.set(ball.position);                // Move the camera to the cannon ball
				camera.zoom = 1 + ball.position.y / WORLD_HEIGHT;  // Zoom out/in as ball goes higher/lower
			}
			else
			{
				// Cannon ball has not fired.  Show default camera view
				camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
				camera.zoom = 1;
				
				// Reset hit streak
				hitStreak = 0;
			}
		}

		@Override
		public void present(float deltaTime) 
		{
			GL10 gl = glGraphics.getGL();
			
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  // Clear the frame buffer
			camera.setViewportAndProjection();     // Set viewport and projection
			
			// Enable texturing
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			
			// Begin sprite batching
			batcher.beginBatch(texture);
			
			// Add targets to the batch
			int len = targets.size();
			for (int i = 0; i < len; i++)
			{
				Target target = targets.get(i);
				// Target has not been hit
				batcher.addSprite(target.position.x, target.position.y, 0.5f, 0.5f, targetRegion);
			}
			
			// Play animation for targets that have been hit
			len = hitTargets.size();
			for (int i = 0; i < len; i++)
			{
				Target target = hitTargets.get(i);
				batcher.addSprite(target.position.x, target.position.y, 0.5f, 0.5f, targetHit.getKeyFrame(target.hitTime, Animation.NONLOOPING));
			}
			
			// Add cannon ball to the batch
			batcher.addSprite(ball.position.x, ball.position.y, 0.2f, 0.2f, ballRegion);
			
			// Add cannon to the batch
			batcher.addSprite(cannon.position.x, cannon.position.y, 1.0f, 0.5f, cannon.angle, cannonRegion);
			
			// Draw the current score
			String score = String.valueOf(playerScore);
			font.drawText(
				batcher, 
				score, 
				(camera.position.x + camera.frustumWidth * camera.zoom / 2) - (FONT_WIDTH * camera.zoom * score.length()), 
				(camera.position.y + camera.frustumHeight * camera.zoom / 2) - (FONT_HEIGHT * camera.zoom / 2),
				FONT_WIDTH * camera.zoom,
				FONT_HEIGHT * camera.zoom
			);
			
			// Draw the hit streak if there is one
			if (hitStreak > 0)
			{
				String streak = String.valueOf(hitStreak);
				font.drawText(
					batcher, 
					"x" + streak + "!", 
					(camera.position.x) - (FONT_WIDTH * camera.zoom * score.length()), 
					(camera.position.y + FONT_HEIGHT * camera.zoom) - (FONT_HEIGHT * camera.zoom / 2),
					FONT_WIDTH * camera.zoom,
					FONT_HEIGHT * camera.zoom
				);
			}
			
			// Draw all sprites in the batch
			batcher.endBatch();
			
			//fps.logFrames();
		}

		@Override
		public void pause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void resume() 
		{
			// Load the texture atlas
			texture = new Texture(game, "tex_atlas.png");
			
			// Set textures
			cannonRegion = new TextureRegion(texture, 0, 0, 64, 32);
			ballRegion = new TextureRegion(texture, 64, 0, 32, 32);
			targetRegion = new TextureRegion(texture, 0, 32, 32, 32);
			
			// Set animation for hitting a target
			targetHit = new Animation(
				0.2f, 
				new TextureRegion(texture, 0, 32, 32, 32),
				new TextureRegion(texture, 32, 32, 32, 32),
				new TextureRegion(texture, 64, 32, 32, 32),
				new TextureRegion(texture, 96, 32, 32, 32)
			);
			
			// Set the bitmap font
			font = new Font(texture, 0, 64, 16, 16, 20);
			
			// Load sounds
			fireSound = game.getAudio().createSound("shoot.ogg");
			hitSound = game.getAudio().createSound("hit.ogg");
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void backButton() {
			// TODO Auto-generated method stub
			
		}		
	}
}
