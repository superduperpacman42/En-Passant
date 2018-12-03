package game;

import java.awt.Graphics;

import game.GameObject.Animations;

/**
 * A game element capable of displaying animations
 */
public abstract class GameObject implements Comparable<GameObject> {
	
	protected Sprite sprite;
	public double x, y;
	public int z, w, h;
	public boolean visible = true, kill = false;
	
	/**
	 * Animations that Sprites can display
	 */
	public enum Animations {
		SWING("WhitePawnSwing", 6, 6),
		STABUP("WhitePawnStabUp", 4, 4),
		STABDOWN("WhitePawnStabDown", 4, 4),
		IDLE("WhitePawnIdle", 6, 6),
		PAWN("BlackPawn", 6, 6),
		KING("KingIdle", 6, 6),
		BLACKKING("BlackKing", 6, 6),
		BLACKQUEEN("BlackQueen", 1, 1),
		DEAD("DeadKing", 1, 1),
		TARGET("Target2", 1, 1),
		BACKGROUND("Background", 1, 1),
		FOREGROUND("Foreground", 1, 1),
		SPLASH("Splash", 2, 2),
		ROOK("RookCharge", 6, 6),
		WHITEROOK("WhiteRook", 6, 6),
		SWORD("Sword",1,1),
		SPEAR("Spear",1,1),
		SPELL1("Spell1",7,7),
		SPELL2("Spell2",7,7),
		PLATFORM("Platform", 1, 1),
		FLATFORM("Flatform", 1, 1),
		KNIGHT1("KnightIdle", 6, 6),
		KNIGHT2("KnightIdle2", 6, 6),
		ARROW("Arrow", 1, 1),
		KINGSWING("KingSwing", 6, 6),
		BISHOP("BishopShoot", 6, 6);
		
		public String filename;
		public int columns;
		public int frames;
		private Animations(String filename, int columns, int frames) {
			this.filename = filename;
			this.columns = columns;
			this.frames = frames;
		}
	}
	
	/**
	 * Superclass constructor
	 * @param x - initial x-coordinate in pixels
	 * @param y - initial y-coordinate in pixels
	 * @param z - relative layer to display sprite on-screen
	 * @param w - width of GameObject for collisions in pixels
	 * @param h - height of GameObject for collisions in pixels
	 */
	public GameObject(double x, double y, int z, int w, int h, double scale) {
		this.sprite = new Sprite();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		if(scale<0) {
			this.sprite.xscale = -w*scale;
			this.sprite.yscale = -h*scale;
		} else {
			this.sprite.xscale = this.sprite.yscale = scale;
		}
	}
	
	/**
	 * Updates the sprite behavior and animation
	 * @param dt - elapsed time in seconds
	 * @return An array containing horizontal and vertical displacement in pixels
	 */
	public abstract double[] update(Game game, double dt);

	/**
	 * Checks if another GameObject can block this GameObject's motion
	 * @param obj - GameObject being interacted with
	 * @return If the other GameObject can block this GameObject
	 */
	public boolean isCollidable(GameObject obj) {
		return false;
	}
	
	/**
	 * Checks if another GameObject can interact with this GameObject
	 * @param obj - GameObject being interacted with
	 * @return If the other GameObject can interact with this GameObject
	 */
	public boolean isInteractable(GameObject obj) {
		return isCollidable(obj);
	}
	
	/**
	 * Called when another GameObject collides with this one
	 * @param obj - The object that collided with this object
	 */
	public void collide(GameObject obj, double[] delta) {
		return;
	}
	
	/**
	 * Renders the sprite
	 * @param g - the Graphics context with appropriate translation and scaling
	 */
	public void draw(Graphics g) {
		if(!visible) return;
		g.translate((int)x, (int)y);
		sprite.draw(g, this);
		g.translate((int)-x, (int)-y);
	}
	
