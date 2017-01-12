package AlphaBattle;

import battlecode.common.*;


public class Gardener {
	static RobotController rc; 
	

	public Gardener(RobotController rc) {
		Gardener.rc = rc; 
	}
	
	public void run() {
		 System.out.println("I'm a gardener!");

	        // The code you want your robot to perform every round should be in this loop
	        while (true) {

	            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
	            try {

	            	// Get Random Direction
	            	Direction dir = Util.randomDirection();
	            	
	                // Listen for home archon's location
	                int xPos = rc.readBroadcast(0);
	                int yPos = rc.readBroadcast(1);
	                MapLocation archonLoc = new MapLocation(xPos,yPos);

	                //Attempt to build a lumberjack in this direction
	                if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && rc.isBuildReady()) {
	                    rc.buildRobot(RobotType.LUMBERJACK, dir);
	                }

	                // Move away from archon
	                Util.tryMove(dir);

	                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
	                Clock.yield();

	            } catch (Exception e) {
	                System.out.println("Gardener Exception");
	                e.printStackTrace();
	            }
	        }
		
	}
	
	

}
