package blackmere.towerdef.units;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.Movement;

public class Enemy extends Unit {
	private final static int width = 62;
	private final static int height = 45;
	private final static int offsetY = 17;	// TODO: move all this logic to unit?? include offsetX, though 0 here (same for hero)
	private final static int bulletTargetWidth = 30;
	private final static int bulletTargetHeight = 30;
	private final static int bulletTargetOffsetX = 24;
	private final static int bulletTargetOffsetY = offsetY;
	private final static int heroTargetWidth = 33;
	private final static int heroTargetHeight = 19;
	private final static int heroTargetOffsetX = 18;
	private final static int heroTargetOffsetY = 20;
	private final static int motionWidth = 45;
	private final static int motionHeight = 34;
	private final static int motionOffsetX = 8;		// TODO: play with these values; bullets rarely visible when enemy right on top of tower; enemy looks too far to attack hero; make sure it can still reach hero, though
	private final static int motionOffsetY = offsetY;	// TODO: make var for each value, and stick them in another file for constants
	private final static int attackWidth = 18;
	private final static int attackHeight = 10;
	private final static int attackOffsetX = 0;
	private final static int attackOffsetY = 30;
	private final static int maxHP = 160;
	private final static int damage = 10;
	private final int numWalkFrames = 8;
	private final int numAttackFrames = 8;
	private final int walkDuration = 16000;
	private final int attackDuration = 26000;
	private final float speed = 0.0004f;		// 0.0008f
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
		super(startX, startY, maxHP, damage);
		target = null;
		
		idleLeftFrames = new Image[1];
		idleLeftFrames[0] = new Image("res/enemy/wl1.png");
		idleDurationArray = new int[1];
		idleDurationArray[0] = attackDuration;
		
		idleLeft = new Animation(idleLeftFrames, idleDurationArray, false);
		setSprite(idleLeft);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x, y + offsetY, width, height);
	}
	
	// TODO: consolidate w/ hero? also, consolidate all getBox f'ns?
	public Rectangle getBulletTargetBox() {
		return new Rectangle(x + bulletTargetOffsetX, y + bulletTargetOffsetY, bulletTargetWidth, bulletTargetHeight);
	}
	
	public Rectangle getHeroTargetBox() {
		return new Rectangle(x + heroTargetOffsetX, y + heroTargetOffsetY, heroTargetWidth, heroTargetHeight);
	}
	
	// TODO: remove this temp f'n
	public Rectangle getTargetBox() {
		return getHeroTargetBox();
	}
	
	public Rectangle getMotionBox() {
		return new Rectangle(x + motionOffsetX, y + motionOffsetY, motionWidth, motionHeight);
	}
	
	public Rectangle getAttackBox() {
		return new Rectangle(x + attackOffsetX, y + attackOffsetY, attackWidth, attackHeight);
	}
	
	public boolean withinRange(Hero h) {
		Rectangle box = getHeroTargetBox();
		Rectangle otherBox = h.getAttackBox();
		
		return box.intersects(otherBox);
	}
	
	// TODO: add logic for when enemy reaches left side of screen
	// TODO: lanes; document: enemy 'invincible' when flashing red (test this)
	// TODO: fix: make it so hero can't block enemy from moving unless enemy can attack hero
	
	// TODO: consolidate with hero??
	private boolean safeToMove(ArrayList<Unit> units) {
		Rectangle box = getMotionBox();
		float newX = box.getX() - delta * speed;
		Rectangle newBox = new Rectangle(newX, box.getY(), box.getWidth(), box.getHeight());
		
		for (Unit u : units) {
			if (u instanceof Enemy || u instanceof Bullet) {
				// don't be blocked by fellow enemies or by bullets
				continue;
			} else if (detectMotionCollision(newBox, u.getMotionBox())) {
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
				target = null;		// TODO: figure out the if statement; document; repeat in hero if needed
			}
		}
		
		if (target == null) {
			attack(units);	// find new target; do in hero??
		}
		
		if (target != null) {
			long time = System.currentTimeMillis();
			
			if (time - lastAttackUpdate >= attackDelay) {
				target.takeHit(getDamage());
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