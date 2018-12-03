package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import game.GameObject.Animations;

public class Game {
	
	private JFrame frame;
	public Camera camera;
	public static final String NAME = "En Passant";
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 755+40;
	public static final int MAX_STEP = 50000000;
	public static final int FRAME_RATE = 12;
	public int MIN_STEP = 40000000;
	public int windowWidth;
	public int windowHeight;
	public boolean isRunning, reset = false;
	public Player player;
	Background background = new Background(1280,755, Animations.BACKGROUND);
	Background foreground = new Background(1280,755, Animations.FOREGROUND);
	Background splash = new Background(1280,755, Animations.SPLASH);
	AudioPlayer audio = new AudioPlayer();
	String kingSpeech = "";
	String blackSpeech = "";
	String blackSpeech2 = "";
	String rookSpeech = "";
	double elapsedTime = 0;
	int level = 1;
	public boolean leftPressed, rightPressed, upPressed, downPressed = false;
	int[] levelmarkers = new int[]{7350, 12450, 17850, -1};
	boolean begun = false;
	double deathTimer = 0;
	int dialogue = 1;
	
	public int delete_this_variable = 0;
	public double test_local_time = 0;
	
	public ArrayList<GameObject> sprites = new ArrayList<GameObject>();
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public Game() {
		this(WIDTH, HEIGHT);
	}
	
