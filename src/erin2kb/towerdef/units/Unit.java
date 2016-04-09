package erin2kb.towerdef.units;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import erin2kb.towerdef.Demo;

import static erin2kb.towerdef.util.Constants.*;

public abstract class Unit {
	protected float x, y, currentHP, maxHP, damage;
	protected boolean dead, attacking, hit;
	protected Animation sprite;
	protected long lastHitUpdate, lastAttackUpdate;
	protected Rectangle healthBar, healthGauge;
	protected Demo level;	// TODO: do this a better way
	protected Unit killer;	// TODO: use an enum instead?
	protected int healthOffsetX;
	
	public Unit(Demo lv, float startX, float startY, int HP, int dmg) {
		level = lv;
		x = startX;
		y = startY;
		healthOffsetX = healthBarOffsetX;	// only enemy uses a different value here (thus far)
		damage = dmg;
		maxHP = HP;
		currentHP = maxHP;
		dead = false;
		attacking = false;
		hit = false;
		killer = null;
		lastHitUpdate = System.currentTimeMillis();
		lastAttackUpdate = System.currentTimeMillis();
		healthBar = new Rectangle(x + healthBarOffsetX, y - healthBarOffsetY, healthBarWidth, healthBarHeight);
		healthGauge = new Rectangle(x + healthBarOffsetX, y - healthBarOffsetY, healthBarWidth, healthBarHeight);
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
		/* Rectangle box = (this instanceof Enemy ? getMotionBox() : getMotionBox());
		Rectangle box2 = getAttackBox();
		g.setColor(Color.red);
		g.draw(box);
		g.setColor(Color.blue);
		g.draw(box2); */
	}
	
	public void drawHealthBar(Graphics g) {
		// TODO: only draw health bar if a current target of something, or taking damage
		// TODO: make function to choose health color based on percent (used both here and in the UI text)
		if (this instanceof Bullet) {
			return; // don't draw health bars for bullets
		}
		
		float thirdHP = maxHP / 3;

		if (currentHP <= thirdHP) {
			g.setColor(Color.red);
		} else if (currentHP < 2 * thirdHP) {
			g.setColor(Color.yellow);
		} else {
			g.setColor(Color.green);
		}

		// update the health bar; TODO: do somewhere that's called less often?
		healthBar.setLocation(x + healthOffsetX, y - healthBarOffsetY);
		healthGauge.setLocation(x + healthOffsetX, y - healthBarOffsetY);
		healthGauge.setWidth((currentHP / maxHP) * healthBarWidth);

		g.fill(healthGauge);
		g.setColor(Color.black);
		g.draw(healthBar);
	}
	
	public float getDamage() {
		return damage;
	}
	
	public float getHP() {
		return currentHP;
	}
	
	public float getMaxHP() {
		return maxHP;
	}
	
	public int getLane() {
		return (int) Math.floor((y - 1) / tileSize);
	}
	
	public void die() {
		if (this instanceof Enemy) {
			level.anotherOneBitesTheDust(killer);
		}
		
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	private float getX() {
		return x;
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
	
	public void takeHit(float damageAmount, Unit attacker) {
		// if already dead, don't take any more damage
		if (currentHP <= 0) {
			return;
		}
		
		hit = true;
		lastHitUpdate = System.currentTimeMillis();
		currentHP -= damageAmount;
		
		// if killed by this blow, acknowledge the killer
		if (currentHP <= 0) {
			killer = attacker;
		}
	}
	
	// TODO: document reason for dying here (final red flash)
	public void recover() {
		hit = false;
		
		if (currentHP <= 0) {
			die();
		}
	}
	
	// TODO: remove this after fixing the hack in Bullet
	protected void setDamage(int dmg) {
		damage = dmg;
	}
	
	public boolean withinRange(Unit u) {
		// special case for towers
		if (u instanceof Tower) {
			return (this.getLane() == u.getLane()) && (this.getX() > u.getX());
		}
		
		Rectangle box = getTargetBox();
		Rectangle otherBox = u.getAttackBox();		// TODO: ensure this can't be called on units without an attackbox?
		
		return box.intersects(otherBox);
	}
}
