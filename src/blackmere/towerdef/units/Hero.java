package blackmere.towerdef.units;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.Movement;

// TODO: consolidate hero and enemy code
public class Hero extends Unit {
	private final static int width = 62;	// rightmost spear point to leftmost spear point
	private final static int height = 50;	// from tallest point of head to bottom of foot
	private final static int offsetY = 11;	
	private final static int targetWidth = 20;
	private final static int targetHeight = 48;
	private final static int targetOffsetX = 21;
	private final static int targetOffsetY = 12;
	private final static int motionWidth = 30;		// TODO: put constants in their own file??
	private final static int motionHeight = 47;
	private final static int motionOffsetX = 18;
	private final static int motionOffsetY = 12;
	private final static int attackWidth = 13;
	private final static int attackHeight = 5;
	private final static int attackOffsetXRight = 49;
	private final static int attackOffsetXLeft = 0;
	private final static int attackOffsetY = 40;
	private final static int maxHP = 200;
	private final static int damage = 10;
	private final int numWalkFrames = 8;
	private final int numAttackFrames = 8;
	private final int walkDuration = 16000;		// 10000
	private final int attackDuration = 26000;	// 10000
	private final float speed = 0.001f;			// 0.003f
	private final int delta = 150;
	private final int attackDelay = 1100;		// TODO: consolidate delays?
	private Image[] walkLeftFrames;
	private Image[] attackLeftFrames;
	private Image[] idleLeftFrames;
	private Image[] walkRightFrames;
	private Image[] attackRightFrames;
	private Image[] idleRightFrames;
	private int[] walkDurationArray;
	private int[] attackDurationArray;
	private int[] idleDurationArray;
	private Animation walkLeft, attackLeft, idleLeft, walkRight, attackRight, idleRight;
	private boolean facingRight;
	private Enemy target;

	
	// TODO: handle exceptions
	public Hero(float startX, float startY) throws SlickException {
		super(startX, startY, maxHP, damage);
		target = null;
		facingRight = true;
		
		walkLeftFrames = new Image[numWalkFrames];
		attackLeftFrames = new Image[numAttackFrames];
		idleLeftFrames = new Image[1];
		idleLeftFrames[0] = new Image("res/hero/wl1.png");
		walkRightFrames = new Image[numWalkFrames];
		attackRightFrames = new Image[numAttackFrames];
		idleRightFrames = new Image[1];
		idleRightFrames[0] = new Image("res/hero/wr1.png");
		walkDurationArray = new int[numWalkFrames];
		attackDurationArray = new int[numAttackFrames];
		idleDurationArray = new int[1];
		idleDurationArray[0] = attackDuration;
		
		for (int i = 0; i < numWalkFrames; i++) {
			int index = i + 1;
			String leftName = "res/hero/wl" + index + ".png";
			String rightName = "res/hero/wr" + index + ".png";
			walkLeftFrames[i] = new Image(leftName);
			walkRightFrames[i] = new Image(rightName);
			walkDurationArray[i] = walkDuration;
		}
		
		for (int i = 0; i < numAttackFrames; i++) {
			int index = i + 1;
			String leftName = "res/hero/al" + index + ".png";
			String rightName = "res/hero/ar" + index + ".png";
			attackLeftFrames[i] = new Image(leftName);
			attackRightFrames[i] = new Image(rightName);
			attackDurationArray[i] = attackDuration;
		}
		
		walkLeft = new Animation(walkLeftFrames, walkDurationArray, false);
		attackLeft = new Animation(attackLeftFrames, attackDurationArray, false);
		idleLeft = new Animation(idleLeftFrames, idleDurationArray, false);
		walkRight = new Animation(walkRightFrames, walkDurationArray, false);
		attackRight = new Animation(attackRightFrames, attackDurationArray, false);
		idleRight = new Animation(idleRightFrames, idleDurationArray, false);
		setSprite(idleRight);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x, y + offsetY, width, height);
	}
	
	public Rectangle getAttackBox() {
		int attackOffsetX = (facingRight ? attackOffsetXRight : attackOffsetXLeft);
		return new Rectangle(x + attackOffsetX, y + attackOffsetY, attackWidth, attackHeight);
	}
	
	public Rectangle getTargetBox() {
		return new Rectangle(x + targetOffsetX, y + targetOffsetY, targetWidth, targetHeight);
	}
	
	public Rectangle getMotionBox() {
		return new Rectangle(x + motionOffsetX, y + motionOffsetY, motionWidth, motionHeight);
	}

	private boolean safeToMove(Movement direction, ArrayList<Unit> units) {
		Rectangle box = getMotionBox();	// use box for more realistic collision checking
		float newX = box.getX();
		float newY = box.getY();
		
		switch (direction) {
		case UP:			// leaving this empty equates to "UP || DOWN"
		case DOWN:
			newY = newY + delta * (direction == Movement.DOWN ? speed : -speed);
			break;
		case LEFT:
		case RIGHT:
			newX = newX + delta * (direction == Movement.RIGHT ? speed : -speed);
			break;
		}
		
		if (newX + box.getWidth() > rightBound || newX < leftBound || newY + box.getHeight() > downBound || newY < upBound) {
			return false;
		}

		for (Unit u : units) {
			if (u instanceof Hero || u instanceof Bullet) {
				// don't be blocked by heroes (i.e. itself) or bullets
				continue;
			} else if (detectMotionCollision(u)) {
				return false;
			}
		}

		return true;
	}
	
	// TODO: issue with 'sticky' collision checks (can't move if boxes brush each other even once)
	// TODO: hero doesn't die properly
	
	// TODO: use doubles instead of floats throughout?
	public void move(Movement direction, ArrayList<Unit> units) {
		if (! safeToMove(direction, units)) {
			// TODO: condense this function's switch statements if at all possible,
			// while still allowing player to change direction even if blocked
			switch(direction) {
			case LEFT:
			case RIGHT:
				facingRight = (direction == Movement.RIGHT ? true : false);
				break;
			}
			idle();
			return;
		}

		switch(direction) {
		case LEFT:
		case RIGHT:
			facingRight = (direction == Movement.RIGHT ? true : false);
			x = x + delta * (direction == Movement.RIGHT ? speed : -speed);
			break;
		case UP:
		case DOWN:
			y = y + delta * (direction == Movement.DOWN ? speed : -speed);
			break;
		}
		
		sprite = (facingRight ? walkRight : walkLeft);
		sprite.update(delta);
	}
	
	public void idle() {
		sprite = (facingRight ? idleRight : idleLeft);
		sprite.update(delta);
	}
	
	public void attack(ArrayList<Unit> units) {
		sprite = (facingRight ? attackRight : attackLeft);	// TODO: always use getter/setter methods for unit fields?? [makes code longer]
		sprite.stopAt(numAttackFrames - 1);
		attacking = true;
		lastAttackUpdate = System.currentTimeMillis();
		
		for (Unit u : units) {
			if (u instanceof Enemy && ((Enemy) u).withinRange(this)) {
				target = (Enemy) u;
				return;
			}
		}
	}
	
	// TODO: diff weapon box dims based on direction facing??
	
	
	// TODO: move to unit??
	public void checkAttack() {
		if (target != null && target.isDead()) {
			target = null;	// TODO: right spot for this?
		}
		
		if (target != null) {
			long time = System.currentTimeMillis();
			
			if (time - lastAttackUpdate >= attackDelay) {
				target.takeHit();
				// TODO: fix this part so it doesn't get called a million times
			}
		}
		
		if (sprite.isStopped()) {
			attacking = false;
			sprite.restart();
			idle();
			
			if (target != null) {
				target.takeDamage(getDamage());		// TODO: optimize null checks
			}
			
			return;
		}
		
		sprite.update(delta);
	}
	
	// TODO: consolidate with tower, and enemy if possible
	public boolean withinRange(Enemy e) {
		Rectangle box = getTargetBox();
		Rectangle otherBox = e.getAttackBox();
		
		return box.intersects(otherBox);
	}
}
