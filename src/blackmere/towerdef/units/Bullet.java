package blackmere.towerdef.units;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Bullet extends Unit {

	private final static int bulletWidth = 20;
	private final static int bulletHeight = 10;
	private final static int damage = 10;
	private final float speed = 0.004f;
	private final int delta = 150;

	public Bullet(float startX, float startY) throws SlickException {
		super(bulletWidth, bulletHeight, startX, startY, 0, damage);
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle(x, y, width, height);
	}
	
	public void move() {
		x += delta * speed;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.cyan);
		g.fill(getBoundingBox());
		g.setColor(Color.black);
		g.draw(getBoundingBox());
	}
}
