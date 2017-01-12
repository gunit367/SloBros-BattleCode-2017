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
		System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = Util.randomDirection();

                // Attempt to build a gardener in this direction
                if (rc.canHireGardener(dir)) {
                    rc.hireGardener(dir);
                }

                // Broadcast archon's location for other robots on the team to know
                MapLocation myLocation = rc.getLocation();
                rc.broadcast(0,(int)myLocation.x);
                rc.broadcast(1,(int)myLocation.y);

                System.out.println("BULLET COUNT: " + rc.getTeamBullets());
                if (rc.getTeamBullets() > 200.0) {
                	rc.donate(20);
                }
                
                Util.tryMove(dir);
                
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
}
