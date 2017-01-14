package AlphaBattle;
import battlecode.common.*;

public class Archon {

	static RobotController rc;
	
	public Archon(RobotController rc)
	{
		Archon.rc = rc;
	}
	
	public void run()
	{
		// Variable Declarations
		Team enemy = rc.getTeam().opponent();
		int donationCount = 0; 
		
		try
		{
			initArchon(rc);
		} catch (Exception e)
		{
			System.out.println("Archon Init Exception");
			e.printStackTrace();
		}
		

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = Util.randomDirection();
                
                updateAreaOfInterest();

                // Attempt to deploy with a max number of gardeners
                if (TeamComms.getGardeners(rc) < 2)
                {
                	// This function builds a gardener if possible, and increments the unit count
                	deployGardener(dir);
                }
                else if (rc.getTeamBullets() >= 500 && donationCount < 200)
                {
                	rc.donate(50);
                	donationCount += 50;
                }
                
                // Look for enemies and move away from them
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                if (robots.length > 0)
                {
                	Util.tryMove(rc, (rc.getLocation().directionTo(robots[0].location)).opposite());
                }

                // Broadcast archon's location for other robots on the team to know
                TeamComms.broadcastArchonLoc(rc);
                
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
	
	void initArchon(RobotController rc) throws GameActionException
	{
		System.out.println("I'm an archonnn!");
		TeamComms.broadcastArchonLoc(rc);
		TeamComms.updateGardeners(rc, 0);
		TeamComms.updateSoldiers(rc, 0);
		TeamComms.updateLumberjacks(rc, 0);
		TeamComms.updateScouts(rc, 0);
		TeamComms.updateTanks(rc, 0);
		if (rc.getTeam() == Team.A) {
			broadcastEnemyArchon(Team.B); 
		} else { 
			broadcastEnemyArchon(Team.A); 
		}
		System.out.println("Afterewards");
		
		// Generate Initial Area of Interest to Explore
		MapLocation[] areasOfInterest = rc.getInitialArchonLocations(rc.getTeam().opponent());
		if(areasOfInterest.length > 0)
		{
			TeamComms.setAreaOfInterest(rc, areasOfInterest[0]);
		}
	}
	
	void broadcastEnemyArchon(Team enemy) throws GameActionException {
		MapLocation enemyArchon = rc.getInitialArchonLocations(enemy)[0]; 
		TeamComms.broadcastOppArchon(rc, enemyArchon);
		System.out.println(enemyArchon.toString());
	}
	
	void deployGardener(Direction dir)
	{
		try
		{
			// Attempt to hire a gardener
			if (rc.canHireGardener(dir))
			{
				rc.hireGardener(dir);

				// Increment the gardener count
				incrementGardenerCount();
			}
			
		} 
		catch (Exception e)
		{
			// ERROR: incrementGardenerCount in deployGardener
			System.out.println("ERROR: incrementGardenerCount in deployGardener");
			e.printStackTrace();
		}
	}
	
	void incrementGardenerCount() throws GameActionException
	{
		int old = TeamComms.getGardeners(rc);
		TeamComms.updateGardeners(rc, old + 1);
	}
	
	void updateAreaOfInterest() throws GameActionException
	{
		if(rc.getRoundNum() - TeamComms.getLastArchonSighting(rc) < 5)
		{
			MapLocation l = TeamComms.getOppArchonLoc(rc);
			TeamComms.setAreaOfInterest(rc, l);
		}
	}
}
