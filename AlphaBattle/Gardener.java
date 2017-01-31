package AlphaBattle;

import battlecode.common.*;


public class Gardener extends RobotPlayer {
	RobotController rc;
	GardenerMemory mem;
	
	public final float LARGE_MAP_DIST = 50;

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
			if (mem.trees.length > 5 || (mem.archonLocation.distanceTo(TeamComms.getInitialArchonLocation()) < LARGE_MAP_DIST))
			{
				deployInitialUnit(RobotType.LUMBERJACK);
			}
			else
			{
				deployInitialUnit(RobotType.SOLDIER);
			}
			break;
		}
	}
	
	// The normal strategy
	public void maintainFarm() throws GameActionException
	{	
		fillFarm();
		waterTree();
		deployRobotLogic();
	}
	
	public void deployRobotLogic() throws GameActionException 
	{
		if (rc.senseNearbyTrees(-1, Team.NEUTRAL).length > 10 || rc.senseNearbyTrees(mem.FARM_RADIUS, Team.NEUTRAL).length > 0)
		{
			deployRobot(RobotType.LUMBERJACK);
		} 
		else if (mem.enemiesInView.length > 0) 
		{
			deployRobot(RobotType.SOLDIER);
		} else {
			int random = (int) (Math.random() * 12);
			
			if (random < 4)
			{
				deployRobot(RobotType.SOLDIER);
			} 
			else if (random < 8)
			{
				deployRobot(RobotType.SCOUT);
			} 
			else
			{
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

	Direction awayFromEdge()
	{
		MapLocation loc = rc.getLocation();
		if (loc.x - mem.mapMinX < 1)
		{
			mem.explorationLocation = rc.getLocation().add(Direction.getEast());
			return Direction.getEast();
		}
		else if (loc.y - mem.mapMinY < 1)
		{
			mem.explorationLocation = rc.getLocation().add(Direction.getNorth());
			return Direction.getNorth();
		}
		else if (mem.mapMaxX - loc.x < 1)
		{
			mem.explorationLocation = rc.getLocation().add(Direction.getWest());
			return Direction.getWest();
		}
		else
		{
			mem.explorationLocation = rc.getLocation().add(Direction.getSouth());
			return Direction.getSouth();
		}
	}
	
	Direction getExploreDirection() throws GameActionException
	{
		// Try to move away from edges of the map.
		for(int i = 0; i < 4; i++)
		{
			if(!(rc.onTheMap(rc.getLocation().add(Direction.getNorth().rotateLeftDegrees(90 * i)))))
			{
				return Direction.getSouth().rotateLeftDegrees(90 * i);
			}
		}
		
		if (mem.exploreDirection != null)
		{
			return mem.exploreDirection;
		}
		
		return mem.archonLocation.directionTo(rc.getLocation());
	}
	
	void constructFarm() throws GameActionException
	{
		// Construct Farm
		waterTree();
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
		if (!Util.tryMove(getExploreDirection()))
		{
			mem.updateExplorationLocation();
		}
		
		// Update to see it this place is chill
		mem.foundFarmland = foundLand((float)mem.FARM_RADIUS);
		if(mem.foundFarmland)
		{
			// Just found a suitable place!
			mem.newFarm();
		}
		
		// Spawn a Lumberjack
		attemptDeploy(Util.randomDirection(), RobotType.LUMBERJACK);
	}
	
	public void tryPlantFarm() throws GameActionException 
	{
		Direction toEnemyArchon = TeamComms.recentArchonDirection();
		
		mem.setDirectionToDeploy(toEnemyArchon);
		
		rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(toEnemyArchon, 4), 222, 0, 0);
		
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
	}
	
	public boolean foundLand(float radius) throws GameActionException
	{
		TreeInfo[] trees = rc.senseNearbyTrees(radius, rc.getTeam());
		
		if (trees.length == 0 && rc.getLocation().distanceTo(mem.archonLocation) > mem.FARM_RADIUS && rc.onTheMap(rc.getLocation(), radius))
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
	
	boolean attemptDeploy(Direction d, RobotType t) throws GameActionException
	{
		if(rc.canBuildRobot(t, d))
		{
			rc.buildRobot(t, d);
			incrementCount(t);
			return true;
		}
		return false;
	}
	
	public boolean deployInitialUnit(RobotType t) throws GameActionException 
	{
		// Calculate Move - away from archon
		Direction moveDir = TeamComms.getArchonLoc().directionTo(rc.getLocation());
		Util.tryMove(moveDir);
		if((!(rc.getTeamBullets() > t.bulletCost) && TeamComms.getCount(t) != 0) || rc.getRoundNum() > 30)
		{
			// We've already spawned the first lumberjack, skipping is ok
			mem.setStrat(2);
			return true;
		}
		
		// Try to Spawn
		Direction randDir = Util.randomDirection();
		if(attemptDeploy(moveDir, t))
		{
			mem.setStrat(2);
			return true;
		}
		else if (attemptDeploy(moveDir.opposite(), t))
		{
			mem.setStrat(2);
			return true;
		}
		else if (attemptDeploy(randDir, t))
		{
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
		Direction dir = mem.farmDirections[mem.farmDirections.length - 1];
		if (dir == null)
		{
			dir = Util.randomDirection();
		}
		
		try
		{
			return attemptDeploy(dir, type);
		}
		catch (GameActionException e)
		{
			System.out.println("Error Deploying");
			e.printStackTrace();
		}
		return false;
	}
}
