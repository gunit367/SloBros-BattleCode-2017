package AnotherBattle;

import battlecode.common.*;

public strictfp class RobotPlayer {
    public static RobotController rc;
    public static RobotMemory mem;
   
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;

        switch (rc.getType()) {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
                runSoldier();
                break;
            case LUMBERJACK:
                runLumberjack();
                break;
            case TANK:
            	runTank();
            	break;
            case SCOUT:
            	runScout();
            	break;
        }
        
    }
    
    public void logic() throws GameActionException {
    	tryShake();
    }

    static void runTank() throws GameActionException
    {
    	try
    	{
    		Tank t = new Tank(rc);
    		t.run();
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error Running Tank Object");
    	}

    	
    }
    
    static void runScout() throws GameActionException
    {
    	try
    	{
    		Scout scout = new Scout(rc);
    		scout.run();
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error Running Tank Object");
    	}
    	
    }
    
    static void runArchon() throws GameActionException
    {
    	try
    	{
    		Archon archon = new Archon(rc);
    		archon.run();
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error Running Tank Object");
    	}
    	
    }

	static void runGardener() throws GameActionException 
	{
		try
    	{
    		Gardener gardener = new Gardener(rc);
    		gardener.run();
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error Running Tank Object");
    	}
       
    }

    static void runSoldier() throws GameActionException 
    {
    	try
    	{
    		Soldier soldier = new Soldier(rc);
    		soldier.run();
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error Running Tank Object");
    	}
        
    }

    static void runLumberjack() throws GameActionException
    {
    	try
    	{
    		Lumberjack l = new Lumberjack(rc);
    		l.run();
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error Running Tank Object");
    	}
    	
    }
    
    static boolean isNearArchon() {
    	return false; 
    }
    
    static boolean isNearSoldier() {
    	return false; 
    } 
    
    static boolean isNearTank() {
    	return false; 
    }
    
    Direction followEnemy(RobotInfo enemy, float distance)
	{
		Direction dir = enemy.location.directionTo(rc.getLocation()).rotateRightDegrees(20);
		MapLocation toLoc = enemy.location.add(dir, distance);
		return rc.getLocation().directionTo(toLoc);
	}
    
	void incrementCount(RobotType type)
	{
		int old;
		try
		{
			switch (type)
			{
				case SOLDIER:
					old = TeamComms.getSoldiers();
					TeamComms.updateSoldiers(old + 1);
					break;
				case LUMBERJACK:
					old = TeamComms.getLumberjacks();
					TeamComms.updateLumberjacks(old + 1);
					break;
				case SCOUT:
					old = TeamComms.getScouts();
					TeamComms.updateScouts(old + 1);
					break;
				case TANK:
					old = TeamComms.getTanks();
					TeamComms.updateTanks(old + 1);
				case ARCHON:
					break;
				case GARDENER:
					old = TeamComms.getGardeners();
					TeamComms.updateGardeners(old + 1);
					break;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error Incrementing Counts");
		}
	}
	
	public void updateAreaOfInterest() throws GameActionException
	{
		updateEnemyArchon();
		updateMilitaryAOI();
	}
	
	public void updateMilitaryAOI() throws GameActionException {
		MapLocation militaryAOI = TeamComms.getAreaOfMilitaryInterest();
		MapLocation archonLoc = TeamComms.getOppArchonLoc();
		RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
				
		if (militaryAOI != null) {
			if (Util.nearLocation(militaryAOI) && rc.senseNearbyRobots(-1, rc.getTeam().opponent()).length == 0) {
                TeamComms.setAreaOfMilitaryInterest(new MapLocation(0, 0));
			}
		} else if (archonLoc != null) {
			TeamComms.setAreaOfMilitaryInterest(archonLoc);
		} else {
			if (robots.length > 0) {
				TeamComms.setAreaOfMilitaryInterest(robots[0].location);
			}
		}
	
	}
	
	public void updateEnemyArchon() throws GameActionException {
		RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		MapLocation archonLoc = TeamComms.getOppArchonLoc();	
		
		if (archonLoc != null) {
			if (rc.getLocation().distanceTo(archonLoc) < (rc.getType().sensorRadius) - .5 && robots.length == 0) {
					TeamComms.broadcastOppArchonLoc(new MapLocation(0, 0));
			}
		} else if (robots.length > 0) {
			for (int i = 0; i < robots.length; i++) {
				if (robots[i].type.equals(RobotType.ARCHON)) {
					TeamComms.broadcastOppArchonLoc(robots[i].location);
					return;
				}
			}
		}
		
	}
	
	public boolean foundLand(int radius) throws GameActionException
	{
		RobotInfo[] team = rc.senseNearbyRobots(radius, rc.getTeam());
		TreeInfo[] trees = rc.senseNearbyTrees();
		
		if (team.length == 0 && trees.length == 0)
		{
			return true;
		} 
			
		
		Util.tryMove(rc, Util.randomDirection());
		
		
		return false; 
		
	}
	
	public void tryShake() throws GameActionException {
		TreeInfo[] trees = rc.senseNearbyTrees();
		
		if (trees.length > 0 && rc.canShake(trees[0].ID)) {
			rc.shake(trees[0].ID);
		}
	}
    
}
