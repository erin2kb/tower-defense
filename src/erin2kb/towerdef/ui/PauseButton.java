package erin2kb.towerdef.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class PauseButton extends Button {
	
	private Image buttonImage;

	public PauseButton(Image buttonIcon, int x, int y, int w, int h) {
		super(x, y, w, h);
		buttonImage = buttonIcon;
	}
	
	public void draw(Graphics g) {
		buttonImage.draw(x, y);
	}
}
