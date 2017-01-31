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
	           	logic(mem.getStrat());
	           	mem.updateMemory();
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
	
	// The normal strategy
	public void normalStrat() throws GameActionException
	{	
		
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
			System.out.println("Random: " + random);
			if (random < 3) {
				deployRobot(RobotType.SOLDIER);
			} else if (random < 7) {
				deployRobot(RobotType.SCOUT);
			} else {
				deployRobot(RobotType.LUMBERJACK);
			}
		}
		
		
		
		/*
		
		// Deploy military units if possible
		if (mem.enemiesInView.length > 0) {
			deployRobot(RobotType.SOLDIER);
		}
				
		if (TeamComms.getScouts() < 10 && mem.birthCount % 10 == 0) {
			deployRobot(RobotType.SCOUT);
		} else if (TeamComms.getLumberjacks() < 10 && mem.birthCount % 5 == 0) {
			deployRobot(RobotType.LUMBERJACK);
		} else if (TeamComms.getSoldiers() < 20) {
			deployRobot(RobotType.SOLDIER);
		} else if (TeamComms.getTanks() < 3) {
			// deployRobot(RobotType.TANK);
		}
		
		*/
	}
		
	public void tryPlantFarm() throws GameActionException 
	{
		Direction toEnemyArchon = TeamComms.getDirectionToInitialArchonLoc();
		Direction dir = toEnemyArchon.rotateRightDegrees(72); 
		int num = 0; 
		
		mem.setDirectionToDeploy(toEnemyArchon);
		
		System.out.println(toEnemyArchon.toString());
		rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(toEnemyArchon), 222, 0, 0);
		
		if (foundLand(3))
		{
			while (num < 4)
			{
				rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(toEnemyArchon), 222, 0, 0);
				rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(dir), 0, 0, 222);

				System.out.println("Degrees: " + dir.getAngleDegrees() + " = " + toEnemyArchon.getAngleDegrees() + " !");
				if ((int)dir.getAngleDegrees() != (int)toEnemyArchon.getAngleDegrees()) {
					if (plantTree(dir)) {
						num++;
					} 
					
					waterTree();
				} else {
					System.out.println("Equaled the enemy loc");
				}
				
				
				dir = dir.rotateRightDegrees(72);
				Clock.yield();
			}
		} 
		else 
		{
			deployRobotLogic();
			Clock.yield();
			tryPlantFarm(); 
		}
		
		mem.setStrat(1);
	}
	
	public boolean foundLand(int radius) throws GameActionException
	{
		RobotInfo[] team = rc.senseNearbyRobots(radius, rc.getTeam());
		TreeInfo[] trees = rc.senseNearbyTrees(radius);
		int turn = TeamComms.getTurnCount();
		
		System.out.println("Turn: " + turn);
		
		if (team.length == 0 && trees.length == 0)
		{
			return true;
		} 

		if (turn % 5 == 0) {
			if (!Util.tryMove(rc, TeamComms.getDirectionToInitialArchonLoc())) {
				Util.tryMove(rc, Util.randomDirection());
			}
		}
	
		Util.tryMove(rc, Util.randomDirection());
		
		System.out.println("Have not found land");
			
		return false; 
		
	}
	
	
	// Returns an array of trees in robots sight. 
	public TreeInfo[] findTreesInSight()
	{
		return rc.senseNearbyTrees();
	}
	
	// Water given tree
	public boolean waterTree() throws GameActionException
	{
		TreeInfo tree = findTreeToWater();
	
		System.out.println("Attempts to water");
		// Attemp to water tree
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
	
	// Plants a tree behind the robot
	public boolean plantTree(Direction dir) throws GameActionException
	{
		if (rc.canPlantTree(dir))
		{
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
		TreeInfo[] treeInfo = findTreesInSight();
		
		if (treeInfo.length == 0) 
		{
			return null; 
		} 
		
		TreeInfo lowestInWater = treeInfo[0];
		
		for (int i = 0; i < treeInfo.length; i++) 
		{
			if (treeInfo[i].health < lowestInWater.health) 
			{
				lowestInWater = treeInfo[i];
			}
		}
		
		return lowestInWater; 
				
		
	} 
	
	// Finds lowest tree to shake
	TreeInfo findTreeToShake() 
	{
		TreeInfo[] treeInfo = findTreesInSight();
		
		if (treeInfo.length == 0) 
		{
			return null; 
		} 
		
		TreeInfo highestInBullets = treeInfo[0];
		
		for (int i = 0; i < treeInfo.length; i++)
		{
			if (treeInfo[i].getContainedBullets() > highestInBullets.getContainedBullets())
			{
				highestInBullets = treeInfo[i];
			}
		}
		
		return highestInBullets; 
				
		
	}
	
	public boolean deployInitialLumberjack() throws GameActionException 
	{
		int tries = 0;
		
		while (tries++ < 5) {
			Direction dir = Util.randomDirection();
		
			if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) 
			{
				rc.buildRobot(RobotType.LUMBERJACK, dir);
				incrementCount(RobotType.LUMBERJACK);
				mem.setStrat(2);
				return true; 
			} 
			Clock.yield();
		}
		mem.setStrat(2);
		return false; 
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
		   System.out.println("I cannot deploy this robot! - Gardener");
		}
		
		return false; 
	}
}
