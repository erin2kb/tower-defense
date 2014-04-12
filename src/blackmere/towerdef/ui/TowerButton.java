package blackmere.towerdef.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

public class TowerButton extends Button {
	
	private Image currentImage, lockedImage, unlockedImage;
	private boolean selected;
	private Rectangle outline;

	public TowerButton(Image unlockImage, Image lockImage, int x, int y, int w, int h) {
		super(x, y, w, h);
		lockedImage = lockImage;
		unlockedImage = unlockImage;
		currentImage = lockedImage;
		selected = false;
		outline = new Rectangle(x - 1, y - 1, width + 2, height + 2);	// TODO: un-hard code
	}
	
	// TODO: remove if no longer used
	public void toggleSelect() {
		selected = !selected;		// TODO: make outline bolder
	}
	
	public void select() {
		selected = true;		// TODO: make outline bolder
	}
	
	public void deselect() {
		selected = false;		// TODO: make outline bolder
	}
	
	public void unlock() {
		currentImage = unlockedImage;
	}
	
	public void draw(Graphics g) {
		currentImage.draw(x, y);
		
		if (selected) {
			g.setColor(Color.blue);	// TODO: un-hard code
			g.draw(outline);
		}
	}
}
