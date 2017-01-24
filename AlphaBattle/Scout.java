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
    			
    			MilitaryUtil.dodge();
    			
    			// Attack If Need Be
    			if(enemiesNearby())
    			{
    				fireAtFirstEnemy();
    			}
    			else if(!rc.hasMoved())
    			{

        			// Explore instead
        			Util.tryMove(rc, Util.randomDirection());
    			}
    			
    		}
    		catch (Exception e)
    		{
    			System.out.println("Scout Exception");
    			e.printStackTrace();
    		}
    		Clock.yield();
    	}
	}
	
	int initScout()
	{
		try
		{
			int num = TeamComms.getScouts(rc);
			System.out.println("I am Scout Number " + num);
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
		return mem.recentEnemies.length > 0;
	}
	
	// Attempts to fire at the first enemy seen this turn, 
	void fireAtFirstEnemy()
	{
		try
		{
			if (rc.canFireSingleShot()) {
	            // ...Then fire a bullet in the direction of the enemy.
	            rc.fireSingleShot(rc.getLocation().directionTo(mem.recentEnemies[0].location));
	        }
			if (!rc.hasAttacked())
			{
				// Try to follow the enemy if attacking couldn't happen
				Util.tryMove(rc, followEnemy(mem.recentEnemies[0], 8.5f));
			}
		} 
		catch (Exception e)
		{
			System.out.println("Scout Raised Exception while Firing");
			e.printStackTrace();
		}
	}
}
