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
		MapLocation loc = TeamComms.getOppArchonLoc();
		avoidFriendlyScout();
		if (loc != null) {
			Util.tryMove(rc, rc.getLocation().directionTo(loc));
		} else if (!Util.tryMove(rc, mem.getMyDirection())) {
			mem.setDirection(Util.randomDirection()); 
			Util.tryMove(rc, mem.getMyDirection());
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
	
	void harassFromCover()
	{
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		// If enemies nearby & on a tree, move toward enemy
		try {
			if (rc.isLocationOccupiedByTree(rc.getLocation()) && enemies.length > 0)
			{
				if (enemies[0].getType() == RobotType.GARDENER || enemies[0].getType() == RobotType.ARCHON)
				{
					//Move to the middle of tree
					mem.setDirection(Util.getDirectionToLocation(rc, rc.getLocation()));
					Util.tryMove(rc, mem.getMyDirection());
					//While there are still enemies and the path is clear, shoot at them from cover!
					while (enemies.length > 0 && MilitaryUtil.noFriendlyFire(rc, Util.getDirectionToLocation(rc, enemies[0].getLocation())))
					{
					   MilitaryUtil.shootEnemy(rc, 0, enemies[0].ID);
					   enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
					   Clock.yield();
					}
					//No more enemies, continue to scout the map.
					mem.setDirection(Util.randomDirection());
					Util.tryMove(rc, mem.getMyDirection());
				}
			}
		} catch (GameActionException e) {
			System.out.println("Error in Scout harassFromCover!");
			e.printStackTrace();
		}
	}
	
	void returnToCover(MapLocation tree_loc)
	{
		BulletInfo[] bullets = RobotPlayer.rc.senseNearbyBullets();
		
		if (bullets.length > 0)
		{
			mem.setDirection(Util.getDirectionToLocation(rc, tree_loc));
		}
	}
	
	void avoidFriendlyScout()
	{
	   RobotInfo[] robots = rc.senseNearbyRobots(3, rc.getTeam()); 	
	   
	   for (int i=0; i<robots.length; i++)
	   {
		   // If scout comes in contact with a friendly scout, choose a new random direction
		   if (robots[i].type == RobotType.SCOUT)
		   {
			   mem.setDirection(Util.randomDirection());
		   }
	   }
	}
	
	// Attempts to fire at the first enemy seen this turn, 
	void fireAtFirstEnemy()
	{
		try
		{
			if (rc.canFireSingleShot()) {
	            // ...Then fire a bullet in the direction of the enemy.
	            rc.fireSingleShot(rc.getLocation().directionTo(mem.enemiesInView[0].location));
	        }
			if (!rc.hasAttacked())
			{
				// Try to follow the enemy if attacking couldn't happen
				Util.tryMove(rc, followEnemy(mem.enemiesInView[0], 8.5f));
			}
		} 
		catch (Exception e)
		{
			System.out.println("Scout Raised Exception while Firing");
			e.printStackTrace();
		}
	}
}
