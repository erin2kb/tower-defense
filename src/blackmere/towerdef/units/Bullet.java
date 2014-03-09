package blackmere.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Bullet extends Unit {

	private final static int width = 20;
	private final static int height = 10;
	private final static int offsetX = 21;
	private final static int offsetY = 26;
	private final static int damage = 10;
	private final float speed = 0.001f;		// 0.004f
	private final int delta = 150;
	private final int numAnimFrames = 1;
	private final int animDuration = 26000;	// doesn't actually matter at this point, since there's only one frame in the animation
	private Image[] animFrames;
	private int[] animDurationArray;
	private Animation anim;

	public Bullet(float startX, float startY) throws SlickException {
		super(startX, startY, 10, damage);	// 10 is a dummy value, since bullets don't really have HP (it can be any number > 0)
		
		animFrames = new Image[numAnimFrames];
		animFrames[0] = new Image("res/bullet.png");
		animDurationArray = new int[1];
		animDurationArray[0] = animDuration;
		
		anim = new Animation(animFrames, animDurationArray, false);
		setSprite(anim);
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
	
	public Rectangle getAttackBox() {
		return getBoundingBox();
	}
	
	public void move() {
		x += delta * speed;
		
		if (x > rightBound) {
			// we went off the screen
			die();
		}
	}
	
	// TODO: consolidate with similar f'ns?
	public boolean detectBulletCollision(Enemy e) {
		Rectangle box = getBoundingBox();
		Rectangle otherBox = e.getBulletTargetBox();
		
		if (box.intersects(otherBox)) {
			return true;
		} else {
			return false;
		}
	}
}
