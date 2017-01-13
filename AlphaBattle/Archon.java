package AlphaBattle;
import battlecode.common.*;

public class Archon {

	static RobotController rc;
	
	public Archon(RobotController rc)
	{
		System.out.println("HERE");
		Archon.rc = rc;
	}
	
	public void run()
	{
		// Variable Declarations
		int farmerCount = 0;
		int donationCount = 0;
		Team enemy = rc.getTeam().opponent();
		
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

                // Attempt to build a gardener in this direction, with a max number of gardeners
                if (rc.canHireGardener(dir) && farmerCount < 10)
                {
                	System.out.println("Hiring Gardener");
                    rc.hireGardener(dir);
                    farmerCount += 1;
                    incrementGardenerCount();
                }
                else if (farmerCount >= 10 && rc.getTeamBullets() >= 50 && donationCount < 200)
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
		System.out.println("I'm an archon!");
		TeamComms.broadcastArchonLoc(rc);
		TeamComms.updateGardeners(rc, 0);
		TeamComms.updateSoldiers(rc, 0);
		TeamComms.updateLumberjacks(rc, 0);
		TeamComms.updateScouts(rc, 0);
		TeamComms.updateTanks(rc, 0);
		
		// Generate Initial Area of Interest to Explore
		MapLocation[] areasOfInterest = rc.getInitialArchonLocations(rc.getTeam().opponent());
		if(areasOfInterest.length > 0)
		{
			TeamComms.setAreaOfInterest(rc, areasOfInterest[0]);
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
