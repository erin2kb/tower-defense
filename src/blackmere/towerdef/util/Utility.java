package blackmere.towerdef.util;

import org.newdawn.slick.geom.Rectangle;

public class Utility {

	// TODO: document that this gets called by the unit checking if it's safe to move
	public static boolean detectCollision(Rectangle box, Rectangle otherBox) {	
		if (box.intersects(otherBox)) {
			return true;
		} else {
			return false;
		}
	}
}
