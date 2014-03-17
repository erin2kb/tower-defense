package blackmere.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.Demo;
import blackmere.towerdef.util.Utility;

import static blackmere.towerdef.util.Constants.*;

public class Bullet extends Unit {

	private Image[] idleFrames;
	private int[] idleDurationArray;
	private Animation idle;

	public Bullet(Demo lv, float startX, float startY) throws SlickException {
		super(lv, startX, startY, bulletMaxHP, bulletDamage);
		
		idleFrames = new Image[bulletNumIdleFrames];
		idleFrames[0] = new Image("blackmere/towerdef/res/bullet.png");
		idleDurationArray = new int[1];
		idleDurationArray[0] = bulletIdleDuration;
		
		idle = new Animation(idleFrames, idleDurationArray, false);
		setSprite(idle);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x + bulletOffsetX, y + bulletOffsetY, bulletWidth, bulletHeight);
	}
	
	public Rectangle getTargetBox() {
		return getBoundingBox();
	}
	
	public Rectangle getMotionBox() {
		return getBoundingBox();
	}
	
	public Rectangle getAttackBox() {
		return getBoundingBox();
	}
	
	public void move(int delta) {
		x += delta * bulletSpeed;
		
		if (x > rightBound) {
			// it went off the screen, so it's no longer in play
			die();
		}
	}
	
	public boolean detectBulletCollision(Enemy e) {
		Rectangle box = getBoundingBox();
		Rectangle otherBox = e.getBulletTargetBox();
		
		return Utility.detectCollision(box, otherBox);
	}
}
