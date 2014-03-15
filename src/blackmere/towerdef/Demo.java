package blackmere.towerdef;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import blackmere.towerdef.ui.Button;
import blackmere.towerdef.units.Enemy;
import blackmere.towerdef.units.Hero;
import blackmere.towerdef.units.Tower;
import blackmere.towerdef.units.Bullet;
import blackmere.towerdef.units.Unit;
import blackmere.towerdef.util.Direction;
import blackmere.towerdef.util.Utility;
import static blackmere.towerdef.util.Constants.*;

public class Demo extends BasicGameState {
	
	private StateBasedGame gameManager;
	private TiledMap demoMap;
	private ArrayList<Unit> allUnits;
	private ArrayList<Button> buttons;
	private Hero activeHero;
	private int currentEnergy;
	private boolean buildMode;
		
	//
	public void init(GameContainer container, StateBasedGame manager) throws SlickException {
		gameManager = manager;
		demoMap = new TiledMap("res/basicMap.tmx");
		currentEnergy = initialEnergy;
		buildMode = false;
		activeHero = new Hero(heroStartX, heroStartY);
		allUnits = new ArrayList<Unit>();
		allUnits.add(activeHero);
		allUnits.add(new Enemy(enemyStartX, enemyStartY));
		allUnits.add(new Enemy(enemyStartX, enemyStartY + tileSize));
		allUnits.add(new Enemy(enemyStartX, enemyStartY - tileSize));
		allUnits.add(new Tower(towerStartX, towerStartY));
		buttons = new ArrayList<Button>();
		Image towerButtonImage = new Image("res/towerButton.png");
		Image towerButtonLocked = new Image("res/towerButtonLocked.png");
		Button towerButtonBlue = new Button(towerButtonImage, towerButtonLocked, UIButtonXPos, UIButtonYPos, UIButtonSize, UIButtonSize);
		Button towerButtonPurple = new Button(towerButtonImage, towerButtonLocked, UIButtonXPos, UIButtonYPos + tileSize, UIButtonSize, UIButtonSize);	// TODO: more precise pos; better init?
		Button towerButtonRed = new Button(towerButtonImage, towerButtonLocked, UIButtonXPos, UIButtonYPos + 2 * tileSize, UIButtonSize, UIButtonSize);	// TODO: use the actual images
		towerButtonBlue.unlock();
		buttons.add(towerButtonBlue);
		buttons.add(towerButtonPurple);
		buttons.add(towerButtonRed);
	}

	//
	public void render(GameContainer container, StateBasedGame manager, Graphics g) throws SlickException {
		demoMap.render(0, 0);
		
		// draw the UI text elements
		g.setColor(Color.black);
		g.drawString("Dream Energy: ", resTextXPos, resTextYPos);
		g.drawString("HP:", HPTextXPos, HPTextYPos);
		
		// draw the current resource amount
		if (currentEnergy < cheapestTowerCost) {
			g.setColor(Color.red);
		} else if (currentEnergy < 2 * cheapestTowerCost) {
			g.setColor(Color.magenta);
		} else {
			g.setColor(Color.blue);
		}
		
		g.drawString("" + currentEnergy, resTextXPos + resTextOffset, resTextYPos);	// TODO: consolidate w/ HP text?
		
		// draw the current HP	
		double currentHP = activeHero.getHP();
		double maxHP = activeHero.getMaxHP();
		double thirdHP = maxHP / 3;
		
		// TODO: keep position static even when going b/w triple and double digits
		if (currentHP > thirdHP * 2) {
			g.setColor(Color.blue);		// TODO: make green? (but dark enough to see)
		} else if (currentHP <= thirdHP) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.magenta);	// TODO: should be yellow?
		}
		
		// TODO: potential minor issues with pausing during red flash (cancels flash delay??)
		
		g.drawString((int) currentHP + "/" + (int) maxHP, HPTextXPos + HPTextOffset, HPTextYPos);
		
		// draw the UI buttons
		for (Button b : buttons) {
			b.draw(g);
		}
		
		// divide units into lists according to type
		ArrayList<Bullet> bullets = new ArrayList<Bullet>();
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		ArrayList<Tower> towers = new ArrayList<Tower>();
		
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
		
		// then, draw the hero, as long as they're currently alive
		if (!activeHero.isDead()) {
			activeHero.draw(g);
		}
		
		// finally, draw all the health bars
		for (Unit u : getActiveUnits()) {
			u.drawHealthBar(g);
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
			if (u instanceof Enemy && u.withinRange(t) && t.timeToFire()) {
				allUnits.add(t.getBullet());
				return;		// TODO: find a better way?
			}
		}
		
		if (t.timeToGenerate()) {
			currentEnergy += t.getEnergy();
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
	
	// override of InputListener interface method
	public void mouseClicked(int button, int x, int y, int count) {
		//System.out.println("Click at " + x + "," + y);		// debug line
		
		// TODO: process UI clicks first
		if (buttons.get(0).getBoundingBox().contains(x, y)) {
			buildMode = !buildMode;
			buttons.get(0).toggleSelect();	// TODO: do for all buttons in list, generically
			return;		// TODO: consolidate with branch below?
		}
		
		if (!buildMode) {
			return;		// don't process clicks unless we're building
		}
		
		// don't process build clicks that are in the UI
		if (x <= leftBound || x >= rightBound || y <= upBound || y >= downBound) {	// TODO: the top and bottom boundaries seem a tad off
			return;	// TODO: consolidate
		}
		
		// process battlefield clicks
		if (currentEnergy < towerCostBlue) {	// TODO: get cost from object
			return;		// TODO: give better indication of 'can't afford', i.e. res amt flash red w/ sound effect
		}
		
		int towerX = (x / tileSize) * tileSize;	// first divide to round off and get the column number, then multiply to get the starting x-value for that column
		int towerY = (y / tileSize) * tileSize;	// TODO: is there a mathematically 'better' way to achieve this?
		Tower t = null;
		
		try {
			t = new Tower(towerX, towerY);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (t == null) {
			return;
		}
		
		Rectangle box = t.getBoundingBox();		// TODO: use motionbox instead??
		
		for (Unit u : getActiveUnits()) {
			if (u instanceof Bullet) {
				// don't be blocked by bullets
				continue;
			} else if (Utility.detectCollision(box, u.getMotionBox())) {
				return;
			}
		}
		
		// if we get here, there were no collisions, so make the tower 'official'
		currentEnergy -= t.getCost();
		allUnits.add(t);
	}
	
	// TODO: allow towers to be built 'on top' of enemies to a reasonable extent, as in PvZ
	// TODO: allow hero to walk over top of towers?

	//
	public int getID() {
		return demoID;
	}
}
