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
	
	public void run() throws GameActionException {				
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
		// Generate a random direction
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        Direction dir = rc.getLocation().directionTo(TeamComms.getOppArchonLoc(rc));
        
        checkDonation();
        
        //updateAreaOfInterest();
        System.out.println("NUM GARDENERS " + TeamComms.getGardeners(rc));

        // Attempt to deploy with a max number of gardeners
        if (TeamComms.getGardeners(rc) < 3) 
        {
        	// This function builds a gardener if possible, and increments the unit count
        	deployGardener(dir);
        }
        
        // Look for enemies and move away from them
        if (enemies.length > 0)
        {
        	Util.tryMove(rc, (rc.getLocation().directionTo(enemies[0].location)).opposite());
        }

        // Broadcast archon's location for other robots on the team to know
        TeamComms.broadcastArchonLoc(rc);
	}
	
	void initArchon(RobotController rc) throws GameActionException
	{
		System.out.println("I'm an archonnn!");
		TeamComms.broadcastArchonLoc(rc);
		TeamComms.updateGardeners(rc, 0);
		TeamComms.updateSoldiers(rc, 0);
		TeamComms.updateLumberjacks(rc, 0);
		TeamComms.updateScouts(rc, 0);
		TeamComms.updateTanks(rc, 0);
		if (rc.getTeam() == Team.A) {
			broadcastEnemyArchon(Team.B); 
		} else { 
			broadcastEnemyArchon(Team.A); 
		}
		System.out.println("Afterewards");
		
		// Generate Initial Area of Interest to Explore
		MapLocation[] areasOfInterest = rc.getInitialArchonLocations(rc.getTeam().opponent());
		if(areasOfInterest.length > 0)
		{
			TeamComms.setAreaOfInterest(rc, areasOfInterest[0]);
		}
	}
	
	void broadcastEnemyArchon(Team enemy) throws GameActionException {
		MapLocation enemyArchon = rc.getInitialArchonLocations(enemy)[0]; 
		TeamComms.broadcastOppArchon(rc, enemyArchon);
		System.out.println(enemyArchon.toString());
	}
	
	void deployGardener(Direction dir) throws GameActionException
	{
		// Attempt to hire a gardener
		if (rc.canHireGardener(dir))
		{
			rc.hireGardener(dir);
			incrementCount(RobotType.GARDENER);
		}
	}
	
	void updateAreaOfInterest() throws GameActionException
	{
		if(rc.getRoundNum() - TeamComms.getLastArchonSighting(rc) < 5)
		{
			MapLocation l = TeamComms.getOppArchonLoc(rc);
			TeamComms.setAreaOfInterest(rc, l);
		}
	}
	
	void checkDonation() throws GameActionException {
		if (rc.getTeamBullets() > 10000) {
			rc.donate(rc.getTeamBullets());
		}
	}
	
}
