package AlphaBattle;

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
		mem.updateMemory();
		executeMove();
		executeAction();
        	
	}

	void executeMove() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        
        // Look for enemies and move away from them
        if (enemies.length > 0)
        {
        	if(Util.tryMove((rc.getLocation().directionTo(enemies[0].location)).opposite()))
        		return;
        }
        
        if (mem.canSeeAlly(RobotType.GARDENER))
        {
        	if(Util.tryMove(rc.getLocation().directionTo(mem.alliesInView[0].location).opposite()))
        		return;
        }
        Util.tryMove(Util.randomDirection());
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
		int numTeamAround = mem.alliesInView.length;
				
		if (numTrees > 10 && numTeamAround > 2) {
			return; 
		}
		
		if ((turn < 500  && (turn / 40 < TeamComms.getGardeners())) || turn % 80 == 0) {
			deployGardener();
		}
	}
	
	void deployGardener() throws GameActionException
	{		
        Direction dirToEnemy = TeamComms.recentArchonDirection();
		boolean canBuild = rc.canBuildRobot(RobotType.GARDENER, dirToEnemy);
		
        if (canBuild && mem.alliesInView.length < 4)
        {
        	rc.hireGardener(dirToEnemy);
        	incrementCount(RobotType.GARDENER);
        	return;
        }
        
        // try a few more directions
        float offset = 10;
        for(int i = 1; i < 10; i ++)
        {
        	Direction dir = dirToEnemy.rotateLeftDegrees(offset * i);
        	canBuild = rc.canBuildRobot(RobotType.GARDENER, dir);
        	if (canBuild)
        	{
        		rc.hireGardener(dir);
        		incrementCount(RobotType.GARDENER);
        		return;
        	}
        	
        	dir = dirToEnemy.rotateRightDegrees(offset * i);
        	canBuild = rc.canBuildRobot(RobotType.GARDENER, dir);
        	if(canBuild)
        	{
        		rc.hireGardener(dir);
        		incrementCount(RobotType.GARDENER);
        		return;
        	}
        	
        }
//        else if (rc.canBuildRobot(RobotType.GARDENER, random)) {
//        	rc.hireGardener(random);
//        	incrementCount(RobotType.GARDENER);
//        }
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