	/**
	 * Determines if a sprite can move into a given space
	 * @param dx - amount sprite is moving horizontally in pixels
	 * @param dy - amount sprite is moving vertically in pixels
	 * @param dx2 - amount other sprite is moving horizontally in pixels
	 * @param dy2 - amount other sprite is moving vertically in pixels
	 * @param obj - the other sprite to collide with
	 * @return Allowed dx, allowed dy, and 1 if a collision occurred
	 */
	public double[] checkCollision(double dx, double dy, double dx2, double dy2, GameObject obj) {
		int x1 = (int)(x+dx);
		int y1 = (int)(y+dy);
		int x2 = (int)(obj.x+dx2);
		int y2 = (int)(obj.y+dy2);
		double stopx = 0, stopy = 0;
		boolean overlapx = (x1+w/2)-1>(x2-obj.w/2) && (x1-w/2)+1<(x2+obj.w/2);
		boolean overlapy = (y1+h/2)>(y2-obj.h/2) && (y1-h/2)+1<(y2+obj.h/2);
		if(overlapx && overlapy) { // check collision
			int W = Math.max(w, obj.w);
			int H = Math.max(h, obj.h);
			if(Math.abs(x1-dx-x2+dx2)*H > Math.abs(y1-dy-y2+dy2)*W && (y1+h/2)>(y2-obj.h/2)+1 && (y1-h/2)+1<(y2+obj.h/2)) { // hit horizontal
				if(x2 > x1) { // hit right
					if(dx > (x2-x1)-(w/2+obj.w/2)) {
						stopx = 1;
						dx += (x2-x1)-(w/2+obj.w/2)+1;
					}
				} else { // hit left
					if(dx < (x2-x1)+(w/2+obj.w/2)) {
						stopx = 1;
						dx += (x2-x1)+(w/2+obj.w/2)-1;
					}
				}
			} else { // hit vertical
				if(y2 > y1) { // hit down
					if(dy > (y2-y1)-(h/2+obj.h/2)) {
						stopy = 1;
						dy += (y2-y1)-(h/2+obj.h/2)+1;
					}
				} else { // hit up
					if(dy < (y2-y1)+(h/2+obj.h/2)) {
						stopy = 1;
						dy += (y2-y1)+(h/2+obj.h/2)-1;
					}
				}
			}
			return new double[]{dx, dy, stopx, stopy};
		}
		return new double[]{dx, dy, 0, 0};
	}
	
	public boolean checkCollision(GameObject obj, double x, double y, double w, double h) {
		int x2 = (int)(obj.x);
		int y2 = (int)(obj.y);
		if(obj instanceof Enemy) {
			Enemy e = (Enemy)obj;
			if(e.defaultAnim==Animations.BLACKKING) return false;
			if(e.defaultAnim==Animations.KNIGHT1||e.defaultAnim==Animations.KNIGHT2) {
				boolean overlapx = (x+w/2)>(x2-obj.w/2) && (x-w/2)<(x2+obj.w/2);
				boolean overlapy = (y+h/2)>(y2) && (y-h/2)<(y2+obj.h/3);
				return(overlapx && overlapy);
			}
			if(e.defaultAnim==Animations.ROOK) {
				boolean overlapx = (x+w/2)>(x2-obj.w/2) && (x-w/2)<(x2+obj.w/2);
				boolean overlapy = (y+h/2)>(y2) && (y-h/2)<(y2+obj.h/3);
				return(overlapx && overlapy);
			}
		}
		boolean overlapx = (x+w/2)>(x2-obj.w/2) && (x-w/2)<(x2+obj.w/2);
		boolean overlapy = (y+h/2)>(y2-obj.h/2) && (y-h/2)<(y2+obj.h/2);
		return(overlapx && overlapy);
	}
	
	/**
	 * Determines if a solid object is directly underneath this one
	 * @param game
	 * @return
	 */
	public boolean checkFloor(Game game) {
		for(GameObject sprite:game.sprites) {
			if(isCollidable(sprite)) {
				if(checkCollision(0, 1, 0, 0, sprite)[1]<1) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Move GameObject by a set distance
	 * @param dx - horizontal movement in pixels
	 * @param dy - vertical movement in pixels
	 */
	public void move(double dx, double dy) {
		x += dx;
		y += dy;
	}
	
	@Override
	public int compareTo(GameObject obj) {
		return z - obj.z;
	}
}