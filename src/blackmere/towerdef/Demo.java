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
//import org.newdawn.slick.tiled.TiledMap;

import blackmere.towerdef.ui.Button;
import blackmere.towerdef.ui.GameOver;
import blackmere.towerdef.ui.PauseButton;
import blackmere.towerdef.ui.PauseMenu;
import blackmere.towerdef.ui.TowerButton;
import blackmere.towerdef.ui.Victory;
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
	private GameContainer gameContainer;	// TODO: clean this up (some f'ns use 'container' param instead)
	private Image[] maps;
	private ArrayList<Unit> allUnits;
	private ArrayList<TowerButton> towerButtons;
	private PauseButton pauseButton;
	private Hero activeHero;
	private int mapIndex, currentEnergy, enemiesSpawned, enemiesKilled, heroKills, towerKills, spawnDelay;
	private boolean buildBlue;
	private long lastSpawn;
		
	//
	public void init(GameContainer container, StateBasedGame manager) throws SlickException {
		gameManager = manager;
		gameContainer = container;
		maps = new Image[numMaps];
		
		// TODO: utility f'n
		for (int i = 0; i < numMaps; i++) {
			maps[i] = new Image(mapPrefix + i + mapPostfix);
		}
		
		mapIndex = 0;
		currentEnergy = initialEnergy;
		enemiesSpawned = 1;		// the first enemy, which we spawn here
		enemiesKilled = 0;
		heroKills = 0;
		towerKills = 0;
		spawnDelay = firstSpawnDelay;
		lastSpawn = System.currentTimeMillis();
		buildBlue = true;
		activeHero = new Hero(this, heroStartX, heroStartY);
		allUnits = new ArrayList<Unit>();
		allUnits.add(activeHero);
		allUnits.add(new Enemy(this, enemyStartX, enemyStartY));	// TODO: make separate Debug class? Level superclass?			
		towerButtons = new ArrayList<TowerButton>();
		Image towerButtonImageBlue = new Image("blackmere/towerdef/res/towerButtonBlue.png");
		Image towerButtonImagePurple = new Image("blackmere/towerdef/res/towerButtonPurple.png");
		Image towerButtonLocked = new Image("blackmere/towerdef/res/towerButtonLocked.png");
		TowerButton towerButtonBlue = new TowerButton(towerButtonImageBlue, towerButtonLocked, towerButtonXPos, towerButtonYPos, towerButtonSize, towerButtonSize);
		TowerButton towerButtonPurple = new TowerButton(towerButtonImagePurple, towerButtonLocked, towerButtonXPos, towerButtonYPos + tileSize, towerButtonSize, towerButtonSize);	// TODO: more precise pos; better init?
		TowerButton towerButtonRed = new TowerButton(towerButtonImageBlue, towerButtonLocked, towerButtonXPos, towerButtonYPos + 2 * tileSize, towerButtonSize, towerButtonSize);	// TODO: use the actual images
		towerButtonBlue.unlock();
		towerButtonBlue.select();		// start game with this one selected
		towerButtonPurple.unlock();
		towerButtons.add(towerButtonBlue);
		towerButtons.add(towerButtonPurple);
		towerButtons.add(towerButtonRed);
		Image pauseButtonImage = new Image("blackmere/towerdef/res/pauseButton.png");
		pauseButton = new PauseButton(pauseButtonImage, pauseButtonXPos, pauseButtonYPos, pauseButtonSize, pauseButtonSize);
	}

	//
	public void render(GameContainer container, StateBasedGame manager, Graphics g) throws SlickException {
		maps[mapIndex].draw(0, 0);
		
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
		
		// draw the kill counts
		g.setColor(Color.black);
		g.drawString("Hero Kills: " + heroKills + "   Tower Kills: " + towerKills, killTextXPos, killTextYPos);
		
		// draw the UI buttons
		pauseButton.draw(g);
		
		for (Button b : towerButtons) {
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
		setMap();
		
		if (enemiesKilled >= numEnemiesTotal) {
			render(container, manager, container.getGraphics());	// render one last time, to show updated kill counts
			Image bg = new Image(windowWidth, windowHeight);
			container.getGraphics().copyArea(bg, 0, 0);
			((Victory) gameManager.getState(victoryID)).setBackground(bg);
			gameManager.enterState(victoryID);
		} else if (enemiesKilled >= 3 * numEnemiesTotal / 4) {
			spawnDelay = fourthSpawnDelay;
		} else if (enemiesKilled >= 2 * numEnemiesTotal / 4) {
			spawnDelay = thirdSpawnDelay;
		} else if (enemiesKilled >= numEnemiesTotal / 4) {
			spawnDelay = secondSpawnDelay;
		}
		
		long currentTime = System.currentTimeMillis();	// TODO: make a f'n for such time comparisons?
		if (enemiesSpawned < numEnemiesTotal && currentTime - lastSpawn >= spawnDelay) {
			lastSpawn = currentTime;
			spawnEnemy();
		}
		
		// process input
		Input input = container.getInput();
		
		if (input.isKeyPressed(Input.KEY_P)) {
			pauseGame(container);
		}
		
		for (Unit u : getActiveUnits()) {
			if (u instanceof Tower) {
				towerLogic((Tower) u);
			} else if (u instanceof Enemy) {
				enemyLogic((Enemy) u, delta);
			} else if (u instanceof Bullet) {
				bulletLogic((Bullet) u, delta);
			}
		}

		if (activeHero.isDead()) {
			return;		// don't do any more processing if hero is dead
		} else if (! activeHero.isAttacking()) {
			if (input.isKeyDown(Input.KEY_LEFT)) {
				activeHero.move(Direction.LEFT, getActiveUnits(), delta);
			} else if (input.isKeyDown(Input.KEY_RIGHT)) {
				activeHero.move(Direction.RIGHT, getActiveUnits(), delta);
			} else if (input.isKeyDown(Input.KEY_UP)) {
				activeHero.move(Direction.UP, getActiveUnits(), delta);
			} else if (input.isKeyDown(Input.KEY_DOWN)) {
				activeHero.move(Direction.DOWN, getActiveUnits(), delta);
			} else if (input.isKeyDown(Input.KEY_SPACE)) {
				activeHero.attack(getActiveUnits());
			} else {
				activeHero.idle();
			}
		} else {
			// hero is alive and currently attacking
			activeHero.checkAttack(delta);
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
	
	private void enemyLogic(Enemy e, int delta) throws SlickException {
		e.checkAttack(getActiveUnits(), delta);
		e.move(getActiveUnits(), delta);
		
		if (e.hasWon()) {
			Image bg = new Image(windowWidth, windowHeight);	// TODO: f'n for this?
			gameContainer.getGraphics().copyArea(bg, 0, 0);
			((GameOver) gameManager.getState(gameOverID)).setBackground(bg);
			gameManager.enterState(gameOverID);
		}
	}
	
	private void bulletLogic(Bullet b, int delta) {
		b.move(delta);
		
		for (Unit u : getActiveUnits()) {
			if (u instanceof Enemy && b.detectBulletCollision((Enemy) u)) {
				u.takeHit(b.getDamage(), b);
				b.die();
				return;
			}
		}
	}
	
	// override of InputListener interface method
	public void mouseClicked(int button, int x, int y, int count) {
		//System.out.println("Click at " + x + "," + y);		// debug line
		
		// TODO: process UI clicks first
		if (pauseButton.getBoundingBox().contains(x, y)) {
			try {
				pauseGame(gameContainer);
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (towerButtons.get(0).getBoundingBox().contains(x, y)) {
			buildBlue = true;		// TODO: handle this better, more generically, for all colors, and possibly re-allow deselecting
			towerButtons.get(0).select();	// TODO: do for all buttons in list, generically
			towerButtons.get(1).deselect();	// TODO: do this better
			return;		// TODO: consolidate with !buildBlue branch below?
		} else if (towerButtons.get(1).getBoundingBox().contains(x, y)) {
			buildBlue = false;
			towerButtons.get(0).deselect();
			towerButtons.get(1).select();
		}
		
		/*if (!buildBlue) {
			return;		// don't process clicks unless we're building
		} */
		
		// don't process build clicks that are in the UI area (but not on a button)
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
			t = (buildBlue ? new Tower(this, towerX, towerY, true) : new Tower(this, towerX, towerY, false));
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
	// TODO: allow hero to walk over top of towers

	//
	public int getID() {
		return demoID;
	}
	
	//
	public void anotherOneBitesTheDust(Unit killer) {
		enemiesKilled++;
		
		if (killer instanceof Hero) {
			heroKills++;
		} else {
			towerKills++;
		}
	}
	
	//
	private void spawnEnemy() throws SlickException {
		int lane = 1 + (int)(Math.random() * ((5 - 1) + 1));
		enemiesSpawned++;
		allUnits.add(new Enemy(this, enemyStartX, tileSize * lane));
	}
	
	//
	private void pauseGame(GameContainer container) throws SlickException {
		Image bg = new Image(windowWidth, windowHeight);
		container.getGraphics().copyArea(bg, 0, 0);
		((PauseMenu) gameManager.getState(pauseID)).setBackground(bg);
		gameManager.enterState(pauseID);
	}
	
	//
	private void setMap() {
		int index = 0;
		
		for (Unit u : getActiveUnits()) {
			if (! u.isDead() && u instanceof Enemy && ((Enemy) u).getProgress() > index) {
				index = ((Enemy) u).getProgress();
			}
		}
		
		mapIndex = index;
	}
}
