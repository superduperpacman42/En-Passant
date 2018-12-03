package game;

public class Item extends GameObject {
	
	double t = 0, k;
	Animations a, b;

	public Item(double x, double y, Animations a, Animations b) {
		super(x, y+10, 2, 10, 10, .25);
		this.a = a;
		this.b = b;
		this.k = 1.5;
		if(a==Animations.SPEAR) {
			this.k = 1.2;
		}
	}

	@Override
	public double[] update(Game game, double dt) {
		t += dt;
		double vy = Math.sin(2*t)*.5;
		this.sprite.animate(a, dt, false);
		return new double[]{0, vy};
	}
	
	public void collide(GameObject obj, double[] delta) {
		if(obj instanceof Player) {
			this.visible = false;
			this.kill = true;
		}
	}
}
