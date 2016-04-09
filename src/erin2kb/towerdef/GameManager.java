package erin2kb.towerdef;

import static erin2kb.towerdef.util.Constants.windowHeight;
import static erin2kb.towerdef.util.Constants.windowWidth;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import erin2kb.towerdef.ui.GameOver;
import erin2kb.towerdef.ui.MainMenu;
import erin2kb.towerdef.ui.PauseMenu;
import erin2kb.towerdef.ui.Victory;


public class GameManager extends StateBasedGame {
	
	// constructor
	public GameManager(String title) {
		super(title);
	}

	// TODO [2016 revamp]: what is this function? where is it used?
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new MainMenu());
		addState(new Demo());
		addState(new PauseMenu());
		addState(new GameOver());
		addState(new Victory());
	}

	//
	public static void main(String[] args) {
		GameManager newGame = new GameManager("Night of a Thousand Bugs  ~Demo~");
		AppGameContainer gameContainer;
		// TODO: redirect System.err to a log file; test these log files

		try {
			gameContainer = new AppGameContainer(newGame);
			gameContainer.setDisplayMode(windowWidth, windowHeight, false);
			gameContainer.setShowFPS(false);
			/////gameContainer.setMinimumLogicUpdateInterval(100); // TODO: figure out how to use this?
			gameContainer.start();
		} catch (SlickException e) {

			/*File logFile = new File("logs/errors.log");

			if (! logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException e1) {
					System.err.println("Error creating log file");
				}
			}

			FileWriter fw = null;
			BufferedWriter bw = null;

			try {
				fw = new FileWriter(logFile.getAbsoluteFile());
				bw = new BufferedWriter(fw);
			} catch (IOException e1) {
				System.err.println("Error creating log file writers");
			}

			try {
				bw.write(e.toString());
			} catch (IOException e1) {
				System.err.println("Error writing to log file");
			}

			try {
				fw.close();
				bw.close();
			} catch (IOException e1) {
				System.err.println("Error closing log file writers");
			} */
		} 
	}
}
