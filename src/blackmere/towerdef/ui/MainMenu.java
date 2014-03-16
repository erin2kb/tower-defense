package blackmere.towerdef.ui;

import static blackmere.towerdef.util.Constants.*;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenu extends BasicGameState {
	private StateBasedGame gameManager;
	private Image background;
	private Button buttonStart, buttonQuit;
	private UnicodeFont bigFont, medFont;	// TODO: constant? diff. from pause/game over 'bigFont' though

	// TODO: figure out the warning
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame manager)
			throws SlickException {
		gameManager = manager;
		background = new Image("res/mainMenu.png");
		buttonStart = new Button(mainButtonOffsetX, mainButtonOffsetY, mainButtonWidth, mainButtonHeight);
		buttonQuit = new Button(mainButtonOffsetX, mainButtonOffsetY + mainButtonHeight + mainButtonOffsetGap, mainButtonWidth, mainButtonHeight);
		bigFont = new UnicodeFont(new Font("Verdana", Font.BOLD, 42)); 
		bigFont.getEffects().add(new ColorEffect(java.awt.Color.black));
		bigFont.addNeheGlyphs();
		bigFont.loadGlyphs();
		medFont = new UnicodeFont(new Font("Verdana", Font.BOLD, 36)); 
		medFont.getEffects().add(new ColorEffect(java.awt.Color.black));
		medFont.addNeheGlyphs();
		medFont.loadGlyphs();
		// TODO: make 'push down' effect on buttons when clicked
	}

	// TODO: common menu class?
	public void render(GameContainer container, StateBasedGame manager, Graphics g)
			throws SlickException {
		background.draw(0, 0);
		buttonStart.draw(g);	// TODO: click recognition slightly off on bottom of button??
		buttonQuit.draw(g);
		g.setColor(Color.black);
		g.drawString("Begin Demo", buttonStart.getX() + 4, buttonStart.getY() + 10);
		g.drawString("Quit", buttonQuit.getX() + 30, buttonQuit.getY() + 10);
		g.drawString("Game Version: " + gameVersion, rightBound - 100, downBound + 40);	// TODO: un-hard code
		g.setFont(bigFont);
		g.drawString("Night of a Thousand Bugs", 36, 30);	// TODO: un-hard code
		g.setFont(medFont);
		g.drawString("~Demo Version~", 160, 90);	// TODO: un-hard code
		// TODO: make some sort of 'cursor' between the buttons and enable arrow navigation
	}

	//
	public void update(GameContainer container, StateBasedGame manager, int delta)
			throws SlickException {
		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_Q)) {
			System.exit(0);
		}
	}

	//
	public int getID() {
		return mainMenuID;
	}
	
	// override of InputListener interface method
	public void mouseClicked(int button, int x, int y, int count) {
		if (buttonStart.getBoundingBox().contains(x, y)) {
			gameManager.enterState(demoID);
		} else if (buttonQuit.getBoundingBox().contains(x, y)) {
			System.exit(0);
		}
	}
}
