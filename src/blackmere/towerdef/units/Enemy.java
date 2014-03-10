package blackmere.towerdef.units;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.util.Utility;

import static blackmere.towerdef.util.Constants.*;

public class Enemy extends Troop {
	private Image[] walkLeftFrames, attackLeftFrames, idleLeftFrames;
	private int[] walkDurationArray, attackDurationArray, idleDurationArray;
	private Animation walkLeft, attackLeft, idleLeft;

	
	public Enemy(float startX, float startY) throws SlickException {
		super(startX, startY, enemyMaxHP, enemyDamage);
		target = null;
		
		idleLeftFrames = new Image[enemyNumIdleFrames];
		idleLeftFrames[0] = new Image("res/enemy/wl1.png");
		idleDurationArray = new int[enemyNumIdleFrames];
		idleDurationArray[0] = enemyIdleDuration;
		
		idleLeft = new Animation(idleLeftFrames, idleDurationArray, false);
		setSprite(idleLeft);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x + enemyOffsetX, y + enemyOffsetY, enemyWidth, enemyHeight);
	}
	
	// TODO: consolidate w/ hero? also, consolidate all getBox f'ns?
	public Rectangle getBulletTargetBox() {
		return new Rectangle(x + enemyTargetOffsetBulletX, y + enemyTargetOffsetBulletY, enemyTargetWidthBullet, enemyTargetHeightBullet);
	}
	
	public Rectangle getHeroTargetBox() {
		return new Rectangle(x + enemyTargetOffsetHeroX, y + enemyTargetOffsetHeroY, enemyTargetWidthHero, enemyTargetHeightHero);
	}
	
	// TODO: remove this temp f'n
	public Rectangle getTargetBox() {
		return getHeroTargetBox();
	}
	
	public Rectangle getMotionBox() {
		return new Rectangle(x + enemyMotionOffsetX, y + enemyMotionOffsetY, enemyMotionWidth, enemyMotionHeight);
	}
	
	public Rectangle getAttackBox() {
		return new Rectangle(x + enemyAttackOffsetX, y + enemyAttackOffsetY, enemyAttackWidth, enemyAttackHeight);
	}
	
	public boolean withinRange(Hero h) {
		Rectangle box = getHeroTargetBox();
		Rectangle otherBox = h.getAttackBox();
		
		return box.intersects(otherBox);
	}
	
	// TODO: add logic for when enemy reaches left side of screen
	// TODO: lanes (only detect collisions/tower fire if in lane)
	// TODO: document: enemy 'invincible' when flashing red (test this)...
	// TODO: ...or, change this behavior and reset flash on each hit (need to account for individual delay between bullet/hero so no instant-kills)
	// TODO: fix: make it so hero can't block enemy from moving unless enemy can attack hero
}