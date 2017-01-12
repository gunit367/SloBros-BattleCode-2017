package AlphaBattle;
import battlecode.common.*;

public class Soldier {
	static RobotController rc;
	
	public Soldier(RobotController rc)
	{
		Soldier.rc = rc;
	}
	
	public void run()
	{
		//helper functions: isEnemyClose(), getEnemyLocation(), shoot(location), 
		System.out.println("I'm an soldier!");
	    Team enemy = rc.getTeam().opponent();
        RobotInfo archon = null; 

	    // The code you want your robot to perform every round should be in this loop
	    while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                //MapLocation myLocation = rc.getLocation();

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
                
                
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                MapLocation archonLoc = new MapLocation(xPos,yPos);
                if (archonLoc != null && rc.canSenseLocation(archonLoc)) {
                	archon = rc.senseRobotAtLocation(archonLoc);
                }
                

                if (archon == null) {
                	for (int i = 0; i < robots.length; i++) {
                		if (robots[i].type == RobotType.ARCHON) {
                			archon = robots[i];
                			
                			rc.broadcast(0,(int)archon.location.x);
                            rc.broadcast(1,(int)archon.location.y);
                			
                		}
                	
                	}
                }
                
                
                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }

                if (archon != null) {
                	try {
                		rc.move(archon.location);
                	} catch (Exception e) {
                		
                	}
                } else {
                	// Move randomly
                    Util.tryMove(rc, Util.randomDirection());
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
