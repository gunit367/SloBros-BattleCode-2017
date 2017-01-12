package AlphaBattle;

import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

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
	}

    static void runTank() throws GameActionException
    {
    	Tank t = new Tank(rc);
    	t.run();
    }
    
    static void runScout() throws GameActionException
    {
    	Scout s = new Scout(rc);
    	s.run();
    }
    
    static void runArchon() throws GameActionException {
    	
    	Archon a = new Archon(rc);
    	a.run();
        
    }

	static void runGardener() throws GameActionException {
       Gardener gardener = new Gardener(rc);
       gardener.run();
       
    }

    static void runSoldier() throws GameActionException {
        Soldier soldier = new Soldier(rc);
        soldier.run();
    }

    static void runLumberjack() throws GameActionException {
    	Lumberjack l = new Lumberjack(rc);
    	l.run();
    }
}
