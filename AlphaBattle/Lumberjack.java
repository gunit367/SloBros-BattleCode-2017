package AlphaBattle;

import battlecode.common.*;


public class Lumberjack extends RobotPlayer {

	RobotController rc; 
	LumberjackMemory mem;
	
	public Lumberjack(RobotController rc) {
		this.rc = rc;
		mem = new LumberjackMemory(rc);
	}
	
	public void run() {
		System.out.println("I'm a lumberjack!");
        Team enemy = rc.getTeam().opponent();
        

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                logic();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
	}
	
	public void logic() throws GameActionException {
        Team enemy = rc.getTeam().opponent();

		// See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
        RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);
        TreeInfo[] trees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS);
        MapLocation archonLoc = TeamComms.getOppArchonLoc(rc);
        
        if(robots.length > 0 && !rc.hasAttacked()) {
            // Use strike() to hit all nearby robots!
            rc.strike();
        } else if (trees.length > 0 && rc.canChop(trees[0].ID) && trees[0].getTeam() != rc.getTeam()) {
        	rc.chop(trees[0].ID);
        } else if (archonLoc != null) {
            Util.tryMove(rc, rc.getLocation().directionTo(archonLoc));
        } else {
        	Util.tryMove(rc, Util.randomDirection());
        }
	}
	
}
