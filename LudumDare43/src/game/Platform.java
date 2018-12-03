package game;

import game.GameObject.Animations;

public class Platform extends GameObject {
	static double image_size = 100;
	Animations a;

	public Platform(double x, double y, int z, int w, int h, Animations a) {
		super(x, y, 1, w, h, 1.5);
		this.a = a;
	}

	@Override
	public double[] update(Game game, double dt) {
		this.sprite.animate(a, dt, false);
		return new double[]{0, 0};
	}
}
