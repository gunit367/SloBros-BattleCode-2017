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
    	while (true)
    	{
    		try
    		{
    			
    		} catch (Exception e)
    		{
    			System.out.println("Tank Exception");
    			e.printStackTrace();
    		}
    	}
    }
    
    static void runScout() throws GameActionException
    {
    	while (true)
    	{
    		try
    		{
    			
    		} catch (Exception e)
    		{
    			System.out.println("Scout Exception");
    			e.printStackTrace();
    		}
    	}
    }
    
    static void runArchon() throws GameActionException {
    	try {
    		System.out.println("Before");
    		Archon a = new Archon(rc);
    		a.run();
    		System.out.println("After");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        
    }

	static void runGardener() throws GameActionException {
		try {
			Gardener gardener = new Gardener(rc);
			gardener.run();
			System.out.println("THis is a mothafuckin gardena");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    static void runSoldier() throws GameActionException {
        Soldier soldier = new Soldier();
        soldier.run();
    }

    static void runLumberjack() throws GameActionException {
    	
    }
}
