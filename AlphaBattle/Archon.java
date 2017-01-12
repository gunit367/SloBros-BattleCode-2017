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
		
		System.out.println("I'm an archon!");
		

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = Util.randomDirection();

                // Attempt to build a gardener in this direction, with a max number of gardeners
                if (rc.canHireGardener(dir) && farmerCount < 3)
                {
                	System.out.println("Hiring Gardener");
                    rc.hireGardener(dir);
                    farmerCount += 1;
                }
                else if (farmerCount >= 3 && rc.getTeamBullets() >= 50 && donationCount < 200)
                {
                	rc.donate(50);
                	donationCount += 50;
                	if(donationCount >= 200)
                	{
                		farmerCount = 0;
                		donationCount = 0;
                	}
                }
                
                // Look for enemies and move away from them
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                if (robots.length > 0)
                {
                	Util.tryMove(rc, (rc.getLocation().directionTo(robots[0].location)).opposite());
                }

                // Broadcast archon's location for other robots on the team to know
                TeamComms.broadcastArchonLoc(rc, rc.getLocation());
                
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
}
