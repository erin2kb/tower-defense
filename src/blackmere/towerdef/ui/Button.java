package blackmere.towerdef.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

public class Button {
	private int x, y, width, height;
	private Image buttonImage;
	private Rectangle boundingBox, outline;
	private boolean selected;
	
	public Button(Image image, int x, int y, int w, int h) {
		buttonImage = image;
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		selected = false;
		boundingBox = new Rectangle(x, y, width, height);
		outline = new Rectangle(x - 1, y - 1, width + 2, height + 2);	// TODO: un-hard code
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public void draw(Graphics g) {
		buttonImage.draw(x, y);
		
		if (selected) {
			g.setColor(Color.blue);	// TODO: un-hard code
			g.draw(outline);
		}
	}
	
	public void toggleSelect() {
		selected = !selected;		// TODO: make outline bolder
	}
}
