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
	
	void logic() {
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
		if (!Util.tryMove(rc, mem.getMyDirection())) {
			mem.setDirection(mem.myDir.rotateLeftDegrees(72)); 
			Util.tryMove(rc, mem.getMyDirection());
		}
	}
	
	void executeAction() throws GameActionException {
		TreeInfo tree = mem.trees[0];
		
		if (rc.canShake(tree.ID)) {
			rc.shake(tree.ID);
		}
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
	
	void tryShake()
	{
		// if on a tree, attempt to shake
		if (rc.canInteractWithTree(rc.getLocation()) && rc.canShake(rc.getLocation()))
		{
			try {
				rc.shake(rc.getLocation());
			} catch (GameActionException e) {
				System.out.println("Scout raised an exception while attmepting to shake tree!");;
				e.printStackTrace();
			}
		}
	}
}
