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
	private Image[] idleFrames,	walkFrames, attackFrames;
	private int[] idleDurationArray, walkDurationArray, attackDurationArray;
	private Animation idle, walk, attack;
	private Unit target;
	private boolean damageDone;
	
	public Enemy(Demo lv, float startX, float startY) throws SlickException {
		super(lv, startX, startY, enemyMaxHP, enemyDamage);
		healthOffsetX = healthOffsetX + 10;	// since enemy is wider than other sprites
		target = null;
		damageDone = false;
		
		walkFrames = new Image[enemyNumWalkFrames];
		attackFrames = new Image[enemyNumAttackFrames];
		idleFrames = new Image[enemyNumIdleFrames];
		idleFrames[0] = new Image("blackmere/towerdef/res/enemy/spider/w1.png");
		walkDurationArray = new int[enemyNumWalkFrames];
		attackDurationArray = new int[enemyNumAttackFrames];
		idleDurationArray = new int[enemyNumIdleFrames];
		idleDurationArray[0] = enemyIdleDuration;
		
		for (int i = 0; i < enemyNumWalkFrames; i++) {
			int index = i + 1;
			String name = "blackmere/towerdef/res/enemy/spider/old/w" + index + ".png";		// TODO: decide on a final set and remove 'old'
			walkFrames[i] = new Image(name);
			walkDurationArray[i] = enemyWalkDuration;
		}
		
		for (int i = 0; i < enemyNumAttackFrames; i++) {
			int index = i + 1;
			String name = "blackmere/towerdef/res/enemy/spider/a" + index + ".png";
			attackFrames[i] = new Image(name);
			attackDurationArray[i] = enemyAttackDuration;
		}
		
		attack = new Animation(attackFrames, attackDurationArray, false);
		walk = new Animation(walkFrames, walkDurationArray, false);
		idle = new Animation(idleFrames, idleDurationArray, false);
		setSprite(idle);
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
	
	// TODO: document the reason for this; but first, remember the reason for this >.<
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
			this.setSprite(walk);
			sprite.update(delta);
		} else if (attacking) {
			this.setSprite(attack);
			sprite.update(delta);	// TODO: necessary?
		} else {
			this.setSprite(idle);
			sprite.update(delta);	// TODO: necessary?
		}
	}
	
	private void attack(ArrayList<Unit> units) {
		for (Unit u : units) {
			if ((u instanceof Hero && ((Hero) u).withinRange(this)) || 
					(u instanceof Tower && ((Tower) u).withinRange(this))) {
				lastAttackUpdate = System.currentTimeMillis();
				attacking = true;
				sprite = attack;
				sprite.stopAt(enemyNumAttackFrames - 1);
				target = u;
			} 
		}
	}
	
	public void checkAttack(ArrayList<Unit> units, int delta) {		
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
		
		// TODO: consolidate with lower branch (also uses time) - compatible??
		if (sprite.isStopped() && System.currentTimeMillis() - lastAttackUpdate >= enemyAttackDelay) {
			damageDone = false; // TODO: document why this is needed
			sprite.restart();	// TODO: this was in hero; but why is it needed?
			lastAttackUpdate = System.currentTimeMillis();
			return;
		}
		
		if (target != null && !damageDone) {
			long time = System.currentTimeMillis();

			if (time - lastAttackUpdate >= enemyHitDelay) {
				target.takeHit(getDamage(), this);
				lastAttackUpdate = time;
				damageDone = true;
			}
		}
		
		sprite.update(delta);
	}
	
	public boolean hasWon() {
		return x < leftBound - 10;
	}
	
	//
	public int getProgress() {		
		return (int) ((rightBound - x) / tileSize) + 2;		// not sure why, but +2 gets the behavior I want...
	}
	
	// TODO: only detect collisions if in lane -- need to reconsider this in order to use for enemies (since heroes not confined to lanes, but still need to be attacked)
	// TODO: document: enemy 'invincible' when flashing red (test this)...
	// TODO: ...or, change this behavior and reset flash on each hit (need to account for individual delay between bullet/hero so no instant-kills)
	// TODO: fix: make it so hero can't block enemy from moving unless enemy can attack hero (inc. for bee/ladybug too)
}