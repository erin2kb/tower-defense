package blackmere.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import static blackmere.towerdef.util.Constants.*;

public abstract class Unit {
	protected float x, y;
	protected int currentHP, maxHP, damage;
	protected boolean dead, attacking, hit;
	protected Animation sprite;
	protected long lastHitUpdate, lastAttackUpdate;
	
	public Unit(float startX, float startY, int HP, int dmg) {
		x = startX;
		y = startY;
		damage = dmg;
		maxHP = HP;
		currentHP = maxHP;
		dead = false;
		attacking = false;
		hit = false;
		lastHitUpdate = System.currentTimeMillis();
		lastAttackUpdate = System.currentTimeMillis();
	}
	
	public abstract Rectangle getBoundingBox();
	public abstract Rectangle getTargetBox();
	public abstract Rectangle getMotionBox();
	public abstract Rectangle getAttackBox();
	
	public void setSprite(Animation s) {
		sprite = s;
	}
	
	public void draw(Graphics g) {
		checkHit();
		
		if (hit) {
			sprite.draw((int) x, (int) y, Color.red);
		} else {
			sprite.draw((int) x, (int) y);
		}
				
		// DEBUG: draw a bounding box
		/*Rectangle box = (this instanceof Enemy ? getAttackBox() : getBoundingBox());
		g.setColor(Color.red);
		g.draw(box); */
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getLane() {
		return (int) Math.floor((y - 1) / tileSize);
	}
	
	public void die() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public boolean isAttacking() {
		return attacking;
	}
	
	public void checkHit() {
		long time = System.currentTimeMillis();		// TODO: optimize - use more reliable methods, like the nanoseconds one
		
		if (time - lastHitUpdate >= hitDuration) {
			recover();
		}
	}
	
	public void takeHit(int damageAmount) {
		hit = true;
		currentHP -= damageAmount;
		lastHitUpdate = System.currentTimeMillis();
	}
	
	// TODO: document reason for dying here (final red flash)
	public void recover() {
		hit = false;
		
		if (currentHP <= 0) {
			die();
		}
	}
	
	public boolean withinRange(Unit u) {
		Rectangle box = getTargetBox();
		Rectangle otherBox = u.getAttackBox();		// TODO: ensure this can't be called on units without an attackbox?
		
		return box.intersects(otherBox);
	}
}
