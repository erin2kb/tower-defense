package blackmere.towerdef.units;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.util.Direction;
import blackmere.towerdef.util.Utility;
import static blackmere.towerdef.util.Constants.*;

// TODO: consolidate hero and enemy code
public class Hero extends Troop {
	private Image[] walkLeftFrames, attackLeftFrames, idleLeftFrames, walkRightFrames, attackRightFrames, idleRightFrames;
	private int[] walkDurationArray, attackDurationArray, idleDurationArray;
	private Animation walkLeft, attackLeft, idleLeft, walkRight, attackRight, idleRight;
	private boolean facingRight, damageDone;
	private Enemy target;

	
	// TODO: handle exceptions
	public Hero(float startX, float startY) throws SlickException {
		super(startX, startY, heroMaxHP, heroDamage);
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
		Rectangle box = getMotionBox();	// use box for more realistic collision checking
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
		if (! safeToMove(direction, units)) {
			// TODO: condense this function's switch statements if at all possible,
			// while still allowing player to change direction even if blocked
			switch(direction) {
			case LEFT:
			case RIGHT:
				facingRight = (direction == Direction.RIGHT ? true : false);
				break;
			}
			idle();
			return;
		}

		switch(direction) {
		case LEFT:
		case RIGHT:
			facingRight = (direction == Direction.RIGHT ? true : false);
			x = x + heroDelta * (direction == Direction.RIGHT ? heroSpeed : -heroSpeed);
			break;
		case UP:
		case DOWN:
			y = y + heroDelta * (direction == Direction.DOWN ? heroSpeed : -heroSpeed);
			break;
		}
		
		sprite = (facingRight ? walkRight : walkLeft);
		sprite.update(heroDelta);
	}
	
	public void idle() {
		sprite = (facingRight ? idleRight : idleLeft);
		sprite.update(heroDelta);
	}
	
	public void attack(ArrayList<Unit> units) {
		sprite = (facingRight ? attackRight : attackLeft);	// TODO: cleanup unneeded getters/setters/checkers
		sprite.stopAt(heroNumAttackFrames - 1);
		attacking = true;
		lastAttackUpdate = System.currentTimeMillis();
		
		for (Unit u : units) {
			if (u instanceof Enemy && ((Enemy) u).withinRange(this)) {
				target = (Enemy) u;
				return;
			}
		}
	}
		
	// TODO: move to unit??
	public void checkAttack() {
		if (target != null && target.isDead()) {
			target = null;		// TODO: should this be in attack() or somewhere else instead?
		}
		
		if (sprite.isStopped()) {
			attacking = false;
			damageDone = false;
			sprite.restart();
			idle();		// TODO: optimize null checks?
			return;
		}
		
		if (target != null && !damageDone && target.withinRange(this)) {
			long time = System.currentTimeMillis();
			
			if (time - lastAttackUpdate >= heroAttackDelay) {
				target.takeHit(getDamage());
				damageDone = true;
			}
		}
		
		sprite.update(heroDelta);
	}
	
	// TODO: consolidate with tower, and enemy if possible
	public boolean withinRange(Enemy e) {
		Rectangle box = getTargetBox();
		Rectangle otherBox = e.getAttackBox();
		
		return box.intersects(otherBox);
	}
}
