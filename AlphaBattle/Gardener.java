package AlphaBattle;

import battlecode.common.*;


public class Gardener extends RobotPlayer {
	RobotController rc;
	GardenerMemory mem;
	
	public static final int SHAKE_AMT = 0;
	public static final float NORTH = (float) 1.5708;

	public Gardener(RobotController rc) {
		this.rc = rc; 
		mem = new GardenerMemory(rc);
		mem.setStrat(3);
	}
	
	// Gardener run method
	public void run() throws GameActionException {
		while (true) {
            try {
        		int strat = mem.getStrat();
	           	logic(strat);
	           	Clock.yield();
            } catch (Exception e) {
            	System.out.println("Gardener Exception");
	            e.printStackTrace();
	        }
		}
	}
	
	// The Logic for a given turn
	public void logic(int strat) throws GameActionException {
		switch (strat) {
		case 0:
			wallStrat(); 
			break;
		case 1: 
			normalStrat(); 
			break;
		case 2: 
			tryPlantFarm();
			break;
		case 3: 
			deployInitialLumberjack();
			break;
		}
	}
	
	// Depending on gardener count, gardeners make a wall in front of archon 
	public void wallStrat() throws GameActionException {
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
	
	// The normal strategy
	public void normalStrat() throws GameActionException {
		TreeInfo[] trees = rc.senseNearbyTrees();
		RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		boolean success = false; 
		
		// Look to see how many trees are around you. if none plant one and continue shaking and watering without moving. 
		if (trees.length > 0) {
			System.out.println("Trees > 0");
			//shakeTree(trees[0]); 
			if (!waterTree(findTreeToWater())) {
				if (!Util.tryMove(rc, rc.getLocation().directionTo(trees[0].location))) {
					//plantTree(Util.randomDirection());
					Util.tryMove(rc, Util.randomDirection());
				}
			}
		} else if (!plantTree(TeamComms.getArchonLoc(rc).directionTo(TeamComms.getOppArchonLoc(rc)))){
			Util.tryMove(rc, Util.randomDirection());
		}
		
		System.out.println("Bout to deploy: " + enemyRobots.length);
		// Deploy military units if possible 
		if (enemyRobots.length > 0) {
			deployRobot(RobotType.SOLDIER);
		} else if (TeamComms.getLumberjacks(rc) < 10) {
			deployRobot(RobotType.LUMBERJACK);
		} else if (TeamComms.getSoldiers(rc) < 20) {
			deployRobot(RobotType.SOLDIER);
		} else if (TeamComms.getTanks(rc) < 3) {
			deployRobot(RobotType.TANK);
		} 
	}
	
	// Returns an array of trees in robots sight. 
	public TreeInfo[] findTreesInSight() {
		return rc.senseNearbyTrees();
	}
	
	// Water given tree
	public boolean waterTree(TreeInfo tree) throws GameActionException {
		// Attemp to water tree
		if (rc.canWater(tree.ID)) {
				rc.water(tree.ID);
				System.out.println("Watered tree");
				return true;
		} else {
			return false;
		}
	}
	
	// Plants a tree behind the robot
	public boolean plantTree(Direction dir) throws GameActionException {
		if (rc.canPlantTree(dir)) {
			rc.plantTree(dir);
			return true; 
		}
		else
		{
			System.out.println("could not plant tree");
			return false; 
		}
	}
	
	public boolean planTreeOnBullet() throws GameActionException {
		BulletInfo[] bullets = RobotPlayer.rc.senseNearbyBullets();
		for(int i = 0; i < bullets.length; i++) 
		{
			if(MilitaryUtil.willHitMe(bullets[i])) 
			{
				plantTree(rc.getLocation().directionTo(bullets[i].location));
				return true;
			}
		}
		return false; 
	}
	
	public void tryPlantFarm() throws GameActionException {
		int num = 0; 
		Direction dir = Direction.getNorth();
		System.out.println("Here");
		
		if (foundLand(3)) {
			while (num < 4) {
				System.out.println(dir);
				if (plantTree(dir)) {
					System.out.println(num + ". Planted Tree");
					num++;
				} else {
					TreeInfo tree = findTreeToWater();
					if (tree != null) {
						waterTree(findTreeToWater());
					}
				}
				
				dir = dir.rotateRightDegrees(72);
			

				Clock.yield();
			}
		} else {
			Clock.yield();
			tryPlantFarm(); 
		}
		
		System.out.println("Top");
		for (int i = 0; i < 5; i++) {
			if (rc.isLocationOccupiedByTree(rc.getLocation().add(dir, 2))) {
				System.out.println("Occupied By tree");
				dir = dir.rotateRightDegrees(72);
			} else {
				System.out.println("Not Occupied By tree");
				break;
			}
		}
		
		mem.setDirectionToDeploy(dir);
		mem.setStrat(1);
	}
	
	public boolean foundLand(int radius) throws GameActionException {
		if (rc.senseNearbyRobots(radius).length == 0 && rc.senseNearbyTrees(radius).length == 0 && !Util.isWithinDistanceToSide(rc, radius)) {
			return true;
		} 
			
		Util.tryMove(rc, Util.randomDirection());
		return false; 
		
	}
	
	// Shakes the given tree
	public boolean shakeTree(TreeInfo tree) throws GameActionException {
		// Attempt to shake given tree
		if (rc.canShake(tree.ID)) {
				rc.shake(tree.ID);
				System.out.println("Shook tree");
				return true; 
		} else {
			return false; 
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
	
	// Finds lowest tree to shake
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
	
	// The watering path for a gardener 
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
	
	// The shake path for a gardener 
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
	public boolean deployRobot(RobotType type) {
		Direction dir = mem.getDirectionToDeploy();
		if (dir == null) {
			dir = Util.randomDirection();
		}
		
		if (rc.canBuildRobot(type, dir))
		{
			try {
				rc.buildRobot(type, dir);
				incrementCount(type);
				return true; 
			} 
			catch (GameActionException e)
			{
				// ERROR: deployment failed
				System.out.println("ERROR: buildRobot failed!");
				e.printStackTrace();
			}
		}
		else 
		{
		   // Robot cannot build this robot
		   System.out.println("I cannot deploy this robot! - Gardener");
		}
		
		return false; 
	}
	
	public boolean deployInitialLumberjack() throws GameActionException {
		Direction dir = Util.randomDirection();
		
		if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
			rc.buildRobot(RobotType.LUMBERJACK, dir);
			incrementCount(RobotType.LUMBERJACK);
			mem.setStrat(2);
			return true; 
		}
		return false; 
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
}
