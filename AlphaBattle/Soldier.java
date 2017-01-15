package AlphaBattle;
import battlecode.common.*;

public class Soldier extends RobotPlayer {
	RobotController rc;
	SoldierMemory mem;
	
	public Soldier(RobotController rc)
	{
		this.rc = rc;
		mem = new SoldierMemory(rc);
	}
	
	public void run()
	{
		//helper functions: isEnemyClose(), getEnemyLocation(), shoot(location), 
		System.out.println("I'm an soldier!");
	    Team enemy = rc.getTeam().opponent();
        RobotInfo archon = null; 
        MapLocation archonLoc = null; 

	    // The code you want your robot to perform every round should be in this loop
	    while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                //MapLocation myLocation = rc.getLocation();

            	// Check for nearby Robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                
                // Fetch Current Area of Interest
                MapLocation aoi = TeamComms.getAreaOfInterest(rc);
                Direction dir = rc.getLocation().directionTo(aoi);
                
                if (archonLoc != null && rc.canSenseLocation(archonLoc)) {
                	archon = rc.senseRobotAtLocation(archonLoc);
                }
                
                for (int i = 0; i < robots.length; i++) {
                	if (robots[i].type == RobotType.ARCHON) {
                		archon = robots[i];
                		archonLoc = archon.location;
                		TeamComms.broadcastOppArchon(rc, archonLoc);
                	}
                }
                
                
                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                    Util.tryMove(rc, rc.getLocation().directionTo(robots[0].location));
                }
                else
                {
                    Util.tryMove(rc, dir);
                }
                
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
	}
}
