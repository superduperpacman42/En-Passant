package game;

public class PlatformingObject extends GameObject{
	
	Animations currentAnim, defaultAnim;
	
	float xVelocity = 0;
	float yVelocity = 0;
	float effectiveX;
	float effectiveY;
	boolean grounded = false, toground = false, canJump = false;
	boolean right = true;
	boolean auto = false;
	double endtimer = 0;
	
	// Maximum velocities in each direction
	float maxVUp = 500;
	float maxVDown = 500;
	float maxVLeft = 500;
	float maxVRight = 500;
	
	// Platforming parameters
	float gravity = 3000;
	float jumpPower = 1100;
	float runAccel = 200;
	float deccel = 500;

	public PlatformingObject(int x, int y, int z, Animations defaultAnim) {
		super(x, y, z, 50, 120, .25);
		this.defaultAnim = defaultAnim;
		setAnim(defaultAnim);
	}
	
	@Override
	public double[] update(Game game, double dt) {
		if(auto) {
			if(x < 19050.0-450+450) {
				if(xVelocity<maxVRight/4) xVelocity += runAccel;
			} else {
				endtimer+=dt;
				double k = 1.5;
				if(endtimer < 1*k) {
					game.blackSpeech = "Well that's checkmate.";
				} else if(endtimer <2*k) {
					game.blackSpeech = "Thank you for your help, ";
					game.blackSpeech2 =  "that was a close match.";
				} else if(endtimer <3*k) {
					game.blackSpeech = "Most pawns are used to following orders, ";
					game.blackSpeech2 =   "but you're an exception.";
				} else if(endtimer <4*k) {
					game.blackSpeech = "So congratulations! You avoided  ";
					game.blackSpeech2 = "sacrificing yourself...";
				} else if(endtimer <8*k) {
					game.blackSpeech = "But think about what you gave up ";
					game.blackSpeech2 = "in the process...";
				} else {
					game.isRunning = false;
				}
			}
		}
		boolean done = this.sprite.animate(currentAnim, dt, !right);
		if (yVelocity<-100) game.upPressed = false;
		if(done) setAnim(defaultAnim);
		return applyMovement(dt);
	}
	
	public double[] applyMovement(double dt) {
		
		// Cap velocities at maximum values before applying movement
		effectiveX = Math.max(xVelocity, -maxVLeft);
		effectiveX = Math.min(xVelocity, maxVRight);
		effectiveY = Math.max(yVelocity, -maxVUp);
		effectiveY = Math.max(yVelocity, maxVDown);
		
		effectiveX = xVelocity;
		effectiveY = yVelocity;
		
		double dx = effectiveX*dt;
		double dy = effectiveY*dt;
		
//		x += dx;
//		y += dy;

		if(!grounded) {
			yVelocity += gravity*dt;
		}
		
//		System.out.println(yVelocity*1000);

		xVelocity/=Math.pow(deccel, dt);
//		if (y>=640 && effectiveY >= 0) {
//			dy = 640-y;
//		}
		if(dx>0) right = true;
		if(dx<0) right = false;
		return new double[]{dx, dy};
	}
	
	public void jump() {
		if(grounded) yVelocity = -jumpPower;
		grounded = false;
	}
	
	public void setAnim(Animations newAnim) {
		currentAnim = newAnim;
	}

	@Override
	public boolean isCollidable(GameObject obj) {
		return (obj instanceof Platform);
	}

	public boolean isInteractable(GameObject obj) {
		return (obj instanceof Platform);
	}

	public void collide(GameObject obj, double[] delta) {
		if(obj instanceof Platform && delta[3]==1 && obj.y>y) {
			grounded = toground = true;
		}
	}
	
	public void up() {
		if(auto) return;
		if (grounded) {
			jump();
		}
	}
	

	public void left() {
		if(auto) return;
		if(xVelocity>-maxVLeft) xVelocity -= runAccel;
	}
	

	public void right() {
		if(auto) return;
		if(xVelocity<maxVRight) xVelocity += runAccel;
	}

}
