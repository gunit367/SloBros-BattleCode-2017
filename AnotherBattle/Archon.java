package AnotherBattle;

import battlecode.common.*;

public class Archon extends RobotPlayer {

	RobotController rc;
	ArchonMemory mem;
	
	public Archon(RobotController rc)
	{
		this.rc = rc;
		mem = new ArchonMemory(rc);
	}
	
	public void run() throws GameActionException 
	{			
		// Initialize TeamComms 
		initArchon(rc);

		while (true) {
            try {
            	
            	logic(); 
            	Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
	}
	
	
	
	public void logic() throws GameActionException {
		super.logic();

		executeMove();
		executeAction();
        	
	}

	void executeMove() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        
        // Look for enemies and move away from them
        if (enemies.length > 0)
        {
        	Util.tryMove(rc, (rc.getLocation().directionTo(enemies[0].location)).opposite());
        }
	}
	
	void executeAction() throws GameActionException {    
        deploy();
        checkDonation();
        TeamComms.broadcastArchonLoc();
        TeamComms.updateTurnCount();
	}
	
	void initArchon(RobotController rc) throws GameActionException
	{
		TeamComms.broadcastArchonLoc();
		TeamComms.broadcastNumArchons();
		broadcastEnemyArchon();

		// Generate Initial Area of Interest to Explore
		MapLocation[] areasOfInterest = rc.getInitialArchonLocations(rc.getTeam().opponent());
		if(areasOfInterest.length > 0)
		{
			TeamComms.setAreaOfInterest(areasOfInterest[0]);
		}
	}
	
	void broadcastEnemyArchon() throws GameActionException {
		MapLocation enemyArchon = rc.getInitialArchonLocations(rc.getTeam().opponent())[0];
			
		TeamComms.broadcastOppArchonLoc(enemyArchon);
	}
	
	void deploy() throws GameActionException {
		int turn = TeamComms.getTurnCount();
		int numTrees = rc.senseNearbyTrees().length;
		int numTeamAround = rc.senseNearbyRobots(-1, rc.getTeam()).length;
				
		if (numTrees > 10 && numTeamAround > 3) {
			return; 
		}
		
		if ((turn < 500  && turn % 10 == 0)|| turn % 25 == 0) {
			deployGardener();
		}
	}
	
	void deployGardener() throws GameActionException
	{		
        Direction dirToEnemy = TeamComms.getDirectionToInitialArchonLoc();
        Direction random = Util.randomDirection();
		boolean canBuild = rc.canBuildRobot(RobotType.GARDENER, dirToEnemy);
		
        if (canBuild)
        {
        	rc.hireGardener(dirToEnemy);
        	incrementCount(RobotType.GARDENER);
        }
        else if (rc.canBuildRobot(RobotType.GARDENER, random)) {
        	rc.hireGardener(random);
        	incrementCount(RobotType.GARDENER);
        }
	}
	
	void updateTeamAreaOfInterest() throws GameActionException
	{
		if(rc.getRoundNum() - TeamComms.getLastArchonSighting() < 5)
		{
			MapLocation l = TeamComms.getOppArchonLoc();
			TeamComms.setAreaOfInterest(l);
		}
	}
	
	void checkDonation() throws GameActionException {
		double cost = 7.5 + (rc.getRoundNum())*12.5 / 3000;
		if (rc.getTeamBullets() / cost > 1000 - rc.getTeamVictoryPoints()) {
			rc.donate(rc.getTeamBullets());
		} else if (rc.getTeamBullets() > 1000) {
			rc.donate(500);
		}
	}
	
}
