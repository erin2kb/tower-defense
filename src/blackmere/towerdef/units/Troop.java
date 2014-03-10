package blackmere.towerdef.units;

import static blackmere.towerdef.util.Constants.enemyAttackDelay;
import static blackmere.towerdef.util.Constants.enemyDelta;
import static blackmere.towerdef.util.Constants.enemySpeed;

import java.util.ArrayList;

import org.newdawn.slick.geom.Rectangle;

import blackmere.towerdef.util.Utility;

public abstract class Troop extends Unit {
	
	protected Unit target;

	public Troop(float startX, float startY, int HP, int dmg) {
		super(startX, startY, HP, dmg);
	}
	
	private boolean safeToMove(ArrayList<Unit> units) {
		Rectangle box = getMotionBox();
		float newX = box.getX() - enemyDelta * enemySpeed;
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
	
	public void move(ArrayList<Unit> units) {
		if (safeToMove(units)) {
			x -= enemyDelta * enemySpeed;
		}
	}
	
	// TODO: consolidate
	public void checkAttack(ArrayList<Unit> units) {		
		if (target != null) {
			if (target.isDead() ||
					(target instanceof Hero && !((Hero) target).withinRange(this)) ||
					(target instanceof Tower && !((Tower) target).withinRange(this))) {
				target = null;		// TODO: figure out the if statement; document; repeat in hero if needed
			}
		}
		
		if (target == null) {
			attack(units);	// find new target; do in hero??
		}
		
		if (target != null) {
			long time = System.currentTimeMillis();
			
			if (time - lastAttackUpdate >= enemyAttackDelay) {
				target.takeHit(getDamage());
				lastAttackUpdate = time;
			}
		}
	}
	
	private void attack(ArrayList<Unit> units) {
		for (Unit u : units) {
			if (u instanceof Hero) {	// TODO: consolidate branches
				if (((Hero) u).withinRange(this)) {
					lastAttackUpdate = System.currentTimeMillis();
					target = u;
				}
			} else if (u instanceof Tower) {
				if (((Tower) u).withinRange(this)) {
					lastAttackUpdate = System.currentTimeMillis();
					target = u;
				}
			}
		}
	}
}
