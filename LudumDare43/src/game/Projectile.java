package game;

public class Projectile extends GameObject{
	Animations a;
	boolean right;
	double t = 0;

	public Projectile(double x, double y, Animations a, boolean right) {
		super(x, y, 4, 50, 50, .25);
		this.a = a;
		this.right = right;
	}

	@Override
	public double[] update(Game game, double dt) {
		t += dt;
		if(this.a==Animations.ARROW){
			if(t>.25) {
				double V = 800;
				this.x+=right?V*dt:-V*dt;
				this.y-=V*dt;
				if(this.y < 0) this.kill = true;
				this.sprite.animate(a, dt, !right);
			}
			if(this.checkCollision(game.player, this.x, this.y, 20, 20)) {
				game.player.collide(this, new double[]{0,0,0,0});
			}
		} else if (this.a==Animations.SPELL1||this.a==Animations.SPELL2) {
			if(t>7.0/12) {
				this.kill = true;
			}
			this.sprite.animate(a, dt, true);
			if(this.checkCollision(game.player, this.x, this.y, 80, 80)) {
				game.player.collide(this, new double[]{0,0,0,0});
			}
		}
		return new double[]{0,0,0,0};
	}

}
