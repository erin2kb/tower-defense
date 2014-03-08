package blackmere.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

public abstract class Unit {
	protected float x, y;
	protected int width, height, currentHP, maxHP, damage;
	protected boolean dead, attacking, hit;
	protected Animation sprite;
	protected long lastHitUpdate, lastAttackUpdate;
	protected final int rightBound = 600;
	protected final int leftBound = 60;
	protected final int upBound = 60;
	protected final int downBound = 360;
	protected final int hitDuration = 800;
	
	public Unit(int w, int h, float startX, float startY, int HP, int dmg) {
		setWidth(w);
		setHeight(h);
		setX(startX);		// TODO: HP getter/setters?
		setY(startY);
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
	
	public void setSprite(Animation s) {
		sprite = s;
	}
	
	public void draw() {
		checkHit();
		
		if (hit) {
			sprite.draw((int) x, (int) y, Color.red);
		} else {
			sprite.draw((int) x, (int) y);
		}
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void die() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	// TODO: necessary?
	public boolean isAttacking() {
		return attacking;
	}
	
	public boolean isHit() {
		return hit;
	}
	
	public void checkHit() {
		long time = System.currentTimeMillis();
		
		if (time - lastHitUpdate >= hitDuration) {
			recover();
		}
	}
	
	public void takeHit() {
		hit = true;
		lastHitUpdate = System.currentTimeMillis();
	}
	
	// TODO: document reason for dying here (final red flash)
	public void recover() {
		hit = false;
		
		if (currentHP <= 0) {
			die();
		}
	}
	
	public void takeDamage(int damageAmount) {
		currentHP -= damageAmount;
	}
	
	// TODO: clean this up; diff. bound boxes for diff. cases; prevent 'stepping on' enemies from behind
	// TODO: document which unit must call this (i.e. to use correct bound box)
	public boolean detectCollision(float newX, float newY, Unit other) {
		Rectangle box = new Rectangle(newX, newY, width, height);
		Rectangle otherBox = other.getBoundingBox();
		
		if (box.intersects(otherBox)) {
			return true;
		} else {
			return false;
		}
	}
}
