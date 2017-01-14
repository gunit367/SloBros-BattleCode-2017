package AlphaBattle;

import battlecode.common.*;


public class Gardener {
	static RobotController rc;
	
	public static final int SHAKE_AMT = 0;
	public static final float NORTH = (float) 1.5708;

	public Gardener(RobotController rc) {
		Gardener.rc = rc; 
	}
	
	public void run() throws GameActionException {
		 System.out.println("I'm a gardener!");

	        // The code you want your robot to perform every round should be in this loop
	        while (true) {

	            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	            try {
	            	
	            	logic();

	            	// Get Random Direction
	            	Direction dir = Util.randomDirection();
	            	
	                // Listen for home archon's location
	                MapLocation archonLoc = TeamComms.getArchonLoc(rc);

	             	             
	                
	                int soldierCount = TeamComms.getSoldiers(rc);
	                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && soldierCount < 25) {
	                	rc.buildRobot(RobotType.SOLDIER, dir);
	                	TeamComms.updateSoldiers(rc, soldierCount + 1);
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
	public void logic() throws GameActionException {
		System.out.println(TeamComms.getGardeners(rc));
		int numGardeners = TeamComms.getGardeners(rc);
		MapLocation archonLoc = TeamComms.getArchonLoc(rc);
		MapLocation enemyArchonLoc = TeamComms.getOppArchonLoc(rc);
		MapLocation wall = archonLoc.add(archonLoc.directionTo(enemyArchonLoc), 10); 
		
		if (numGardeners == 1) { 
			createTreeWall(wall, archonLoc.directionTo(enemyArchonLoc));
		} else if (numGardeners < 3) {
			waterPath(archonLoc.add(archonLoc.directionTo(enemyArchonLoc), 7));
		} else if (numGardeners < 6) {
			shakePath(archonLoc.add(archonLoc.directionTo(enemyArchonLoc), 7)); 
		}
	}
	
	// Returns an array of trees in robots sight. 
	public TreeInfo[] findTreesInSight() {
		return rc.senseNearbyTrees();
	}
	
	// NOTE: TOO MANY CODEBYTES //
	// Returns TreeInfo for closest tree in sight, Null if no trees in sight 
	public TreeInfo findClosestTree() {
		TreeInfo closest; 
		TreeInfo[] inSight; 
		float closestDistance; 
		
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
		if (rc.canWater() && rc.canWater(tree.ID))
		{
			try {
				// Attempt to water given tree
				rc.water(tree.ID);
			} catch (GameActionException e) {
				// ERROR: watering failed
			   System.out.println("ERROR: watering tree failed!");
			   e.printStackTrace();
			}
		}
		else
		{
			// Cannot water the tree OR tree has already been watered
			//System.out.println("I cannot water this tree! - Gardener");
		}
	}
	
	// Plants a tree behind the robot
	public void plantTree(Direction dir) throws GameActionException {
		if (rc.canPlantTree(dir)) {
			rc.plantTree(dir);
		}
		else
		{
			// Cannot plant tree
			//System.out.println("I cannot plant this tree! - Gardener");
		}
	}
	
	// Shakes the given tree
	public void shakeTree(TreeInfo tree) {
		if (rc.canShake(tree.ID) && tree.containedBullets > SHAKE_AMT)
		{
			try {
				// Attempt to shake given tree
				rc.shake(tree.ID);
			} catch (GameActionException e) {
				// ERROR: shaking failed
				System.out.println("ERROR: shaking tree failed!");
				e.printStackTrace();
			}
		}
		else 
		{
			// Robot cannot shake OR bullets < SHAKE_AMT
			//System.out.println("I cannot shake this tree! - Gardener");
		}
	}
	
	TreeInfo findTreeToWater() {
		TreeInfo[] treeInfo = findTreesInSight();
		
		if (treeInfo.length == 0) {
			return null; 
		} 
		
		TreeInfo lowestInWater = treeInfo[0];
		
		for (int i = 0; i < treeInfo.length; i++) {
			if (treeInfo[i].health < lowestInWater.health) {
				lowestInWater = treeInfo[i];
			}
		}
		
		return lowestInWater; 
				
		
	} 
	
	TreeInfo findTreeToShake() {
		TreeInfo[] treeInfo = findTreesInSight();
		
		if (treeInfo.length == 0) {
			return null; 
		} 
		
		TreeInfo highestInBullets = treeInfo[0];
		
		for (int i = 0; i < treeInfo.length; i++) {
			if (treeInfo[i].getContainedBullets() > highestInBullets.getContainedBullets()) {
				highestInBullets = treeInfo[i];
			}
		}
		
		return highestInBullets; 
				
		
	}
	
	public void waterPath(MapLocation location) throws GameActionException {
		TreeInfo tree = findTreeToWater(); 
		
		
		while (true) {
			
			if (tree != null) {
				tree = rc.senseTree(tree.ID);
				System.out.println("Tree: " + tree.ID + "Tree Health \nTreeHealth: " + tree.getHealth() + " Max Health: " + tree.getMaxHealth());
			}
			
			if (tree != null && rc.canWater(tree.ID)) {
				System.out.println("water");
				rc.water(tree.ID);
				if (tree.health == tree.maxHealth) {
					System.out.println("Max");
					tree = findTreeToWater();
				}
			} else if (tree != null && rc.canMove(tree.location)) {
				System.out.println("moved");
				Util.tryMove(rc, tree.location, 1);
			} else {
				System.out.println("Last");
				tree = findTreeToWater();
				if (rc.canMove(location)) {
					rc.move(location);
				}			
			}
			Clock.yield();
		}
	}
	
	public void shakePath(MapLocation location) throws GameActionException { 
		TreeInfo tree = findTreeToShake(); 
		
		while (true) {
			tree = findTreeToShake();
			if (tree != null && rc.canShake(tree.ID)) {
				rc.shake(tree.ID);
			} else if (tree != null && rc.canMove(tree.location)){
				rc.move(tree.location);
			} else {
				if (rc.canMove(location)) {
					rc.move(location);
				}
			}
			Clock.yield();
		}
	}
	
	// Create a tree wall 
	public void createTreeWall(MapLocation location, Direction dirToEnemy) throws GameActionException {
		boolean working = true; 
		boolean moveRight = false; 
		boolean createWall = false; 
		
		int numTrees = 0;
		int moves = 0;
		
		MapLocation sideOne = location.add(dirToEnemy.rotateLeftDegrees(90), 5);
		Direction dirBehind = dirToEnemy.rotateLeftDegrees(90);

		while (working) {
			
			if (createWall) {
				if (rc.canPlantTree(dirBehind)) {
					rc.plantTree(dirBehind);
					numTrees++;
					moves = 0; 
				} else if (rc.canMove(dirBehind.rotateLeftDegrees(180)) && moves < 3) {
					rc.move(dirBehind.rotateLeftDegrees(180));
					moves++;
				} 
				
				if (numTrees > 5) {
					working = false; 
				}
				
			} else if (moveRight) {
				if (rc.getLocation().equals(sideOne)) {
					createWall = true; 
				} else if (rc.canMove(sideOne)) {
					rc.move(sideOne);
				}
				
			} else {
				if (rc.getLocation().equals(location)) {
					moveRight = true; 
				} else if (rc.canMove(location)) {
					rc.move(location);
				}	
			}	
			
			Clock.yield();
		}
	}
	
	// Deploys a robot given 
	public void deployRobot(RobotType type) {
		Direction dir = new Direction(NORTH);
		if (rc.canBuildRobot(type, dir))
		{
			try {
				rc.buildRobot(type, dir);
			} catch (GameActionException e) {
				// ERROR: deployment failed
				System.out.println("ERROR: buildRobot failed!");
				e.printStackTrace();
			}
		}
		else 
		{
		   // Robot cannot build this robot
		   //System.out.println("I cannot deploy this robot! - Gardener");
		}
	}
}
