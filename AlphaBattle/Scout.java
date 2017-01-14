package AlphaBattle;
import battlecode.common.*;

public class Scout {

	RobotController rc;
	ScoutMemory mem;
	
	public Scout(RobotController rc)
	{
		this.rc = rc;
		mem = new ScoutMemory(rc);
	}
	
	public void run()
	{
		//int scoutNum;
		Team enemy = rc.getTeam().opponent();
		initScout();
		
		
		while (true)
    	{
    		try
    		{
    			// Look around and Record Important Information
    			RobotInfo[] enemies = rc.senseNearbyRobots(-1, enemy);
    			
    			// Attack If Need Be
    			if(enemies.length > 0)
    			{
    				if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(enemies[0].location));
                    }
    				if (!rc.hasAttacked())
    				{
    					Util.tryMove(rc, followEnemy(enemies[0]));
    				}
    			}
    			else
    			{

        			// Move Phase
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
	
	Direction followEnemy(RobotInfo enemy)
	{
		Direction dir = enemy.location.directionTo(rc.getLocation());
		MapLocation toLoc = enemy.location.add(dir, 8.5f);
		return rc.getLocation().directionTo(toLoc);
	}
}
