package game;

import game.GameObject.Animations;

public class Background extends GameObject {

	Animations a;
	public Background(int w, int h, Animations a) {
		super(w/2, h/2, 0, w, h, 1);
		this.a = a;
	}

	@Override
	public double[] update(Game game, double dt) {
		this.sprite.animate(a, dt, false);
		return new double[]{0, 0};
	}
}
