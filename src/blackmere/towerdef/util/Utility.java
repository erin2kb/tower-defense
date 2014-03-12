package blackmere.towerdef.util;

import org.newdawn.slick.geom.Rectangle;

public class Utility {

	// TODO: document that this gets called by the unit checking if it's safe to move
	// TODO: remove this f'n, since it's only one line anyway?
	public static boolean detectCollision(Rectangle box, Rectangle otherBox) {	
		return box.intersects(otherBox);
	}
}
