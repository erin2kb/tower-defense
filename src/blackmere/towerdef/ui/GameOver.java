package blackmere.towerdef.ui;

import static blackmere.towerdef.util.Constants.demoID;
import static blackmere.towerdef.util.Constants.pauseButtonHeight;
import static blackmere.towerdef.util.Constants.pauseButtonOffsetGap;
import static blackmere.towerdef.util.Constants.pauseButtonOffsetX;
import static blackmere.towerdef.util.Constants.pauseButtonOffsetY;
import static blackmere.towerdef.util.Constants.pauseButtonWidth;
import static blackmere.towerdef.util.Constants.pauseDialogHeight;
import static blackmere.towerdef.util.Constants.pauseDialogWidth;
import static blackmere.towerdef.util.Constants.pauseDialogX;
import static blackmere.towerdef.util.Constants.pauseDialogY;
import static blackmere.towerdef.util.Constants.gameOverID;

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

public class GameOver extends BasicGameState {
	private StateBasedGame gameManager;
	private GameContainer gameContainer;
	private Image background, dialog;
	private Rectangle dialogOutline;
	private Button buttonRetry, buttonQuit;
	private UnicodeFont bigFont;	// TODO: constant?

	// TODO: figure out the warning
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame manager)
			throws SlickException {
		gameManager = manager;
		gameContainer = container;
		dialog = new Image("blackmere/towerdef/res/dialogGameOver.png");
		dialogOutline = new Rectangle(pauseDialogX, pauseDialogY, pauseDialogWidth, pauseDialogHeight);	// TODO: don't just copy Pause, use own constants
		buttonRetry = new Button(pauseDialogX + pauseButtonOffsetX, pauseDialogY + pauseButtonOffsetY, pauseButtonWidth, pauseButtonHeight);
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
		dialog.draw(pauseDialogX, pauseDialogY);		// TODO: make the dialog semi-transparent	
		g.setColor(Color.black);
		g.draw(dialogOutline);
		buttonRetry.draw(g);	// TODO: click recognition slightly off on bottom of button??
		buttonQuit.draw(g);
		g.setColor(Color.black);
		g.drawString("Try Again", buttonRetry.getX() + 4, buttonRetry.getY() + 10);
		g.drawString("Quit", buttonQuit.getX() + 26, buttonQuit.getY() + 10);
		g.setFont(bigFont);
		g.drawString("GAME OVER!", pauseDialogX + 56, pauseDialogY + 30);	// TODO: un-hard code
	}

	//
	public void update(GameContainer container, StateBasedGame manager, int delta)
			throws SlickException {
		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_Q)) {
			System.exit(0);		// TODO: document this and other 'secret' controls for user
		}
	}

	//
	public int getID() {
		return gameOverID;
	}

	//
	public void setBackground(Image bg) {
		background = bg;
	}
	
	// override of InputListener interface method
	public void mouseClicked(int button, int x, int y, int count) {
		if (buttonRetry.getBoundingBox().contains(x, y)) {
			try {
				gameManager.getState(demoID).init(gameContainer, gameManager);
				gameManager.enterState(demoID);
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (buttonQuit.getBoundingBox().contains(x, y)) {
			System.exit(0);
		}
	}
}
