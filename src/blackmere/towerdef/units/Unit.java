package blackmere.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public abstract class Unit {
	protected float x, y;
	protected int currentHP, maxHP, damage;
	protected boolean dead, attacking, hit;
	protected Animation sprite;
	protected long lastHitUpdate, lastAttackUpdate;
	protected final int rightBound = 600;
	protected final int leftBound = 60;
	protected final int upBound = 60;
	protected final int downBound = 360;
	protected final int hitDuration = 800;
	
	public Unit(float startX, float startY, int HP, int dmg) {
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
	public abstract Rectangle getTargetBox();
	public abstract Rectangle getMotionBox();
	public abstract Rectangle getAttackBox();
	
	public void setSprite(Animation s) {
		sprite = s;
	}
	
	// TODO: make enemy attack as soon as in range, rather than as soon as blocked [? - unsure of current behavior]
	
	public void draw(Graphics g) {
		checkHit();
		
		if (hit) {
			sprite.draw((int) x, (int) y, Color.red);
		} else {
			sprite.draw((int) x, (int) y);
		}
		
		// DEBUG: draw a bounding box
		Rectangle box = (this instanceof Hero ? getAttackBox() : getTargetBox());
		g.setColor(Color.red);
		g.draw(box);
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
	
	public int getDamage() {
		return damage;
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
	
	public boolean isHit() {		// TODO: is this used?
		return hit;
	}
	
	public void checkHit() {
		long time = System.currentTimeMillis();		// TODO: optimize - use more reliable methods, like the nanoseconds one
		
		if (time - lastHitUpdate >= hitDuration) {
			recover();
		}
	}
	
	public void takeHit(int damageAmount) {
		hit = true;
		takeDamage(damageAmount);	// TODO: consolidate?
		lastHitUpdate = System.currentTimeMillis();
	}
	
	// TODO: document reason for dying here (final red flash)
	public void recover() {
		hit = false;
		
		if (currentHP <= 0) {
			die();
		}
	}
	
	// TODO: make background tiles 62x62 px
	
	public void takeDamage(int damageAmount) {
		currentHP -= damageAmount;
	}
	
	// TODO: move to hero/enemy? since not used by tower/bullet; rename??
	// TODO: document that this gets called by the unit checking if it's safe to move
	public boolean detectMotionCollision(Rectangle box, Rectangle otherBox) {	
		if (box.intersects(otherBox)) {
			return true;
		} else {
			return false;
		}
	}
}
