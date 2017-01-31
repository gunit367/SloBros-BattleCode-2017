package AlphaBattle;

import battlecode.common.*;


public class Gardener extends RobotPlayer {
	RobotController rc;
	GardenerMemory mem;
	

	public Gardener(RobotController rc) throws GameActionException
	{
		this.rc = rc; 
		mem = new GardenerMemory(rc);		
		mem.setStrat(3);
	}
	
	// Gardener run method
	public void run() throws GameActionException
	{
		while (true)
		{
            try
            {
            	mem.updateMemory();
	           	logic(mem.getStrat());
	           	Clock.yield();
            } 
            catch (Exception e)
            {
            	System.out.println("Gardener Exception");
	            e.printStackTrace();
	        }
		}
	}
	
	// The Logic for a given turn
	public void logic(int strat) throws GameActionException {
		super.logic();
		
		switch (strat) {
		case 1: 
			maintainFarm(); 
			break;
		case 2: 
			tryPlantFarm();
			break;
		case 3: 
			deployInitialLumberjack();
			break;
		}
	}
	
	// The normal strategy
	public void maintainFarm() throws GameActionException
	{	
		// TODO: Check to plant more trees!
		waterTree();
		deployRobotLogic();
	}
	
	public void deployRobotLogic() throws GameActionException {
		if (rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 10) {
			deployRobot(RobotType.LUMBERJACK);
		} else if (mem.enemiesInView.length > 0) {
			deployRobot(RobotType.SOLDIER);
		} else {
			int random = (int) (Math.random() * 11);
			
			if (random < 3) {
				deployRobot(RobotType.SOLDIER);
			} else if (random < 7) {
				deployRobot(RobotType.SCOUT);
			} else {
				deployRobot(RobotType.LUMBERJACK);
			}
		}
	}
		
	
	
	TreeInfo getTreeInfo(Direction dir)
	{
		float dist = RobotType.GARDENER.bodyRadius + GameConstants.BULLET_TREE_RADIUS;
		MapLocation center = rc.getLocation().add(dir, dist);
		TreeInfo[] nearby = rc.senseNearbyTrees(center, GameConstants.BULLET_TREE_RADIUS, rc.getTeam());
		if(nearby.length > 0)
			return nearby[0];
		return null;
	}
	
	public void fillFarm() throws GameActionException
	{
		// Check all directions for trees, ignore direction for spawning
		for(int i = 0; i < mem.farmDirections.length - 1; i++)
		{
			Direction treeDir = mem.farmDirections[i];
			if(mem.farm[i] == null)
			{
				// if there's no tree in this direction, try and plant one!
				if(plantTree(treeDir))
				{
					mem.farm[i] = getTreeInfo(treeDir);
					if(mem.farm[i] != null)
					{
						System.out.println("Found my tree!");
						mem.treeCount += 1;
					}
				}
			}
		}
	}
	
	Direction getExploreDirection() throws GameActionException
	{
		Direction toExplorationLocation = rc.getLocation().directionTo(mem.explorationLocation);
		if(mem.canSeeAlly(RobotType.GARDENER))
		{
			// move away from nearby gardener
			Direction dir = mem.findInView(RobotType.GARDENER).location.directionTo(rc.getLocation());
			float degrees = dir.degreesBetween(toExplorationLocation);
			return dir.rotateRightDegrees(degrees / 2);
		}
		
		return toExplorationLocation;
	}
	
	void constructFarm() throws GameActionException
	{
		// Construct Farm
		if(mem.treeCount < mem.farmDirections.length - 1)
		{
			// Not enough trees, try and plant one
			fillFarm();
		}
		else
		{
			// Farm is complete, Update to next phase strat!
			mem.setStrat(1);
		}
	}
	
	void manifestDestiny() throws GameActionException
	{
		// Expand Outward to find better land
		// Calculate Move
		Util.tryMove(rc, getExploreDirection());
		
		// Update to see it this place is chill
		mem.foundFarmland = foundLand((float)mem.FARM_RADIUS);
		if(mem.foundFarmland)
		{
			// Just found a suitable place!
			mem.newFarm();
		}
	}
	
	public void tryPlantFarm() throws GameActionException 
	{
		Direction toEnemyArchon = TeamComms.recentArchonDirection();
		
		mem.setDirectionToDeploy(toEnemyArchon);
		
		rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(toEnemyArchon), 222, 0, 0);
		
