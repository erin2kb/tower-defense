package blackmere.towerdef;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import static blackmere.towerdef.util.Constants.*;

public class PauseMenu extends BasicGameState {
	
	private Image background;
	
	private StateBasedGame gameManager;

	//
	public void init(GameContainer container, StateBasedGame manager)
			throws SlickException {
		gameManager = manager;
	}

	//
	public void render(GameContainer container, StateBasedGame manager, Graphics g)
			throws SlickException {
		background.draw(0, 0);
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
}
