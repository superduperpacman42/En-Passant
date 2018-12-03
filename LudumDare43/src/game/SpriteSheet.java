package game;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Manages an array of images comprising an animation
 */
public class SpriteSheet {
	
	private Image[] images;
	public int frames;
	public int width;
	public int height;
	
	/**
	 * Loads the sprite sheet image and splits it into frames
	 * @param filename - sprite sheet file
	 * @param columns - number of columns in the sprite sheet
	 * @param frames - total number of frames in the sprite sheet
	 */
	public SpriteSheet(String filename, int columns, int frames) {
		images = new Image[frames];
		this.frames = frames;
		try {
			String path = "/animations/" + filename;
			BufferedImage sheet = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
			width = sheet.getWidth()/columns;
			height = (int) (sheet.getHeight()/Math.ceil(frames/columns));
			for(int i=0; i<frames; i++) {
				images[i] = sheet.getSubimage((i%columns)*width, (i/columns)*height, width, height);
			}
		} catch (IOException e) {
			System.err.println("Image not loaded: "+filename);
		} catch (IllegalArgumentException e) {
			System.err.println("Image not loaded: "+filename);
		}
	}
	
	/**
	 * Get a specified frame of the animation
	 * @param index - the index of the desired frame
	 * @return - the desired frame
	 */
	public Image getFrame(int index) {
		if(index>=images.length) {
			System.err.println("Animation frame index out of bounds");
			return null;
		}
		return images[index];
	}
}