		// Check to see if we've found suitable farm land
		if(mem.foundFarmland)
		{
			constructFarm();
		}
		else
		{
			// Expand, Explore, find better land
			manifestDestiny();
		}
		
		
		// OLD CODE STARTS HERE
//		if (mem.foundFarmland)
//		{
//			// Construct Trees on the farm
//			while (mem.treeCount < 4)
//			{
//				rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(toEnemyArchon), 222, 0, 0);
//				rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(dir), 0, 0, 222);
//
//				System.out.println("Degrees: " + dir.getAngleDegrees() + " = " + toEnemyArchon.getAngleDegrees() + " !");
//				if ((int)dir.getAngleDegrees() != (int)toEnemyArchon.getAngleDegrees()) {
//					if (plantTree(dir)) {
//						mem.treeCount++;
//					} 
//					
//					waterTree();
//				} else {
//					System.out.println("Equaled the enemy loc");
//				}
//				
//				
//				dir = dir.rotateRightDegrees(72);
//				Clock.yield();
//			}
//		} 
//		else 
//		{
//			deployRobotLogic();
//			Clock.yield();
//			tryPlantFarm(); 
//		}
//		
//		mem.setStrat(1);
	}
	
	public boolean foundLand(float radius) throws GameActionException
	{
		TreeInfo[] trees = rc.senseNearbyTrees(radius, rc.getTeam());
		
		if (trees.length < 2 && !mem.canSeeAllyArchon(mem.FARM_RADIUS) && rc.onTheMap(rc.getLocation(), radius))
		{
			return true;
		}
			
		return false; 
	}
	
	
	
	// Water given tree
	public boolean waterTree() throws GameActionException
	{
		TreeInfo tree = findTreeToWater();
	
		// Attempt to water tree
		if (tree != null && rc.canWater(tree.ID)) 
		{
			System.out.println("Watered");
				rc.water(tree.ID);
				return true;
		}
		else
		{
			return false;
		}
	}
	
	// Plants a tree in given direction
	public boolean plantTree(Direction dir) throws GameActionException
	{
		if (rc.canPlantTree(dir))
		{
			System.out.println("Planted Tree!");
			rc.plantTree(dir);
			return true; 
		}
		else
		{
			return false; 
		}
	}
	
	public boolean planTreeOnBullet() throws GameActionException
	{
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

	TreeInfo findTreeToWater()
	{	
		if (mem.trees.length == 0) 
		{
			return null; 
		} 
		
		TreeInfo lowestInWater = mem.trees[0];
		
		for (int i = 0; i < mem.trees.length; i++) 
		{
			if (mem.trees[i].health < lowestInWater.health) 
			{
				lowestInWater = mem.trees[i];
			}
		}
		
		return lowestInWater; 
				
		
	} 
	
	// Finds lowest tree to shake
	TreeInfo findTreeToShake() 
	{	
		if (mem.trees.length == 0) 
		{
			return null; 
		} 
		
		TreeInfo highestInBullets = mem.trees[0];
		
		for (int i = 0; i < mem.trees.length; i++)
		{
			if (mem.trees[i].getContainedBullets() > highestInBullets.getContainedBullets())
			{
				highestInBullets = mem.trees[i];
			}
		}
		
		return highestInBullets; 
				
		
	}
	
	public boolean deployInitialLumberjack() throws GameActionException 
	{
		// Calculate Move - away from archon
		Direction moveDir = TeamComms.getArchonLoc().directionTo(rc.getLocation());
		Util.tryMove(rc, moveDir);
		
		
		// Try to Spawn
		Direction randDir = Util.randomDirection();
		if(rc.canBuildRobot(RobotType.LUMBERJACK, moveDir))
		{
			rc.buildRobot(RobotType.LUMBERJACK, moveDir);
			incrementCount(RobotType.LUMBERJACK);
			mem.setStrat(2);
			return true;
		}
		else if (rc.canBuildRobot(RobotType.LUMBERJACK, moveDir.opposite()))
		{
			rc.buildRobot(RobotType.LUMBERJACK, moveDir.opposite());
			incrementCount(RobotType.LUMBERJACK);
			mem.setStrat(2);
			return true;
		}
		else if (rc.canBuildRobot(RobotType.LUMBERJACK, randDir))
		{
			rc.buildRobot(RobotType.LUMBERJACK, randDir);
			incrementCount(RobotType.LUMBERJACK);
			mem.setStrat(2);
			return true;
		}
		return false;
		
//		OLD CODE
//		int tries = 0;
//		
//		while (tries++ < 5) {
//			Direction dir = Util.randomDirection();
//		
//			if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) 
//			{
//				rc.buildRobot(RobotType.LUMBERJACK, dir);
//				incrementCount(RobotType.LUMBERJACK);
//				mem.setStrat(2);
//				return true; 
//			} 
//			Clock.yield();
//		}
//		mem.setStrat(2);
//		return false; 
	}
	
	// Deploys a robot given 
	public boolean deployRobot(RobotType type)
	{
		Direction dir = mem.getDirectionToDeploy();
		if (dir == null)
		{
			dir = Util.randomDirection();
		}
		
		if (rc.canBuildRobot(type, dir))
		{
			try
			{
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
		}
		
		return false; 
	}
}
