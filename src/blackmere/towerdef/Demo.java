package blackmere.towerdef;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import blackmere.towerdef.units.Enemy;
import blackmere.towerdef.units.Hero;
import blackmere.towerdef.units.Tower;
import blackmere.towerdef.units.Bullet;
import blackmere.towerdef.units.Unit;
import blackmere.towerdef.util.Direction;
import static blackmere.towerdef.util.Constants.*;

public class Demo extends BasicGameState {
	
	private StateBasedGame gameManager;
	private TiledMap demoMap;
	private ArrayList<Unit> allUnits;
	private Hero activeHero;
		
	//
	public void init(GameContainer container, StateBasedGame manager) throws SlickException {
		gameManager = manager;
		demoMap = new TiledMap("res/basicMap.tmx");
		activeHero = new Hero(heroStartX, heroStartY);
		allUnits = new ArrayList<Unit>();
		allUnits.add(activeHero);
		allUnits.add(new Enemy(enemyStartX, enemyStartY));
		allUnits.add(new Tower(towerStartX, towerStartY));
	}

	//
	public void render(GameContainer container, StateBasedGame manager, Graphics g) throws SlickException {
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
			t.draw(g);
		}
		
		// then draw enemies
		for (Enemy e : enemies) {
			e.draw(g);
		}
		
		// then draw bullets
		for (Bullet b : bullets) {
			b.draw(g);
		}
		
		// finally, draw the hero, as long as they're currently alive
		if (!activeHero.isDead()) {
			activeHero.draw(g);		// TODO: document why passing in g (for the sake of the DEBUG code for bounding boxes)
		}
	}
	
	//
	public void update(GameContainer container, StateBasedGame manager, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		if (input.isKeyPressed(Input.KEY_P)) {
			Image bg = new Image(windowWidth, windowHeight);
			container.getGraphics().copyArea(bg, 0, 0);
			((PauseMenu) gameManager.getState(pauseID)).setBackground(bg);
			gameManager.enterState(pauseID);
		}
		
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
				activeHero.idle();
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
			if (u instanceof Enemy && u.getLane() == t.getLane() && t.timeToFire()) {
				allUnits.add(t.getBullet());
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
	public int getID() {
		return demoID;
	}
}
