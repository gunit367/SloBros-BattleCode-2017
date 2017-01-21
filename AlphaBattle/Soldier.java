package AlphaBattle;

import battlecode.common.*;

public class Soldier extends RobotPlayer {
	RobotController rc;
	SoldierMemory mem;

	public Soldier(RobotController rc) {
		this.rc = rc;
		mem = new SoldierMemory(rc);
	}

	public void run() {
		// helper functions: isEnemyClose(), getEnemyLocation(),
		// shoot(location),
		while (true) {
			try {
				logic(1);
				Clock.yield();
			} catch (Exception e) {
				System.out.println("Soldier Exception");
				e.printStackTrace();
			}
		}
	}

	public void logic(int strat) throws GameActionException {
		switch (strat) {
		case 1:
			offense();
		case 2:
			//defense();
		}
	}

	public void offense() throws GameActionException {
		RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		
		// Fetch Current Area of Interest
		MapLocation aoi = TeamComms.getAreaOfInterest(rc);
		Direction dir = rc.getLocation().directionTo(aoi);
		
		MilitaryUtil.dodge();

		if (robots.length > 0)
		{
			if(Util.pathClearTo(robots[0].location))
				MilitaryUtil.shootEnemy(rc, 0, robots[0].getID());
			followEnemy(robots[0]);
		}
		else
		{
			Util.tryMove(rc, dir);
		}
	}

	//public void defense() {
		/*if (SoldierMemory.getEnemyLocation() != null && rc.canSenseLocation(archonLoc)) {
			archon = rc.senseRobotAtLocation(archonLoc);
		}

		for (int i = 0; i < robots.length; i++) {
			if (robots[i].type == RobotType.ARCHON) {
				archon = robots[i];
				archonLoc = archon.location;
				TeamComms.broadcastOppArchon(rc, archonLoc);
			}
		}*/

	//}
}
