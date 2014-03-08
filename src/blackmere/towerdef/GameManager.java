package blackmere.towerdef;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import blackmere.towerdef.units.Enemy;
import blackmere.towerdef.units.Hero;
import blackmere.towerdef.units.Tower;
import blackmere.towerdef.units.Bullet;
import blackmere.towerdef.units.Unit;

public class GameManager extends BasicGame {
	
	private TiledMap demoMap;
	private ArrayList<Unit> allUnits;
	private Hero activeHero;
	private final float heroStartX = 180f;
	private final float heroStartY = 240f;
	private final float enemyStartX = 600f;
	private final float enemyStartY = 120f;
	private final float towerStartX = 60f;
	private final float towerStartY = 120f;
	

	public GameManager(String title) {
		super(title);
	}
	
	//
	public void init(GameContainer container) throws SlickException {
		demoMap = new TiledMap("res/demoMap.tmx");
		activeHero = new Hero(heroStartX, heroStartY);
		allUnits = new ArrayList<Unit>();
		allUnits.add(activeHero);
		allUnits.add(new Enemy(enemyStartX, enemyStartY));
		allUnits.add(new Tower(towerStartX, towerStartY));
	}

	//
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		demoMap.render(0, 0);
		ArrayList<Bullet> bullets = new ArrayList<Bullet>();
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		ArrayList<Tower> towers = new ArrayList<Tower>();
		
		// TODO: implement overall hierarchy (hero > bullets > enemy > tower)
		// divide units into lists according to type
		for (Unit u : getActiveUnits()) {
			if (u instanceof Bullet) {
				bullets.add((Bullet) u);
			} else if (u instanceof Enemy) {
				enemies.add((Enemy) u);
			} else if (u instanceof Tower) {
				towers.add((Tower) u);
			} else {
				continue;
			}
		}
		
		// first draw towers
		for (Tower t : towers) {
			t.draw();
		}
		
		// then draw enemies
		for (Enemy e : enemies) {
			e.draw();
		}
		
		// then draw bullets
		// TODO: why is bullet still not visible when enemy reaches tower? what if it had its own sprite?
		for (Bullet b : bullets) {
			b.draw(arg1);	// TODO: Bullet needs Graphics; optimize this?
		}
		
		// finally, draw the hero
		activeHero.draw();
	}
	
	//
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		// TODO: optimize?
		for (Unit u : getActiveUnits()) {
			if (u instanceof Tower) {
				towerLogic((Tower) u);
			} else if (u instanceof Enemy) {
				enemyLogic((Enemy) u);
			} else if (u instanceof Bullet) {
				bulletLogic((Bullet) u);
			}
		}

		if (! activeHero.isAttacking()) {
			if (input.isKeyDown(Input.KEY_LEFT)) {
				activeHero.move(Movement.LEFT, getActiveUnits());
			} else if (input.isKeyDown(Input.KEY_RIGHT)) {
				activeHero.move(Movement.RIGHT, getActiveUnits());
			} else if (input.isKeyDown(Input.KEY_UP)) {
				activeHero.move(Movement.UP, getActiveUnits());
			} else if (input.isKeyDown(Input.KEY_DOWN)) {
				activeHero.move(Movement.DOWN, getActiveUnits());
			} else if (input.isKeyDown(Input.KEY_SPACE)) {
				activeHero.attack(getActiveUnits());
			} else {
				activeHero.idle();
			}
		} else {
			// hero is attacking
			activeHero.checkAttack();
		}
	}
	
	private ArrayList<Unit> getActiveUnits() {
		ArrayList<Unit> activeUnits = new ArrayList<Unit>();
		
		for (Unit u : allUnits) {
			if (! u.isDead()) {
				activeUnits.add(u);
			}
		}
		
		return activeUnits;
	}
	
	private void towerLogic(Tower t) throws SlickException {
		for (Unit u : getActiveUnits()) {
			if (u instanceof Enemy) {
				if (t.timeToFire()) {
					allUnits.add(t.getBullet());
				}
				return;		// TODO: find a better way
			}
		}
	}
	
	private void enemyLogic(Enemy e) {
		e.checkAttack(getActiveUnits());
		e.move(getActiveUnits());
	}
	
	private void bulletLogic(Bullet b) {
		b.move();
		
		for (Unit u : getActiveUnits()) {
			if (u instanceof Enemy && b.detectCollision(b.getX(), b.getY(), u)) {
				u.takeDamage(b.getDamage());
				u.takeHit();
				b.die();
				return;
			}
		}
	}

	//
	public static void main(String[] args) {
		GameManager demoGame = new GameManager("Tower Defense Demo");
		AppGameContainer gameContainer;
		// TODO: redirect System.err to a log file

		try {
			gameContainer = new AppGameContainer(demoGame);
			gameContainer.setDisplayMode(660, 420, false);
			gameContainer.setShowFPS(false);
			gameContainer.start();
		} catch (SlickException e) {

			File logFile = new File("logs/errors.log");

			if (! logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e1) {
					System.err.println("Error creating log file");
				}
			}

			FileWriter fw = null;
			BufferedWriter bw = null;

			try {
				fw = new FileWriter(logFile.getAbsoluteFile());
				bw = new BufferedWriter(fw);
			} catch (IOException e1) {
				System.err.println("Error creating log file writers");
			}

			try {
				bw.write(e.toString());
			} catch (IOException e1) {
				System.err.println("Error writing to log file");
			}

			try {
				fw.close();
				bw.close();
			} catch (IOException e1) {
				System.err.println("Error closing log file writers");
			}
		} 
	}
}