	@SuppressWarnings("serial")
	public Game(int windowWidth, int windowHeight) {
		loadAllAnimations();
		frame = new JFrame(NAME);
		camera = new Camera();
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		frame.setSize(windowWidth, windowHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.add(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				draw(g);
			}
		});
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent ke) {
				if(!begun) {
					setup();
					begun = true;
					return;
				}
				switch(ke.getKeyCode()) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W: upPressed = true; break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S: player.down(); break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A: leftPressed = true; break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D: rightPressed = true; break;
//				case KeyEvent.VK_P: level++; break;
				case KeyEvent.VK_SPACE: player.swing();
				break;
				}
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				switch(ke.getKeyCode()) {
				case KeyEvent.VK_UP: 
				case KeyEvent.VK_W: upPressed = false; break;
				case KeyEvent.VK_LEFT: 
				case KeyEvent.VK_A: leftPressed = false; break;
				case KeyEvent.VK_RIGHT: 
				case KeyEvent.VK_D: rightPressed = false; break;
				}
			}

			@Override
			public void keyTyped(KeyEvent ke) {
				// TODO Auto-generated method stub
				
			}
		});
		level = 1;
		
		//setup();
		kingSpeech = "Go forth, my pawn.";
	}
	
	public void setup() {
		if(player!=null) {
			if(elapsedTime < 8 && player.y<2000 && level==1) {
				kingSpeech = "Slow down, this isn't blitz!";
			}
			if(elapsedTime > 60) {
				kingSpeech = "Long time no see!";
			}
		}
		if(level==4) {
			switch(dialogue) {
			case 1: kingSpeech = "Yes, I know you'll get recaptured, but we'll come out ahead!";break;
			case 2: kingSpeech = "Stop resetting, just take the queen!";break;
			case 3: kingSpeech = "I don't care if you \"don't want to die,\" this is a war!";break;
			default: kingSpeech = "Last chance. Sacrifices must be made!";break;
			}
			dialogue++;
		}
		sprites.clear();
		loadLevel(level, true);
		AudioPlayer music = new AudioPlayer("Capture.wav");
		music.play();
		elapsedTime = 0;
		reset = false;
	}
	
	public void loadLevel(int i, boolean dead) {
		try {
			Scanner in = new Scanner(new FileReader("level"+i+".txt"));
			int k = 150;
			int y = 0;
			int o = level>1 ? levelmarkers[level-2] : 0;
			while(in.hasNext()) {
				String s = in.nextLine();
				for(int x=0; x<s.length(); x++) {
					char c = s.charAt(x);
					switch(c) {
					case '1': sprites.add(new Platform(x*k+o, y*k, 3, k, k, Animations.PLATFORM)); break;
					case '_': sprites.add(new Platform(x*k+o, y*k-(k-30)/2, 3, k, 20, Animations.FLATFORM)); break;
					case '*': if(!dead) break;
						player = new Player(x*k+o, y*k, this); sprites.add(player); break;
					case 'p': sprites.add(new Enemy(x*k+o, y*k, Animations.PAWN,1, false)); break;
					case 'P': sprites.add(new Enemy(x*k+o, y*k, Animations.PAWN,1, true)); break;
					case 'B': sprites.add(new Enemy(x*k+o, y*k, Animations.BISHOP,1.8, false)); break;
					case 'N': sprites.add(new Enemy(x*k+o, y*k, Animations.KNIGHT1,1.8, false)); break;
					case 'n': sprites.add(new Enemy(x*k+o, y*k, Animations.KNIGHT2,1.8, false)); break;
					case 'R': sprites.add(new Enemy(x*k+o, y*k, Animations.ROOK,1.8, false)); break;
					case 'k': sprites.add(new Enemy(x*k+o, y*k, Animations.BLACKKING,1.8, false)); break;
					case 'Q': sprites.add(new Enemy(x*k+o, y*k, Animations.BLACKQUEEN,1.8, false)); break;
					case 's': sprites.add(new Item(x*k+o, y*k, Animations.SPEAR, Animations.STABUP)); break;
					case 'S': sprites.add(new Item(x*k+o, y*k, Animations.SWORD, Animations.SWING)); break;
					case 'K': 
						if(dialogue<=4) {
							sprites.add(new Ally(x*k+o, y*k, 2, Animations.KING));break;
						} else {
							sprites.add(new Enemy(x*k+o, y*k, Animations.KING,1.8, false)); break;
						}
					case 'r': sprites.add(new Ally(x*k+o, y*k, 2, Animations.WHITEROOK)); break;
					}
				}
				y++;
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start the timer
	 */
	public void start() {
		isRunning = true;
		long then = System.nanoTime();
		long now = then;
		long dt;
		while (isRunning) {
			now = System.nanoTime();
			dt = now - then;
			then = now;
			if (dt > 0) {
				update(Math.min(MAX_STEP, dt)/1000000000.0);
			}
			frame.repaint();
			try{
				Thread.sleep(Math.max((MIN_STEP - (System.nanoTime()-then))/1000000, 0));
			} catch (Exception e) {}
		}
		frame.dispose();
	}

	/**
	 * Update the game model
	 * @param dt - elapsed time in seconds
	 */
	public void update(double dt) {
		if(!begun) {
			splash.update(this, dt/5);
			return;
		}
		if(player.kill) {
			deathTimer += dt;
			if(deathTimer > 0.5) {
				deathTimer = 0;
				setup();
				return;
			}
		}
		if(reset) {
			setup();
			return;
		}
		if(levelmarkers[level-1]>0&&player.x>levelmarkers[level-1]) {
			level++;
			elapsedTime = 0;
			this.loadLevel(level, false);
			switch(level) {
			case 2: kingSpeech = "Nice opening! Now on to the midgame...";break;
			case 3: kingSpeech = "The endgame draws near...";break;
			case 4: kingSpeech = "We've got them cornered, now's your chance to take the queen!";break;
			}
			System.out.println("Level: "+level);
		}
		elapsedTime += dt;
		try{
		// Put code for user interface before camera update, so slowdowns
		// don't affect UI elements.
		
		dt = camera.update(dt);	//	dt changes values here based on camera speed
		Collections.sort(sprites);
		// Update GameObjects and store desired movements
		double[] dx = new double[sprites.size()];
		double[] dy = new double[sprites.size()];
		if(upPressed) player.up();
		if(downPressed) player.down();
		if(leftPressed) player.left();
		if(rightPressed) player.right();
		for(int i=0; i<sprites.size(); i++) {
			double[] delta = sprites.get(i).update(this, dt);
			dx[i] = delta[0];
			dy[i] = delta[1];
		}
		if(sprites.size()==0) return;
		GameObject s1 = sprites.get(sprites.size()-1);
		if(s1 instanceof PlatformingObject) {
			((PlatformingObject)s1).toground = false;
		}
		// Collide GameObjects and carry out movements
		for(int i=0; i<sprites.size()-1; i++) {
			s1 = sprites.get(i);
			if(s1 instanceof PlatformingObject) {
				((PlatformingObject)s1).toground = false;
			}
			for(int j=i+1; j<sprites.size(); j++) {
				GameObject s2 = sprites.get(j);
				double[] delta = new double[]{0,0,0,0};
				if(s1.isCollidable(s2)) { // s2 can block s1
					delta = s1.checkCollision(dx[i], dy[i], dx[j], dy[j], s2);
					dx[i] = delta[0];
					dy[i] = delta[1];
					if(delta[2]==1 && s1 instanceof PlatformingObject) {
						((PlatformingObject)s1).xVelocity = 0;
					}
					if(delta[3]==1 && s1 instanceof PlatformingObject) {
						((PlatformingObject)s1).yVelocity = 0;
					}
				} else if(s2.isCollidable(s1)) { // s1 can block s2
					delta = s2.checkCollision(dx[j], dy[j], dx[i], dy[i], s1);
					dx[j] = delta[0];
					dy[j] = delta[1];

					if(delta[2]==1 && s2 instanceof PlatformingObject) {
						((PlatformingObject)s2).xVelocity = 0;
					}
					if(delta[3]==1 && s2 instanceof PlatformingObject) {
						((PlatformingObject)s2).yVelocity = 0;
					}
				} else if(s1.isInteractable(s2)||s2.isInteractable(s1)) { // no blocking
					delta = s2.checkCollision(dx[j], dy[j], dx[i], dy[i], s1);
				}
				if(delta[2]>0||delta[3]>0) { // collision occurred
					s2.collide(s1, delta);
					s1.collide(s2, delta);
				}
			}
			s1.move(dx[i], dy[i]);
		}
		sprites.get(sprites.size()-1).move(dx[sprites.size()-1], dy[sprites.size()-1]);
		for(int i=0; i<sprites.size(); i++) {
			if(sprites.get(i) instanceof PlatformingObject) {
				((PlatformingObject)sprites.get(i)).grounded=((PlatformingObject)sprites.get(i)).toground;
			}
			if(sprites.get(i).kill) sprites.remove(i);
		}
		if(player.y > 2000) {
			String[] options = {//"Did you know? There's a jump button!",
					//"You should try to avoid the bottomless pit...",
					"Falling to your death is not a legal move"};
			kingSpeech = options[(int)(Math.random()*options.length)];
			setup();
		}
		} catch(Exception e) {
			
		}
	}

	/**
	 * Draw the graphics
	 * @param g - the game's Graphics context
	 */
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
//		long xfoc = (int)(Math.sin(System.nanoTime()/1000000000.0)*(500)) - 640;
//		long yfoc = (int)(Math.cos(System.nanoTime()/1500000000.0)*(320)) - 360;
//		double zoom = Math.sin(System.nanoTime()/2500000000.0)*0.5 + 1.0;
//		xfoc = -WIDTH/2;
//		yfoc = -HEIGHT/2;
//		zoom = 1.0;
//		camera.set_target_pos(xfoc, yfoc);
		if(!begun) {
			splash.update(this, 0);
			splash.draw(g2);
			return;
		}
		background.update(this, 0);
		background.draw(g);
		if(player==null) {
			return;
		}
		camera.set_target_pos(-player.x, -player.y-50);
		camera.zoom.set_target_value(1);
		g2.scale(camera.get_zoom(), camera.get_zoom());
		g2.translate((int)(camera.get_x_pos() + WIDTH/(2*camera.get_zoom())), 
				(int)(camera.get_y_pos() + HEIGHT/(2*camera.get_zoom())));
		

		g2.setFont(new Font("Candara", Font.BOLD, 30));
		g2.drawString(kingSpeech, 25+(level>1?levelmarkers[level-2]:0), 700);
		g2.drawString(blackSpeech, 18600+450, 750-140);
		g2.drawString(blackSpeech2, 18600+450, 800-130);
		g2.drawString(rookSpeech, 18050, 750);
		try {
			for (GameObject sprite:sprites) {
				sprite.draw(g);
			}
		} catch(java.util.ConcurrentModificationException e) {
			
		}
		g2.scale(1/camera.get_zoom(), 1/camera.get_zoom());
		g2.translate(-(int)(camera.get_x_pos() + WIDTH/(2*camera.get_zoom())), 
				-(int)(camera.get_y_pos() + HEIGHT/(2*camera.get_zoom())));
		
//		foreground.update(this, 0);
//		foreground.draw(g);
//		g.fillRect((int)(player.right?player.x+player.w:player.x-player.w)-35, 
//				(int)(player.y+player.h*3/4)-35, 70, 70);

//		if(player.inventory.size()>0) {
//			Item item1 = player.inventory.get(player.index);
//			item1.visible = true;
//			item1.x = 115;
//			item1.y = 120;
//			item1.sprite.xscale *= item1.k;
//			item1.sprite.yscale *= item1.k;
//			item1.update(this, 0);
//			item1.sprite.xscale /= item1.k;
//			item1.sprite.yscale /= item1.k;
//			item1.draw(g);
//			item1.visible = false;
//		}
//		if(player.inventory.size()>1) {
//			int i2 = (player.index+1)%player.inventory.size();
//			Item item2 = player.inventory.get(i2);
//			item2.visible = true;
//			item2.x = 270;
//			item2.y = 120;
//			item2.sprite.xscale *= item2.k*.7;
//			item2.sprite.yscale *= item2.k*.7;
//			item2.update(this, 0);
//			item2.sprite.xscale /= item2.k*.7;
//			item2.sprite.yscale /= item2.k*.7;
//			item2.draw(g);
//			item2.visible = false;
//		}
	}
	
	/**
	 * Loads all animation files
	 */
	public static void loadAllAnimations() {
		for(Animations a:Animations.values()) {
			Sprite.loadAnimation(a);
		}
	}
}
