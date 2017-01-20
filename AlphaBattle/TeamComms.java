package AlphaBattle;
import battlecode.common.*;

// Maximum channels: 1000

public class TeamComms {

	// Friendly Team Information
	// Friendly Team Archon Location: Channels 0 and 1
	public static final int archonX = 0;
	public static final int archonY = 1;
	
	// Friendly Team Unit Counts: Channels 2 - 6
	public static final int numGardeners = 2;
	public static final int numSoldiers = 3;
	public static final int numLumberjacks = 4;
	public static final int numScouts = 5;
	public static final int numTanks = 6;
	
	// DonationCount 
	public static final int donationCount = 50; 
	
	// Enemy Team Information: Starting at Channel 500
	public static final int oppArchonX = 500;
	public static final int oppArchonY = 501;
	public static final int archonSightingTimestamp = 502;
	
	// Prototype for marking Locations to Explore/Secure
	public static final int areaOfInterestX = 503;
	public static final int areaOfInterestY = 504;
	
	// Communication Functions
	// Friendly Archon Location
	public static MapLocation getArchonLoc(RobotController rc) throws GameActionException
	{
		int x = rc.readBroadcast(archonX);
		int y = rc.readBroadcast(archonY);
		return new MapLocation(x,y);
	}
	
	public static void broadcastArchonLoc(RobotController rc) throws GameActionException
	{
		if(rc.getType() == RobotType.ARCHON)
		{
			MapLocation l = rc.getLocation();
			rc.broadcast(archonX, (int)l.x);
			rc.broadcast(archonY, (int) l.y);
		}
	}
	
	
	// Friendly Team Counts
	// Gardener Counts
	public static int getGardeners(RobotController rc) throws GameActionException
	{
		return rc.readBroadcast(numGardeners);
	}
	
	public static void updateGardeners(RobotController rc, int n) throws GameActionException
	{
		rc.broadcast(numGardeners, n);
	}
	
	// Soldier Counts
	public static int getSoldiers(RobotController rc) throws GameActionException
	{
		return rc.readBroadcast(numSoldiers);
	}
	
	public static void updateSoldiers(RobotController rc, int n) throws GameActionException
	{
		rc.broadcast(numSoldiers, n);
	}
	
	// Scouts Counts
	public static int getScouts(RobotController rc) throws GameActionException
	{
		return rc.readBroadcast(numScouts);
	}
	
	public static void updateScouts(RobotController rc, int n) throws GameActionException
	{
		rc.broadcast(numScouts, n);
	}
	
	// Lumberjack Counts
	public static int getLumberjacks(RobotController rc) throws GameActionException
	{
		return rc.readBroadcast(numLumberjacks);
	}
	
	public static void updateLumberjacks(RobotController rc, int n) throws GameActionException
	{
		rc.broadcast(numLumberjacks, n);
	}
	
	// Tank Counts
	public static int getTanks(RobotController rc) throws GameActionException
	{
		return rc.readBroadcast(numTanks);
	}
	
	public static void updateTanks(RobotController rc, int n) throws GameActionException
	{
		rc.broadcast(numTanks, n);
	}
		
	// Return the donation count 
	public static int getDonationCount(RobotController rc) throws GameActionException {
		return rc.readBroadcast(donationCount);
	}
	
	public static void broadcastDonationCount(RobotController rc, int toAdd) throws GameActionException {
		rc.broadcast(donationCount, getDonationCount(rc) + toAdd);
	}
	
	
	// Last Known Enemy Archon Location
	public static void broadcastOppArchon(RobotController rc, MapLocation l) throws GameActionException
	{
		rc.broadcast(oppArchonX, (int) l.x);
		rc.broadcast(oppArchonY, (int) l.y);
		rc.broadcast(archonSightingTimestamp, rc.getRoundNum());
	}
	
	public static int getLastArchonSighting(RobotController rc) throws GameActionException
	{
		return rc.readBroadcast(archonSightingTimestamp);
	}
	
	public static MapLocation getOppArchonLoc(RobotController rc) throws GameActionException
	{
		int x = rc.readBroadcast(oppArchonX);
		int y = rc.readBroadcast(oppArchonY);
		return new MapLocation(x,y);
	}
	
	public static void setAreaOfInterest(RobotController rc, MapLocation l) throws GameActionException
	{
		RobotType t = rc.getType();
		if(t == RobotType.ARCHON || t == RobotType.SCOUT)
		{
			rc.broadcast(areaOfInterestX, (int)l.x);
			rc.broadcast(areaOfInterestY, (int)l.y);
		}
	}
	
	public static MapLocation getAreaOfInterest(RobotController rc) throws GameActionException
	{
		int x = rc.readBroadcast(areaOfInterestX);
		int y = rc.readBroadcast(areaOfInterestY);
		return new MapLocation(x,y);
	}
}
