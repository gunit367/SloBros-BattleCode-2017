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
		MapLocation aoi = TeamComms.getAreaOfMilitaryInterest(rc);
		Direction dirToAOI = rc.getLocation().directionTo(aoi);
		Direction random = Util.randomDirection();
		
		MilitaryUtil.dodge();

		if (robots.length > 0) {
			MilitaryUtil.shootEnemy(rc, 3, robots[0].getID());
			followEnemy(robots[0]);
			TeamComms.setAreaOfMilitaryInterest(rc, robots[0].location);
		} else if (aoi != null && rc.canMove(aoi)) {
			System.out.println("aoi is not null");
			Util.tryMove(rc, dirToAOI);
		} else if (moveTowardsEnemyArchon()) {
			
		} else if (rc.canMove(random)) {
			Util.tryMove(rc, random);
		} 
		
		updateAreaOfInterest();
		
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
	
	public boolean moveTowardsEnemyArchon() throws GameActionException {
		MapLocation enemyArchon = TeamComms.getOppArchonLoc(rc);
		
		if (enemyArchon == null) {
			System.out.println("EnemyArchon == null");
			return false; 
		}
		
		Direction dirToEnemyArchon = rc.getLocation().directionTo(enemyArchon);
		if (rc.canMove(dirToEnemyArchon) && Util.tryMove(rc, dirToEnemyArchon)) {
			return true; 
		} else if (rc.canFireSingleShot() && MilitaryUtil.noFriendlyFire(rc, dirToEnemyArchon)) {
			rc.fireSingleShot(dirToEnemyArchon);
		}
		
		return false; 
	}
	
	public void updateAreaOfInterest() throws GameActionException{
		if (TeamComms.getOppArchonLoc(rc) != null) {
			if (rc.getLocation().distanceTo(TeamComms.getOppArchonLoc(rc)) < 4 && rc.senseNearbyRobots(-1, rc.getTeam().opponent()).length == 0) {
				System.out.println("-------------- Archon");
				TeamComms.broadcastOppArchon(rc, new MapLocation(0, 0));
			}
		}
		
		if (TeamComms.getAreaOfMilitaryInterest(rc) != null) {
			System.out.println("Area of interest is null");
			if (rc.getLocation().distanceTo(TeamComms.getAreaOfMilitaryInterest(rc)) < 4 && rc.senseNearbyRobots(-1, rc.getTeam().opponent()).length == 0) {
				System.out.println("-------------- Military");
				TeamComms.setAreaOfMilitaryInterest(rc, new MapLocation(0,0));
			}
		}
		
	

	}
}
