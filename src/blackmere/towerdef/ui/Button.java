package blackmere.towerdef.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

public class Button {
	private int x, y, width, height;
	private Image currentImage, buttonImage, lockedImage;
	private Rectangle boundingBox, outline;
	private boolean selected, locked;
	
	public Button(Image image, Image lockImage, int x, int y, int w, int h) {
		buttonImage = image;
		lockedImage = lockImage;
		currentImage = lockedImage;
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		selected = false;
		locked = true;
		boundingBox = new Rectangle(x, y, width, height);
		outline = new Rectangle(x - 1, y - 1, width + 2, height + 2);	// TODO: un-hard code
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public void draw(Graphics g) {
		currentImage.draw(x, y);
		
		if (selected) {
			g.setColor(Color.blue);	// TODO: un-hard code
			g.draw(outline);
		}
	}
	
	public void toggleSelect() {
		selected = !selected;		// TODO: make outline bolder
	}
	
	public void unlock() {
		locked = false;
		currentImage = buttonImage;
	}
	
	public boolean isLocked() {
		return locked;
	}
}
