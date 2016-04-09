package erin2kb.towerdef.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import static erin2kb.towerdef.util.Constants.*;

public class Button {
	protected int x, y, width, height;
	protected Rectangle boundingBox;
	
	public Button(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		boundingBox = new Rectangle(x, y, width, height);
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public void draw(Graphics g) {
		g.setColor(buttonColor);
		g.fill(boundingBox);
		g.setColor(Color.black);	// TODO: un-hard code
		g.draw(boundingBox);
	}
	
	public int getX() {
		return x;
	}
	
	// TODO: remove these getters (functionality is to be replaced; see other todos)
	public int getY() {
		return y;
	}
}
