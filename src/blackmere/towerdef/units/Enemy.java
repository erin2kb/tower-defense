package blackmere.towerdef.units;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.Movement;

public class Enemy extends Unit {
	private final static int enemyWidth = 62;
	private final static int enemyHeight = 52;
	private final static int maxHP = 160;
	private final static int damage = 10;
	private final int numWalkFrames = 8;
	private final int numAttackFrames = 8;
	private final int walkDuration = 16000;
	private final int attackDuration = 26000;
	private final float speed = 0.0008f;
	private final int delta = 150;
	private final int attackDelay = 3000;
	private Image[] walkLeftFrames;
	private Image[] attackLeftFrames;
	private Image[] idleLeftFrames;
	private int[] walkDurationArray;
	private int[] attackDurationArray;
	private int[] idleDurationArray;
	private Animation walkLeft, attackLeft, idleLeft;
	private Unit target;

	
	public Enemy(float startX, float startY) throws SlickException {
		super(enemyWidth, enemyHeight, startX, startY, maxHP, damage);
		target = null;
		
		idleLeftFrames = new Image[1];
		idleLeftFrames[0] = new Image("res/enemy/wl1.png");
		idleDurationArray = new int[1];
		idleDurationArray[0] = attackDuration;
		
		idleLeft = new Animation(idleLeftFrames, idleDurationArray, false);
		setSprite(idleLeft);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x + 26, y + 8, width - 48, height - 20);	// TODO: un-hard code
	}
	
	// TODO: consolidate w/ hero?
	public Rectangle getTargetBox() {
		return new Rectangle(x + 18, y + 10, width - 30, height - 20);  // TODO
	}
	
	public Rectangle getAttackBox() {
		return new Rectangle(x - 12, y + 4, 20, height - 8);	 // TODO: un-hard code; determine exact
	}
	
	public boolean withinRange(Hero h) {
		Rectangle box = getTargetBox();
		Rectangle otherBox = h.getWeaponBox();
		
		return box.intersects(otherBox);
	}
	
	// TODO: consolidate with hero??
	private boolean safeToMove(ArrayList<Unit> units) {
		float newX = x - delta * speed;
		float newY = y;
		
		for (Unit u : units) {
			if (u instanceof Enemy || u instanceof Bullet) {
				// don't be blocked by fellow enemies or by bullets
				continue;
			} else if (detectCollision(newX, newY, u)) {
				return false;
			}
		}

		return true;
	}
	
	public void move(ArrayList<Unit> units) {
		if (safeToMove(units)) {
			x -= delta * speed;
		}
	}
	
	// TODO: consolidate
	public void checkAttack(ArrayList<Unit> units) {		
		if (target != null) {
			if (target.isDead() ||
					(target instanceof Hero && !((Hero) target).withinRange(this)) ||
					(target instanceof Tower && !((Tower) target).withinRange(this))) {
				target = null;
			}
		}
		
		if (target == null) {
			attack(units);	// find new target
		}
		
		if (target != null) {
			long time = System.currentTimeMillis();
			
			if (time - lastAttackUpdate >= attackDelay) {
				target.takeHit();
				target.takeDamage(getDamage());
				lastAttackUpdate = time;
			}
		}
	}
	
	private void attack(ArrayList<Unit> units) {
		for (Unit u : units) {
			if (u instanceof Hero) {	// TODO: consolidate branches
				if (((Hero) u).withinRange(this)) {
					lastAttackUpdate = System.currentTimeMillis();
					target = u;
				}
			} else if (u instanceof Tower) {
				if (((Tower) u).withinRange(this)) {
					lastAttackUpdate = System.currentTimeMillis();
					target = u;
				}
			}
		}
	}
}