package AlphaBattle;

import battlecode.common.*;

public class MilitaryUtil {

	// Shoots the enemy with the given id with the type 0 - single shot; type 1 - triad shot; type 2 - pentad shot
	public static void shootEnemy(RobotController rc, int type, int id) throws GameActionException {
		RobotInfo enemy = rc.senseRobot(id);
		
		switch (type) {
			case 0:  
				trySingleShot(rc, rc.getLocation().directionTo(enemy.getLocation())); 
				break;
			case 1: 
				tryTriadShot(rc, rc.getLocation().directionTo(enemy.getLocation()));
				break;
			case 2: 
				tryPentadShot(rc, rc.getLocation().directionTo(enemy.getLocation())); 
				break;
		}	
	}
		
	// Try to fire a single shot
	public static boolean trySingleShot(RobotController rc, Direction dir) throws GameActionException {
		if (rc.canFireSingleShot()) {
			rc.fireSingleShot(dir);
			return true; 
		}
		return false; 
	}

	// Try to fire a Triad shot 
	public static boolean tryTriadShot(RobotController rc, Direction dir) throws GameActionException {
		if (rc.canFireTriadShot()) {
			rc.fireTriadShot(dir);
			return true; 
		}
		return false;
	}
	
	// Try to fire a Pentad shot 
	public static boolean tryPentadShot(RobotController rc, Direction dir) throws GameActionException {
		if (rc.canFirePentadShot()) {
			rc.firePentadShot(dir);
			return true; 
		}
		return false;
	}
}
