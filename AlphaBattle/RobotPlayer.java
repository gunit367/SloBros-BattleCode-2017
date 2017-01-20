package AlphaBattle;

import battlecode.common.*;

public strictfp class RobotPlayer {
    public static RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
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
        
        System.out.println("Here");	
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
    
    Direction followEnemy(RobotInfo enemy)
	{
		Direction dir = enemy.location.directionTo(rc.getLocation());
		MapLocation toLoc = enemy.location.add(dir, 8.5f);
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
					old = TeamComms.getSoldiers(rc);
					TeamComms.updateSoldiers(rc, old + 1);
					break;
				case LUMBERJACK:
					old = TeamComms.getLumberjacks(rc);
					TeamComms.updateSoldiers(rc, old + 1);
					break;
				case SCOUT:
					old = TeamComms.getScouts(rc);
					TeamComms.updateScouts(rc, old + 1);
					break;
				case TANK:
					old = TeamComms.getTanks(rc);
					TeamComms.updateTanks(rc, old + 1);
				case ARCHON:
					break;
				case GARDENER:
					old = TeamComms.getGardeners(rc);
					TeamComms.updateGardeners(rc, old + 1);
					break;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error Incrementing Counts");
		}
	}
    
}
