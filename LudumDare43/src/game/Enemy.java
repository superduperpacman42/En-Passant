package game;

import game.GameObject.Animations;

public class Enemy extends PlatformingObject {

	boolean charge;
	double t = 0;
	
	public Enemy(int x, int y, Animations anim, double scale, boolean charge) {
		super(x, y, 3, anim);
		this.charge = charge;
		maxVLeft = 300;
		maxVRight = 300;
		this.sprite = new Sprite();
		this.w*=scale;
		this.h*=scale;
		this.sprite.xscale = this.sprite.yscale = scale/4;
	}

	@Override
	public double[] update(Game game, double dt) {
		if(kill) return super.update(game, dt);
		t += dt;
		double dx = (game.player.x-x);
		if(Math.abs(dx)>15) {
			this.right = dx>0;
			if (game.level==3) {
				if(game.player.y>950){
					if(charge && right) right();
					if(charge && !right) left();
				}
			}
			else if(Math.abs(dx)<500) {
				if(charge && right) right();
				if(charge && !right) left();
			}
		}
		if(defaultAnim==Animations.BISHOP) {
			if(t>=0.5) {
				game.sprites.add(new Projectile(x, y, Animations.ARROW,right));
				t = 0;
			}
		} else if(defaultAnim==Animations.PAWN) {
			if(checkCollision(game.player, right?x+50:x-50, y, 50, 50)) {
				game.player.collide(this, null);
			}
		} else if(defaultAnim==Animations.KNIGHT1||defaultAnim==Animations.KNIGHT2) {
			if(Math.abs(dx)>100) {
				if(this.right) right();
				else left();
			}
			if(game.player.x<10200 || game.player.x > 11375) return super.update(game, dt);
			if(t>=0.5) {
				double xdist = Math.abs(game.player.x - x);
				double ydist = Math.abs(game.player.y - y);
				double xoffset = (right?1:-1)*150*(xdist>ydist?2:1);
				double yoffset = (game.player.y>y?1:-1)*150*(xdist>ydist?1:2)+30;
				if(defaultAnim==Animations.KNIGHT1) {
					game.sprites.add(new Projectile(x+xoffset, y+yoffset, Animations.SPELL1,right));
				} else {
					game.sprites.add(new Projectile(x+xoffset, y+yoffset, Animations.SPELL2,right));
				}
				t = 0;
			}
		} else if(defaultAnim==Animations.ROOK) {
			if(checkCollision(game.player, right?x+100:x-100, y, 50, 50)) {
				game.player.collide(this, null);
			}
			if(Math.abs(dx)>200&&game.player.y>950) {
				if(this.right) right();
				else left();
				runAccel = 100;
				deccel = 1;
				maxVLeft = 500;
				maxVRight = 500;
			}
			this.right = this.xVelocity>0;
		}  else if(defaultAnim==Animations.BLACKKING) {
			if(Math.abs(game.player.x-x)<225 && Math.abs(game.player.y-y)<225) {
				x = (int) ((Math.round((game.player.x)/150))*150);
//				y = (int) ((Math.round((game.player.y)/150))*150);
				setAnim(Animations.KINGSWING);
				game.player.collide(this, null);
				this.right = false;
			}
		} 
		return super.update(game, dt);
	}
	
	@Override
	public void collide(GameObject obj, double[] delta) {
		if(defaultAnim==Animations.BLACKKING) return;
		if(obj instanceof Player) {
			if(defaultAnim==Animations.KING) {
				((Player)(obj)).game.rookSpeech = "Treason! Regicide!";
				((Player)(obj)).game.kingSpeech = "";
				((Player)(obj)).auto = true;
				this.defaultAnim = Animations.DEAD;
			} else {
				kill = true;
			}
		}
	}
}
