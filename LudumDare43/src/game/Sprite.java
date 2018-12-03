package game;

import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

/**
 * Handles animation of GameObjects
 */
public class Sprite {
	
	public double framerate = Game.FRAME_RATE, duration;
	public int width, height;
	private static HashMap<GameObject.Animations, SpriteSheet> animations = new HashMap<GameObject.Animations, SpriteSheet>();
	private Image frame;
	private GameObject.Animations state;
	private double t;
	public double xscale = 1, yscale = 1;
	boolean flip = false;
	
	/**
	 * Adds a sprite sheet to the dictionary
	 * @param a - The animation to load
	 */
	public static void loadAnimation(GameObject.Animations a) {
		int i = a.filename.indexOf(".");
		String name = i>0?a.filename.substring(0, a.filename.indexOf(".")):a.filename;
		if(animations.containsKey(name)) {
			return; // duplicate detection
		}
		animations.put(a, new SpriteSheet(i>0?a.filename:name+".png", a.columns, a.frames));
	
	}
	
	/**
	 * Updates the animation by a specified time step
	 * @param state - the desired animation
	 * @param dt - the time step in seconds
	 * @return true if the animation completed
	 */
	public boolean animate(GameObject.Animations state, double dt, boolean flip) {
		if(!animations.containsKey(state)) {
			System.err.println("Animation not found: "+state);
			return false; // Error detection
		}
		this.flip = flip;
		t += dt; // Update elapsed time
		if(!state.equals(this.state)) {
			t = 0;
			this.state = state; // Change animations
		}
		SpriteSheet animation = animations.get(state);
		width = (int)(animation.width*xscale);
		height = (int)(animation.height*yscale);
		duration = animation.frames/framerate;
		frame = animation.getFrame((int)(t*framerate)%animation.frames);
		if(t >= duration-.01) {
			t = 0; // Loop animation
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Render the sprite centered at (0,0)
	 * @param g - the Graphics context centered on the sprite
	 * @param object - the GameObject possessing the sprite
	 */
	public void draw(Graphics g, GameObject object) {
		if(frame==null||state==null) return; // error detection
		if(flip) {
			g.drawImage(frame, width/2, -height/2, -width, height, null);
		} else {
			g.drawImage(frame, -width/2, -height/2, width, height, null);
		}
	}
}