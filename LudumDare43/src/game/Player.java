package game;

import java.awt.Graphics;
import java.util.ArrayList;

import game.GameObject.Animations;

public class Player extends PlatformingObject {
	
	ArrayList<Item> inventory = new ArrayList<Item>();
	int index = 0;
	Game game;
	Sprite target;
	boolean ul, ur, dl, dr;
//	AudioPlayer capture = new AudioPlayer("Capture.wav");


	public Player(int x, int y, Game g) {
		super(x, y, 5, Animations.IDLE);
		this.game = g;
		target = new Sprite();
		target.xscale = target.yscale = .25;
		target.animate(Animations.TARGET, 0, false);
		inventory.add(new Item(0, 0, Animations.SPEAR, Animations.STABUP));
	}

	public boolean isInteractable(GameObject obj) {
		return (obj instanceof Item) || super.isInteractable(obj);
	}

	public void collide(GameObject obj, double[] delta) {
		if(obj.kill||this.kill) return;
		super.collide(obj, delta);
		if(obj instanceof Item) {
			inventory.add((Item) obj);
		}
		if(obj instanceof Enemy && !obj.kill) {
			kill = true;

			String[] options = {"Work the angles - you can only capture diagonally.",
					"Remember, chess is a game of strategy."
					};
			game.kingSpeech = options[(int)(Math.random()*options.length)];
			Animations a = ((Enemy)obj).defaultAnim;
			if(a==Animations.ROOK){
				options = new String[]{"Don't take the bait!"};
				game.kingSpeech = options[(int)(Math.random()*options.length)];
			}
		}
		if(obj instanceof Projectile) {
			Animations a = ((Projectile)obj).a;
			kill = true;

			if(a==Animations.ARROW){
				String[] options = {"The Fianchettoed Fortress is a dangerous place..."};
				game.kingSpeech = options[(int)(Math.random()*options.length)];
			}
			if(a==Animations.SPELL1||a==Animations.SPELL2){
				String[] options = {"Knights don't cover every square..."};
				game.kingSpeech = options[(int)(Math.random()*options.length)];
			}
		}
	}
	
	public void findTargets() {
		if(game.dialogue>4 && x > 19050.0-300) return;
		ur = ul = dr = dl = false;
		for(GameObject s:game.sprites) {
			int x0 = (int) ((Math.round((x)/150))*150);
			int y0 = (int)((Math.round((y)/150))*150);
			if(s instanceof Enemy) {
				if(checkCollision(s, right?x0+130:x0-130, y0-150, 150, 150)) {
					boolean blocked = false;
					for(GameObject s2:game.sprites) {
						if(s2 instanceof Platform) {
							if(checkCollision(s2, right?x0+150:x0-150, y0-150, 100, 100)) {
								blocked = true;
							}
						}
					}
					if(!blocked) {
						if(right) ur = true;
						else ul = true;
						return;
					}
				}
				if(checkCollision(s, right?x0+130:x0-130, y0+150, 150, 150)) {
					boolean blocked = false;
					for(GameObject s2:game.sprites) {
						if(s2 instanceof Platform) {
							if(checkCollision(s2, (right?x0+150:x0-150), y0+150, 100, 100)) {
								blocked = true;
							}
						}
					}
					if(!blocked) {
						if(right) dr = true;
						else dl = true;
						return;
					}
				}
			}
		}
	}

	public void swing() {
		try {
		if(game.reset) return;
		if(game.dialogue>4 && x > 19050.0-450) return;
		if(inventory.size()>0) {
			setAnim(inventory.get(index).b);
		}
		if(inventory.size()==0) return;
		if(inventory.get(index).a==Animations.SPEAR) {
			for(GameObject s:game.sprites) {
				int x0 = (int) ((Math.round((x)/150))*150);
				int y0 = (int)((Math.round((y)/150))*150);
				if(s instanceof Enemy) {
					if(checkCollision(s, right?x0+130:x0-130, y0-150, 150, 150)) {
						boolean blocked = false;
						for(GameObject s2:game.sprites) {
							if(s2 instanceof Platform) {
								if(checkCollision(s2, right?x0+150:x0-150, y0-150, 100, 100)) {
									blocked = true;
								}
							}
						}
						if(!blocked) {
							s.collide(this, null);
							setAnim(Animations.STABUP);
							x = x0+(right?150:-150);
							y = y0-150;
//							capture.play();
							return;
						}
					}
					if(checkCollision(s, right?x0+130:x0-130, y0+150, 150, 150)) {
						boolean blocked = false;
						for(GameObject s2:game.sprites) {
							if(s2 instanceof Platform) {
								if(checkCollision(s2, (right?x0+150:x0-150), y0+150, 100, 100)) {
									blocked = true;
								}
							}
						}
						if(!blocked) {
							s.collide(this, null);
							setAnim(Animations.STABDOWN);
							x = x0+(right?150:-150);
							y = y0+150;
//							capture.play();
							return;
						}
					}
				}
			}
		} else if(inventory.get(index).a==Animations.SWORD) {
			for(GameObject s:game.sprites) {
				if(s instanceof Enemy) {
					if(checkCollision(game.player, right?x+50:x-50, y, 50, 50)) {
						game.player.collide(this, null);
					}
				}
			}
		}
		} catch (Exception e) {
			
		}
	}
	
//	@Override
//	public double[] update(Game game, double dt) {
//		boolean done = this.sprite.animate(currentAnim, dt, !right);
//		if (yVelocity<-100) game.upPressed = false;
//		if(done) setAnim(defaultAnim);
//		return applyMovement(dt);
//		
//	}

	public void down() {
		if(inventory.size()==0) return;
		index++;
		index %= inventory.size();
	}

	/**
	 * Renders the sprite
	 * @param g - the Graphics context with appropriate translation and scaling
	 */
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		if(!visible) return;
		int x0 = (int) ((Math.round((x)/150))*150);
		int y0 = (int)((Math.round((y)/150))*150);
		g.translate(x0, y0);
		findTargets();
		if(ul) {
			g.translate(-150, -150);
			target.draw(g, this);
			g.translate(150, 150);
		}
		if(ur) {
			g.translate(150, -150);
			target.draw(g, this);
			g.translate(-150, 150);
		}
		if(dl) {
			g.translate(-150, 150);
			target.draw(g, this);
			g.translate(150, -150);
		}
		if(dr) {
			g.translate(150, 150);
			target.draw(g, this);
			g.translate(-150, -150);
		}
		g.translate(-x0, -y0);
	}

	@Override
	public void right() {
		if(game.dialogue>4 && x > 19050.0-300) return;
		super.right();
	}
}
