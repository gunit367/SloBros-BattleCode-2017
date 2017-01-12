package AlphaBattle;

import battlecode.common.*;


public class Gardener {
	static RobotController rc; 
	

	public Gardener(RobotController rc) {
		Gardener.rc = rc; 
	}
	
	public void run() throws GameActionException {
		 System.out.println("I'm a gardener!");

	        // The code you want your robot to perform every round should be in this loop
	        while (true) {

	            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	            try {

	            	// Get Random Direction
	            	Direction dir = Util.randomDirection();
	            	
	                // Listen for home archon's location
	                int xPos = rc.readBroadcast(0);
	                int yPos = rc.readBroadcast(1);
	                MapLocation archonLoc = new MapLocation(xPos,yPos);

	                TreeInfo[] trees = rc.senseNearbyTrees();
	                TreeInfo tree = null;
	                
	                if (trees.length > 0) {
	                	tree = trees[0];
	                }
	                
	                if (tree != null) {
	                	if (rc.canWater(tree.ID)) {
	                		rc.water(tree.ID);
	                	} else {
	                        Util.tryMove(rc, Util.randomDirection());
	                	}
	                	
	                	if (rc.canShake(tree.ID)) {
	                		rc.shake(tree.ID);
	                	}
	                } else {
	                	plantTree();
	                }
	                
	                if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
	                	rc.buildRobot(RobotType.SOLDIER, dir);
	                }
	                
	                // Move away from archon
	                Util.tryMove(rc, archonLoc.directionTo(rc.getLocation()));

	                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
	                Clock.yield();

	            } catch (Exception e) {
	                System.out.println("Gardener Exception");
	                e.printStackTrace();
	            }
	        }
	}
	
	// The Logic for a given turn
	public void logic() {
	
	}
	
	// Returns an array of trees in robots sight. 
	public TreeInfo[] findTreesInSight() {
		return rc.senseNearbyTrees();
	}
	
	// NOTE: TOO MANY CODEBYTES //
	// Returns TreeInfo for closest tree in sight, Null if no trees in sight 
	public TreeInfo findClosestTree() {
		float closestDistance; 
		TreeInfo closest; 
		TreeInfo[] inSight; 
		
		inSight = findTreesInSight(); 
		closest = inSight[0];
		
		if (closest == null) {
			return null; 
		}
		
		closestDistance = Util.findDistance(rc.getLocation(), inSight[0].location);
		
		for (int i = 1; i < inSight.length; i++) {
			float distance = Util.findDistance(rc.getLocation(), inSight[i].location);
			if (distance < closestDistance) {
				closest = inSight[i];
				closestDistance = distance; 
			}
			
		}
		
		return closest; 
		
	}
	
	// Water given tree
	public void waterTree(TreeInfo tree) {
		
	}
	
	// Plants a tree behind the robot
	public void plantTree() throws GameActionException {
		Direction dir = Util.randomDirection();
		if (rc.canPlantTree(Util.randomDirection())) {
			rc.plantTree(dir);
			
		}
	}

	// Finds the fullest tree to shake if possible 
	public TreeInfo findTreeToShake() {
		return null; 
	}
	
	// Shakes the given tree
	public void shakeTree(TreeInfo tree) {
		
	}
	
	// Deploys a robot given 
	public void delpoyRobot(RobotType type) {
		
	}
}
