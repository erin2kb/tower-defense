package blackmere.towerdef.util;

// TODO: note that this method isn't the best security-wise; a better solution
// would be to have a Constants file for each individual class/family [maybe??]
public class Constants {
	
	// TODO: remove unused resources from res folder
	
	// Constants used by GameManager and other top-level classes (such as "Demo")
	public final static float heroStartX = 186f;
	public final static float heroStartY = 248f;
	public final static float enemyStartX = 620f;
	public final static float enemyStartY = 248f;
	public final static float towerStartX = 62f;
	public final static float towerStartY = 124f;
	public final static int windowWidth = 682;
	public final static int windowHeight = 434;
	public final static int demoID = 0;		// state ID for the 'demo' state
	public final static int pauseID = 1;
	
	// Constants used by Unit
	public final static int tileSize = 62;
	public final static int rightBound = 620;
	public final static int leftBound = 62;
	public final static int upBound = 62;
	public final static int downBound = 372;
	public final static int hitDuration = 800;
	public final static int animationDelta = 150;	// currently used by all animations
	
	// Constants used by Hero
	public final static int heroWidth = 62;	// rightmost spear point to leftmost spear point
	public final static int heroHeight = 50;	// from tallest point of head to bottom of foot
	public final static int heroOffsetX = 0;
	public final static int heroOffsetY = 11;	
	public final static int heroTargetWidth = 24;
	public final static int heroTargetHeight = 48;
	public final static int heroTargetOffsetX = 18;
	public final static int heroTargetOffsetY = 12;
	public final static int heroMotionWidth = 30;
	public final static int heroMotionHeight = 47;
	public final static int heroMotionOffsetX = 18;
	public final static int heroMotionOffsetY = 12;
	public final static int heroAttackWidth = 13;
	public final static int heroAttackHeight = 5;
	public final static int heroAttackOffsetXRight = 49;
	public final static int heroAttackOffsetXLeft = 0;
	public final static int heroAttackOffsetY = 40;
	public final static int heroNumIdleFrames = 1;
	public final static int heroNumWalkFrames = 8;
	public final static int heroNumAttackFrames = 8;
	public final static int heroIdleDuration = 26000;	// doesn't actually matter at this point, since there's only one frame in the animation
	public final static int heroWalkDuration = 16000;		// 10000
	public final static int heroAttackDuration = 26000;	// 10000
	public final static int heroMaxHP = 200;
	public final static int heroDamage = 10;
	public final static float heroSpeed = 0.001f;			// 0.003f
	public final static int heroDelta = 150;		// TODO: consolidate deltas? document purpose of this delta
	public final static int heroAttackDelay = 1100;		// TODO: consolidate delays? is this delay correct/necessary?
	
	// TODO: adjust boxes so that when hero stays still (and enemy comes), hero attack looks like it's properly hitting the enemy
	
	// Constants used by Enemy
	public final static int enemyWidth = 62;
	public final static int enemyHeight = 45;
	public final static int enemyOffsetX = 0;
	public final static int enemyOffsetY = 18;
	public final static int enemyTargetWidthBullet = 30;
	public final static int enemyTargetHeightBullet = 30;
	public final static int enemyTargetOffsetBulletX = 24;
	public final static int enemyTargetOffsetBulletY = enemyOffsetY;
	public final static int enemyTargetWidthHero = 33;
	public final static int enemyTargetHeightHero = 20;
	public final static int enemyTargetOffsetHeroX = 18;
	public final static int enemyTargetOffsetHeroY = 21;
	public final static int enemyMotionWidth = 45;
	public final static int enemyMotionHeight = 30;
	public final static int enemyMotionOffsetX = 8;	// TODO: play with these values; bullets rarely visible when enemy right on top of tower; enemy looks too far to attack hero; make sure it can still reach hero, though
	public final static int enemyMotionOffsetY = enemyOffsetY;
	public final static int enemyAttackWidth = 18;
	public final static int enemyAttackHeight = 40;
	public final static int enemyAttackOffsetX = 0;
	public final static int enemyAttackOffsetY = 11;
	public final static int enemyNumIdleFrames = 1;
	public final static int enemyNumWalkFrames = 8;
	public final static int enemyNumAttackFrames = 8;
	public final static int enemyIdleDuration = 26000;	// doesn't actually matter at this point, since there's only one frame in the animation
	public final static int enemyWalkDuration = 16000;	// TODO: if same across all units, then consolidate (check for other consolidation opportunities too)
	public final static int enemyAttackDuration = 26000;
	public final static int enemyMaxHP = 160;
	public final static int enemyDamage = 10;
	public final static float enemySpeed = 0.0004f;		// 0.0008f
	public final static int enemyDelta = 150;
	public final static int enemyAttackDelay = 3000;
	
	// Constants used by Tower
	public final static int towerWidth = 52;
	public final static int towerHeight = 56;
	public final static int towerOffsetX = 5;
	public final static int towerOffsetY = 3;
	public final static int towerNumIdleFrames = 1;
	public final static int towerIdleDuration = 26000;	// doesn't actually matter at this point, since there's only one frame in the animation --> consolidate?
	public final static int towerBulletSpawnOffset = 34;
	public final static int towerMaxHP = 120;
	public final static int towerDamage = 0;	// since towers themselves don't do damage (only their bullets do)
	public final static int towerAttackDelay = 2200;
	
	// Constants used by Bullet
	public final static int bulletWidth = 20;
	public final static int bulletHeight = 10;
	public final static int bulletOffsetX = 21;
	public final static int bulletOffsetY = 26;
	public final static int bulletNumIdleFrames = 1;
	public final static int bulletIdleDuration = 26000;	// doesn't actually matter at this point, since there's only one frame in the animation
	public final static int bulletMaxHP = 10;	// 10 is a dummy value, since bullets don't really have HP (it can be any number > 0)
	public final static int bulletDamage = 10;
	public final static float bulletSpeed = 0.0006f;		// 0.001f
	public final static int bulletDelta = 150;
}
