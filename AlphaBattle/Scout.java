package AlphaBattle;
import battlecode.common.*;

public class Scout extends RobotPlayer {

	RobotController rc;
	ScoutMemory mem;
	
	public Scout(RobotController rc)
	{
		this.rc = rc;
		mem = new ScoutMemory(rc);
	}
	
	public void run()
	{
		initScout();
		
		
		while (true)
    	{
    		try
    		{
    			// Update Robots Memory with Information it can sense
    			mem.updateMemory();
    			logic();
    		}
    		catch (Exception e)
    		{
    			System.out.println("Scout Exception");
    			e.printStackTrace();
    		}
    		Clock.yield();
    	}
	}
	
	public void logic() throws GameActionException {
		try {
			mem.updateMemory();
			executeMove(); 
			executeAction(); 
			updateAreaOfInterest();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void executeMove() throws GameActionException {
		//MapLocation loc = TeamComms.getOppArchonLoc();
		//avoidFriendlyScout();
		//if (loc != null) {
			//Util.tryMove(rc, rc.getLocation().directionTo(loc));
		findEnemyTree();
	    if (!Util.tryMove(mem.getMyDirection())) {
	    	System.out.println("Setting direction to random!!!");
			mem.setDirection(Util.randomDirection()); 
		}
	}
	
	void executeAction() throws GameActionException {
		tryShake();
		harassFromCover();
	}
	
	int initScout()
	{
		try
		{
			int num = TeamComms.getScouts();
			mem.setDirection(rc.getLocation().directionTo(TeamComms.getOppArchonLoc()));
			return num;
		}
		catch (Exception e)
		{
			System.out.println("Error In Scout Init");
			return -1;
		}
		
	}
	
	boolean enemiesNearby()
	{
		return mem.enemiesInView.length > 0;
	}
	
	void findEnemyTree() throws GameActionException
	{
		TreeInfo[] enemyTrees = rc.senseNearbyTrees(RobotType.SCOUT.sensorRadius-1.0f, rc.getTeam().opponent());
		RobotInfo robot = null;
		int i;
		
		if (enemyTrees.length > 0)
		{
			for (i=0; i<enemyTrees.length; i++)
			{
				robot = rc.senseRobotAtLocation(enemyTrees[i].getLocation());
				//If there is no robot at tree's location
				if (robot == null || robot.getID() == rc.getID())
				{
					break;
				}
				else
				{
					rc.setIndicatorLine(rc.getLocation(), robot.getLocation(), 0, 100, 100);
				}
			}
			mem.setDirection(Util.getDirectionToLocation(rc, enemyTrees[i].getLocation()));
			if (i == enemyTrees.length)
			{
				mem.setDirection(Util.randomDirection());
			}
		}	
	}
	
	void harassFromCover() throws GameActionException
	{
		RobotInfo[] enemies = mem.enemiesInView;
		TreeInfo tree = rc.senseTreeAtLocation(rc.getLocation());
		// If enemies nearby & on a tree, move toward enemy
		try {
			if (tree != null && tree.getTeam() == rc.getTeam().opponent() && enemies.length > 0) {
				RobotType type = enemies[0].getType();
				
				if (type == RobotType.GARDENER || type == RobotType.ARCHON || type == RobotType.SCOUT && rc.getTeamBullets() > 50)
				{	
					// If too far, attempt to follow the enemy
					//if (rc.getLocation().distanceTo(enemies[0].getLocation()) >= 1)
					//{
					//	System.out.println("HERE");
					//	Util.tryMove(enemies[0].getLocation().add(enemies[0].getLocation().directionTo(rc.getLocation()), 0.5f), 1.0f);
					//}
					
					//System.out.println("Distance To: " + rc.getLocation().distanceTo(enemies[0].getLocation()));
					
					//While there are still enemies and the path is clear, shoot at them from cover!
					while (enemies.length > 0)
					{
					   MilitaryUtil.shootEnemy(rc, 3, enemies[0].ID);
					   enemies = rc.senseNearbyRobots(1, rc.getTeam().opponent());
					   Clock.yield();
					}
					
				}
			}
		} catch (GameActionException e) {
			System.out.println("Error in Scout harassFromCover!");
			e.printStackTrace();
		}
	}
}
