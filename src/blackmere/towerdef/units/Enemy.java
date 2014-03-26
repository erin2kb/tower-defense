package blackmere.towerdef.units;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.Demo;
import blackmere.towerdef.util.Utility;

import static blackmere.towerdef.util.Constants.*;

public class Enemy extends Unit {
	private Image[] idleLeftFrames;	//walkLeftFrames, attackLeftFrames;
	private int[] idleDurationArray; //walkDurationArray, attackDurationArray;
	private Animation idleLeft; //walkLeft, attackLeft;
	private Unit target;
	
	public Enemy(Demo lv, float startX, float startY) throws SlickException {
		super(lv, startX, startY, enemyMaxHP, enemyDamage);
		target = null;
		
		idleLeftFrames = new Image[enemyNumIdleFrames];
		idleLeftFrames[0] = new Image("blackmere/towerdef/res/enemy/wl1.png");
		idleDurationArray = new int[enemyNumIdleFrames];
		idleDurationArray[0] = enemyIdleDuration;
		
		idleLeft = new Animation(idleLeftFrames, idleDurationArray, false);
		setSprite(idleLeft);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x + enemyOffsetX, y + enemyOffsetY, enemyWidth, enemyHeight);
	}
	
	// TODO: consolidate all getBox f'ns?
	public Rectangle getBulletTargetBox() {
		return new Rectangle(x + enemyTargetOffsetBulletX, y + enemyTargetOffsetBulletY, enemyTargetWidthBullet, enemyTargetHeightBullet);
	}
	
	public Rectangle getHeroTargetBox() {
		return new Rectangle(x + enemyTargetOffsetHeroX, y + enemyTargetOffsetHeroY, enemyTargetWidthHero, enemyTargetHeightHero);
	}
	
	// TODO: document the reason for this
	public Rectangle getTargetBox() {
		return getHeroTargetBox();
	}
	
	public Rectangle getMotionBox() {
		return new Rectangle(x + enemyMotionOffsetX, y + enemyMotionOffsetY, enemyMotionWidth, enemyMotionHeight);
	}
	
	public Rectangle getAttackBox() {
		return new Rectangle(x + enemyAttackOffsetX, y + enemyAttackOffsetY, enemyAttackWidth, enemyAttackHeight);
	}
	
	protected boolean safeToMove(ArrayList<Unit> units, int delta) {
		Rectangle box = getMotionBox();
		float newX = box.getX() - delta * enemySpeed;
		Rectangle newBox = new Rectangle(newX, box.getY(), box.getWidth(), box.getHeight());
		
		for (Unit u : units) {
			if (u instanceof Enemy || u instanceof Bullet) {
				// don't be blocked by fellow enemies or by bullets
				continue;
			} else if (Utility.detectCollision(newBox, u.getMotionBox())) {
				return false;
			}
		}

		return true;
	}
	
	public void move(ArrayList<Unit> units, int delta) {
		if (safeToMove(units, delta) && !attacking) {
			x -= delta * enemySpeed;
		}
	}
	
	private void attack(ArrayList<Unit> units) {
		for (Unit u : units) {
			if ((u instanceof Hero && ((Hero) u).withinRange(this)) || 
					(u instanceof Tower && ((Tower) u).withinRange(this))) {
				lastAttackUpdate = System.currentTimeMillis();
				attacking = true;
				target = u;
			} 
		}
	}
	
	public void checkAttack(ArrayList<Unit> units) {		
		if (target != null) {
			if (target.isDead() ||
					(target instanceof Hero && !((Hero) target).withinRange(this)) ||
					(target instanceof Tower && !((Tower) target).withinRange(this))) {
				target = null;		// TODO: figure out the if statement; document; repeat in hero if needed
				attacking = false;
			}
		}
		
		if (target == null) {
			attack(units);	// find new target; do in hero?? consolidate with hero??? make so it doesn't get called so much??
		}
		
		if (target != null) {
			long time = System.currentTimeMillis();
			
			if (time - lastAttackUpdate >= enemyAttackDelay) {
				target.takeHit(getDamage(), this);
				lastAttackUpdate = time;
			}
		}
	}
	
	public boolean hasWon() {
		return x < leftBound - 10;
	}
	
	// TODO: only detect collisions if in lane -- need to reconsider this in order to use for enemies (since heroes not confined to lanes, but still need to be attacked)
	// TODO: document: enemy 'invincible' when flashing red (test this)...
	// TODO: ...or, change this behavior and reset flash on each hit (need to account for individual delay between bullet/hero so no instant-kills)
	// TODO: fix: make it so hero can't block enemy from moving unless enemy can attack hero (inc. for bee/ladybug too)
}