package blackmere.towerdef.ui;

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

import static blackmere.towerdef.util.Constants.*;

public class Victory extends BasicGameState {
	private StateBasedGame gameManager;
	private GameContainer gameContainer;
	private Image background, dialog;
	private Rectangle dialogOutline;
	private Button buttonReplay, buttonQuit;
	private UnicodeFont bigFont;	// TODO: constant?

	// TODO: figure out the warning
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame manager)
			throws SlickException {
		gameManager = manager;
		gameContainer = container;
		dialog = new Image("blackmere/towerdef/res/dialogPause.png");	// TODO: don't just copy Pause [[rename the image]]
		dialogOutline = new Rectangle(pauseDialogX, pauseDialogY, pauseDialogWidth, pauseDialogHeight);	// TODO: don't just copy Pause, use own constants
		buttonReplay = new Button(pauseDialogX + victoryButtonOffsetX, pauseDialogY + pauseButtonOffsetY, victoryButtonWidth, pauseButtonHeight);
		buttonQuit = new Button(pauseDialogX + victoryButtonOffsetX + pauseButtonWidth + pauseButtonOffsetGap, pauseDialogY + pauseButtonOffsetY, victoryButtonWidth, pauseButtonHeight);
		bigFont = new UnicodeFont(new Font("Verdana", Font.BOLD, 28)); 
		bigFont.getEffects().add(new ColorEffect(java.awt.Color.black));
		bigFont.addNeheGlyphs();
		bigFont.loadGlyphs();
	}

	//
	public void render(GameContainer container, StateBasedGame manager, Graphics g)
			throws SlickException {
		background.draw(0, 0);
		dialog.draw(pauseDialogX, pauseDialogY);		// TODO: make the dialog semi-transparent	
		g.setColor(Color.black);
		g.draw(dialogOutline);
		buttonReplay.draw(g);	// TODO: click recognition slightly off on bottom of button??
		buttonQuit.draw(g);
		g.setColor(Color.black);
		g.drawString("Play Again", buttonReplay.getX() + 10, buttonReplay.getY() + 10);
		g.drawString("Quit", buttonQuit.getX() + 36, buttonQuit.getY() + 10);	// TODO: un-hard code
		g.drawString("You defeated all " + numEnemiesTotal + " spiders!", pauseDialogX + 30, pauseDialogY + 70);	// TODO: add KO count to game over screen
		g.setFont(bigFont);
		g.drawString("VICTORY!", pauseDialogX + 80, pauseDialogY + 16);	// TODO: un-hard code
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
		return victoryID;
	}

	//
	public void setBackground(Image bg) {
		background = bg;
	}
	
	// override of InputListener interface method
	public void mouseClicked(int button, int x, int y, int count) {
		if (buttonReplay.getBoundingBox().contains(x, y)) {
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
