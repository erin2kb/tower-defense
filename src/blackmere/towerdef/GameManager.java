package blackmere.towerdef;

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
import blackmere.towerdef.util.Direction;
import static blackmere.towerdef.util.Constants.*;

public class GameManager extends BasicGame {
	
	private TiledMap demoMap;
	private ArrayList<Unit> allUnits;
	private Hero activeHero;
	
	// constructor
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
			t.draw(arg1);
		}
		
		// then draw enemies
		for (Enemy e : enemies) {
			e.draw(arg1);
		}
		
		// then draw bullets
		for (Bullet b : bullets) {
			b.draw(arg1);
		}
		
		// finally, draw the hero, as long as they're currently alive
		if (!activeHero.isDead()) {
			activeHero.draw(arg1);		// TODO: document why passing in arg1 (for the sake of the DEBUG code for bounding boxes)
		}
	}
	
	//
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		for (Unit u : getActiveUnits()) {
			if (u instanceof Tower) {
				towerLogic((Tower) u);
			} else if (u instanceof Enemy) {
				enemyLogic((Enemy) u);
			} else if (u instanceof Bullet) {
				bulletLogic((Bullet) u);
			}
		}

		if (activeHero.isDead()) {
			return;		// don't do any more processing if hero is dead
		} else if (! activeHero.isAttacking()) {
			if (input.isKeyDown(Input.KEY_LEFT)) {
				activeHero.move(Direction.LEFT, getActiveUnits());
			} else if (input.isKeyDown(Input.KEY_RIGHT)) {
				activeHero.move(Direction.RIGHT, getActiveUnits());
			} else if (input.isKeyDown(Input.KEY_UP)) {
				activeHero.move(Direction.UP, getActiveUnits());
			} else if (input.isKeyDown(Input.KEY_DOWN)) {
				activeHero.move(Direction.DOWN, getActiveUnits());
			} else if (input.isKeyDown(Input.KEY_SPACE)) {
				activeHero.attack(getActiveUnits());
			} else {
				activeHero.idle();		// TODO: switch statement?
			}
		} else {
			// hero is alive and currently attacking
			activeHero.checkAttack();
		}
	}
	
	// TODO: documentation and comments
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
			if (u instanceof Enemy && b.detectBulletCollision((Enemy) u)) {
				u.takeHit(b.getDamage());
				b.die();
				return;
			}
		}
	}

	//
	public static void main(String[] args) {
		GameManager demoGame = new GameManager("Tower Defense Demo");
		AppGameContainer gameContainer;
		// TODO: redirect System.err to a log file; test these log files

		try {
			gameContainer = new AppGameContainer(demoGame);
			gameContainer.setDisplayMode(660, 420, false);
			gameContainer.setShowFPS(false);
			gameContainer.start();
		} catch (SlickException e) {

			/*File logFile = new File("logs/errors.log");

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
			} */
		} 
	}
}
