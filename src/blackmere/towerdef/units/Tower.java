package blackmere.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Tower extends Unit {
	
	private final static int towerWidth = 62;
	private final static int towerHeight = 60;
	private final static int maxHP = 120;
	private final int delay = 2200;
	private Image[] idleFrames;
	private int[] idleDurationArray;
	private Animation idle;
	private long lastShotFired;

	public Tower(float startX, float startY) throws SlickException {
		super(towerWidth, towerHeight, startX, startY, maxHP, 0);	// TODO: here and bullet, comment on 0's
		lastShotFired = 0;
		
		idleFrames = new Image[1];
		idleFrames[0] = new Image("res/tower.png");
		idleDurationArray = new int[1];
		idleDurationArray[0] = 100000;	// TODO: un-hard code
		
		idle = new Animation(idleFrames, idleDurationArray, false);
		setSprite(idle);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x + 8, y + 2, width - 16, height - 4);
	}
	
	public Rectangle getTargetBox() {
		return new Rectangle(x, y + 4, width - 4, height - 8);  // TODO: determine these exactly
	}
	
	// TODO: optimize this transplanted code
	private void fire(long timestamp) {
		lastShotFired = timestamp;
	}
	
	public boolean timeToFire() {
		long time = System.currentTimeMillis();
		
		if (time - lastShotFired >= delay) {
			fire(time);
			return true;
		} else {
			return false;
		}
	}
	
	// TODO: exceptions
	public Bullet getBullet() throws SlickException {
		return new Bullet(x + width, y + height / 2 - 10); 	// TODO: un-hard code; use getter??
	}

	// TODO: consolidate with hero, and enemy if possible
	public boolean withinRange(Enemy e) {
		Rectangle box = getTargetBox();
		Rectangle otherBox = e.getAttackBox();

		return box.intersects(otherBox);
	}

}
