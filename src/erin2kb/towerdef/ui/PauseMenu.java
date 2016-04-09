package erin2kb.towerdef.ui;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import static erin2kb.towerdef.util.Constants.*;

public class PauseMenu extends BasicGameState {
	
	private StateBasedGame gameManager;
	private Image background, dialog;
	private Rectangle dialogOutline;
	private Button buttonResume, buttonQuit;
	private UnicodeFont bigFont;	// TODO: constant?

	// TODO: figure out the warning -- could it be because of the image-loading issue, i.e. when I changed the package name so it couldn't find the image??
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame manager)
			throws SlickException {
		gameManager = manager;
		dialog = new Image("erin2kb/towerdef/res/dialogPause.png");
		dialogOutline = new Rectangle(pauseDialogX, pauseDialogY, pauseDialogWidth, pauseDialogHeight);
		buttonResume = new Button(pauseDialogX + pauseButtonOffsetX, pauseDialogY + pauseButtonOffsetY, pauseButtonWidth, pauseButtonHeight);
		buttonQuit = new Button(pauseDialogX + pauseButtonOffsetX + pauseButtonWidth + pauseButtonOffsetGap, pauseDialogY + pauseButtonOffsetY, pauseButtonWidth, pauseButtonHeight);
		bigFont = new UnicodeFont(new Font("Verdana", Font.BOLD, 28)); 
		bigFont.getEffects().add(new ColorEffect(java.awt.Color.black));
		bigFont.addNeheGlyphs();
		bigFont.loadGlyphs();
	}

	//
	public void render(GameContainer container, StateBasedGame manager, Graphics g)
			throws SlickException {
		background.draw(0, 0, Color.lightGray);
		dialog.draw(pauseDialogX, pauseDialogY);		// TODO: make pause dialog semi-transparent	
		g.setColor(Color.black);
		g.draw(dialogOutline);
		buttonResume.draw(g);	// TODO: click recognition slightly off on bottom of button??
		buttonQuit.draw(g);
		g.setColor(Color.black);
		g.drawString("Resume", buttonResume.getX() + 16, buttonResume.getY() + 10);
		g.drawString("Quit", buttonQuit.getX() + 26, buttonQuit.getY() + 10);
		g.setFont(bigFont);
		g.drawString("GAME PAUSED", pauseDialogX + 40, pauseDialogY + 30);	// TODO: un-hard code
	}

	//
	public void update(GameContainer container, StateBasedGame manager, int delta)
			throws SlickException {
		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_P)) {
			// TODO: enable KEY_PAUSE as well (and test it somehow...)
			gameManager.enterState(demoID);
		}
	}

	//
	public int getID() {
		return pauseID;
	}

	//
	public void setBackground(Image bg) {
		background = bg;
	}
	
	// override of InputListener interface method
	public void mouseClicked(int button, int x, int y, int count) {
		if (buttonResume.getBoundingBox().contains(x, y)) {
			gameManager.enterState(demoID);
		} else if (buttonQuit.getBoundingBox().contains(x, y)) {
			System.exit(0);
		}
	}
}
