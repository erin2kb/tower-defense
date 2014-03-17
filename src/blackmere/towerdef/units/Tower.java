package blackmere.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.Demo;

import static blackmere.towerdef.util.Constants.*;

public class Tower extends Unit {

	private Image[] idleFrames;
	private int[] idleDurationArray;
	private Animation idle;
	private long lastShotFired, lastEnergyGen;
	private int buildCost, energyRate;

	public Tower(Demo lv, float startX, float startY) throws SlickException {
		super(lv, startX, startY, towerMaxHP, towerDamage);
		lastShotFired = 0;
		buildCost = towerCostBlue;
		energyRate = energyRateBlue;
		lastEnergyGen = System.currentTimeMillis();
		
		idleFrames = new Image[towerNumIdleFrames];
		idleFrames[0] = new Image("blackmere/towerdef/res/tower.png");		// TODO: turn into a loop, like in hero/enemy (do same in bullet); consolidate f'n?
		idleDurationArray = new int[towerNumIdleFrames];
		idleDurationArray[0] = towerIdleDuration;
		
		idle = new Animation(idleFrames, idleDurationArray, false);
		setSprite(idle);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x + towerOffsetX, y + towerOffsetY, towerWidth, towerHeight);
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
	
	private void generate(long timestamp) {
		lastEnergyGen = timestamp;
	}
	
	public int getCost() {
		return buildCost;
	}
	
	public int getEnergy() {
		return energyRate;
	}
	
	public boolean timeToFire() {
		long time = System.currentTimeMillis();
		
		if (time - lastShotFired >= towerAttackDelay) {
			fire(time);
			return true;
		} else {
			return false;
		}
	}
	
	// TODO: consolidate with timeToFire?
	public boolean timeToGenerate() {
		long time = System.currentTimeMillis();
		
		if (time - lastEnergyGen >= towerGenDelay) {
			generate(time);
			return true;
		} else {
			return false;
		}
	}
	
	// TODO: exceptions
	public Bullet getBullet() throws SlickException {
		return new Bullet(level, x + towerBulletSpawnOffset, y);
	}
}
