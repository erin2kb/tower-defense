package blackmere.towerdef.units;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.Demo;
import blackmere.towerdef.util.Direction;
import blackmere.towerdef.util.Utility;
import static blackmere.towerdef.util.Constants.*;

// TODO: consolidate hero and enemy code, if possible
public class Hero extends Unit {
	private Image[] walkLeftFrames, attackLeftFrames, idleLeftFrames, walkRightFrames, attackRightFrames, idleRightFrames;
	private int[] walkDurationArray, attackDurationArray, idleDurationArray;
	private Animation walkLeft, attackLeft, idleLeft, walkRight, attackRight, idleRight;
	private boolean facingRight, damageDone;
	private Enemy target;

	
	// TODO: handle exceptions
	public Hero(Demo lv, float startX, float startY) throws SlickException {
		super(lv, startX, startY, heroMaxHP, heroDamage);
		target = null;
		facingRight = true;
		damageDone = false;
		
		walkLeftFrames = new Image[heroNumWalkFrames];
		attackLeftFrames = new Image[heroNumAttackFrames];
		idleLeftFrames = new Image[heroNumIdleFrames];
		idleLeftFrames[0] = new Image("res/hero/wl1.png");
		walkRightFrames = new Image[heroNumWalkFrames];
		attackRightFrames = new Image[heroNumAttackFrames];
		idleRightFrames = new Image[heroNumIdleFrames];
		idleRightFrames[0] = new Image("res/hero/wr1.png");
		walkDurationArray = new int[heroNumWalkFrames];
		attackDurationArray = new int[heroNumAttackFrames];
		idleDurationArray = new int[heroNumIdleFrames];
		idleDurationArray[0] = heroIdleDuration;
		
		for (int i = 0; i < heroNumWalkFrames; i++) {
			int index = i + 1;
			String leftName = "res/hero/wl" + index + ".png";
			String rightName = "res/hero/wr" + index + ".png";
			walkLeftFrames[i] = new Image(leftName);
			walkRightFrames[i] = new Image(rightName);
			walkDurationArray[i] = heroWalkDuration;
		}
		
		for (int i = 0; i < heroNumAttackFrames; i++) {
			int index = i + 1;
			String leftName = "res/hero/al" + index + ".png";
			String rightName = "res/hero/ar" + index + ".png";
			attackLeftFrames[i] = new Image(leftName);
			attackRightFrames[i] = new Image(rightName);
			attackDurationArray[i] = heroAttackDuration;
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
		return new Rectangle(x + heroOffsetX, y + heroOffsetY, heroWidth, heroHeight);
	}
	
	public Rectangle getAttackBox() {
		int attackOffsetX = (facingRight ? heroAttackOffsetXRight : heroAttackOffsetXLeft);
		return new Rectangle(x + attackOffsetX, y + heroAttackOffsetY, heroAttackWidth, heroAttackHeight);
	}
	
	public Rectangle getTargetBox() {
		return new Rectangle(x + heroTargetOffsetX, y + heroTargetOffsetY, heroTargetWidth, heroTargetHeight);
	}
	
	public Rectangle getMotionBox() {
		return new Rectangle(x + heroMotionOffsetX, y + heroMotionOffsetY, heroMotionWidth, heroMotionHeight);
	}
	
	private boolean safeToMove(Direction direction, ArrayList<Unit> units) {
		Rectangle box = getMotionBox();
		float newX = box.getX();
		float newY = box.getY();
		
		switch (direction) {
		case UP:			// leaving this empty equates to "UP || DOWN"
		case DOWN:
			newY = newY + heroDelta * (direction == Direction.DOWN ? heroSpeed : -heroSpeed);
			break;
		case LEFT:
		case RIGHT:
			newX = newX + heroDelta * (direction == Direction.RIGHT ? heroSpeed : -heroSpeed);
			break;
		}
		
		// don't let hero go outside of the screen
		if (newX + box.getWidth() > rightBound || newX < leftBound || newY + box.getHeight() > downBound || newY < upBound) {
			return false;
		}
		
		Rectangle newBox = new Rectangle(newX, newY, box.getWidth(), box.getHeight());

		for (Unit u : units) {
			if (u instanceof Hero || u instanceof Bullet) {
				// don't be blocked by heroes (i.e. itself) or bullets
				continue;
			} else if (Utility.detectCollision(newBox, u.getMotionBox())) {
				return false;
			}
		}

		return true;
	}

	// TODO: use doubles instead of floats throughout?
	public void move(Direction direction, ArrayList<Unit> units) {
		switch(direction) {
		case LEFT:
		case RIGHT:
			facingRight = (direction == Direction.RIGHT ? true : false);
			if (safeToMove(direction, units)) {
				x = x + heroDelta * (direction == Direction.RIGHT ? heroSpeed : -heroSpeed);
			}
			break;
		case UP:
		case DOWN:
			if (safeToMove(direction, units)) {
				y = y + heroDelta * (direction == Direction.DOWN ? heroSpeed : -heroSpeed);
			}
			break;
		}

		sprite = (facingRight ? walkRight : walkLeft);
		sprite.update(animationDelta);
	}
	
	public void attack(ArrayList<Unit> units) {
		sprite = (facingRight ? attackRight : attackLeft);
		sprite.stopAt(heroNumAttackFrames - 1);
		attacking = true;
		lastAttackUpdate = System.currentTimeMillis();	// TODO: document differences b/w this f'n and enemy ver. of this f'n
		
		for (Unit u : units) {
			if (u instanceof Enemy && ((Enemy) u).withinRange(this)) {
				target = (Enemy) u;
				return;
			}
		}
	}
	
	public void checkAttack() {
		if (target != null && target.isDead()) {
			target = null;
		}
		
		if (sprite.isStopped()) {
			attacking = false;
			damageDone = false;
			sprite.restart();
			idle();
			return;
		}
		
		if (target != null && !damageDone && target.withinRange(this)) {
			long time = System.currentTimeMillis();
			
			if (time - lastAttackUpdate >= heroAttackDelay) {
				target.takeHit(getDamage());
				damageDone = true;
			}
		}
		
		sprite.update(animationDelta);
	}
	
	// TODO: check all access levels (protected, etc.)
	
	// TODO: move to Unit??
	public void idle() {
		sprite = (facingRight ? idleRight : idleLeft);
		sprite.update(heroDelta);
	}
}
