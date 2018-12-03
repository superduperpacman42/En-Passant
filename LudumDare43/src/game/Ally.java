package game;

public class Ally extends PlatformingObject {

	public Ally(int x, int y, double scale, Animations defaultAnim) {
		super(x, y, 4, defaultAnim);
		this.sprite = new Sprite();
		this.w*=scale;
		this.h*=scale;
		this.sprite.xscale = this.sprite.yscale = scale/4;
	}
}