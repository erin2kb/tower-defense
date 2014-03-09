package blackmere.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Tower extends Unit {
	
	private final static int width = 52;
	private final static int height = 56;
	private final static int offsetX = 5;
	private final static int offsetY = 3;
	private final static int bulletOffset = 34;
	private final static int maxHP = 120;
	private final int delay = 2200;
	private Image[] idleFrames;
	private int[] idleDurationArray;
	private Animation idle;
	private long lastShotFired;

	public Tower(float startX, float startY) throws SlickException {
		super(startX, startY, maxHP, 0);	// TODO: here and bullet, comment on 0's
		lastShotFired = 0;
		
		idleFrames = new Image[1];
		idleFrames[0] = new Image("res/tower.png");
		idleDurationArray = new int[1];
		idleDurationArray[0] = 100000;	// TODO: un-hard code
		
		idle = new Animation(idleFrames, idleDurationArray, false);
		setSprite(idle);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x + offsetX, y + offsetY, width, height);
	}
	
	public Rectangle getTargetBox() {
		return getBoundingBox();
	}
	
	public Rectangle getMotionBox() {
		return getBoundingBox();
	}
	
	// TODO: consolidate these 'empty' fns? i.e. make tower/bullet subclass of something?	
	public Rectangle getAttackBox() {
		return getBoundingBox();
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
		return new Bullet(x + bulletOffset, y);	// TODO: use getter??
	}

	// TODO: consolidate with hero, and enemy if possible
	public boolean withinRange(Enemy e) {
		Rectangle box = getTargetBox();
		Rectangle otherBox = e.getAttackBox();

		return box.intersects(otherBox);
	}

}
