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
	
	// Military Informatin: Starting at Channel 100
	public static final int areaOfInterestMilitaryX = 100; 	
	public static final int areaOfInterestMilitaryY = 101; 

	
	// Enemy Team Information: Starting at Channel 500
	public static final int oppArchonX = 500;
	public static final int oppArchonY = 501;
	public static final int archonSightingTimestamp = 502;
	
	// Prototype for marking Locations to Explore/Secure
	public static final int areaOfInterestX = 503;
	public static final int areaOfInterestY = 504;
	
	// Communication Functions
	// Friendly Archon Location
	public static MapLocation getArchonLoc() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		int x = rc.readBroadcast(archonX);
		int y = rc.readBroadcast(archonY);
		return new MapLocation(x,y);
	}
	
	public static void broadcastArchonLoc() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		if(rc.getType() == RobotType.ARCHON)
		{
			MapLocation l = rc.getLocation();
			rc.broadcast(archonX, (int)l.x);
			rc.broadcast(archonY, (int) l.y);
		}
	}
	
	
	// Friendly Team Counts
	// Gardener Counts
	public static int getGardeners() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		return rc.readBroadcast(numGardeners);
	}
	
	public static void updateGardeners(int n) throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		rc.broadcast(numGardeners, n);
	}
	
	// Soldier Counts
	public static int getSoldiers() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		return rc.readBroadcast(numSoldiers);
	}
	
	public static void updateSoldiers(int n) throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		rc.broadcast(numSoldiers, n);
	}
	
	// Scouts Counts
	public static int getScouts() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		return rc.readBroadcast(numScouts);
	}
	
	public static void updateScouts(int n) throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		rc.broadcast(numScouts, n);
	}
	
	// Lumberjack Counts
	public static int getLumberjacks() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		return rc.readBroadcast(numLumberjacks);
	}
	
	public static void updateLumberjacks(int n) throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		rc.broadcast(numLumberjacks, n);
	}
	
	// Tank Counts
	public static int getTanks() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		return rc.readBroadcast(numTanks);
	}
	
	public static void updateTanks(int n) throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		rc.broadcast(numTanks, n);
	}
		
	// Return the donation count 
	public static int getDonationCount() throws GameActionException 
	{
		RobotController rc = RobotPlayer.rc;
		return rc.readBroadcast(donationCount);
	}
	
	public static void broadcastDonationCount(int toAdd) throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		rc.broadcast(donationCount, getDonationCount() + toAdd);
	}
	
	
	// Last Known Enemy Archon Location
	public static void broadcastOppArchon(MapLocation l) throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		rc.broadcast(oppArchonX, (int) l.x);
		rc.broadcast(oppArchonY, (int) l.y);
		rc.broadcast(archonSightingTimestamp, rc.getRoundNum());
	}
	
	public static int getLastArchonSighting() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		return rc.readBroadcast(archonSightingTimestamp);
	}
	
	public static MapLocation getOppArchonLoc() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		int x = rc.readBroadcast(oppArchonX);
		int y = rc.readBroadcast(oppArchonY);
		return x != 0 && y != 0? new MapLocation(x,y) : null;
	}
	
	public static void setAreaOfInterest(MapLocation l) throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		RobotType t = rc.getType();
		if(t == RobotType.ARCHON || t == RobotType.SCOUT)
		{
			rc.broadcast(areaOfInterestX, (int)l.x);
			rc.broadcast(areaOfInterestY, (int)l.y);
		}
	}
	

	public static MapLocation getAreaOfInterest() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		int x = rc.readBroadcast(areaOfInterestX);
		int y = rc.readBroadcast(areaOfInterestY);
		return new MapLocation(x,y);
	}
	
	public static void setAreaOfMilitaryInterest(MapLocation l) throws GameActionException 
	{
		RobotController rc = RobotPlayer.rc;
		rc.broadcast(areaOfInterestMilitaryX, (int) l.x);
		rc.broadcast(areaOfInterestMilitaryY, (int) l.y);
	}
	
	public static MapLocation getAreaOfMilitaryInterest() throws GameActionException 
	{
		RobotController rc = RobotPlayer.rc;
		int x = rc.readBroadcast(areaOfInterestMilitaryX);
		int y = rc.readBroadcast(areaOfInterestMilitaryY);
		return x != 0 && y != 0? new MapLocation(x,y) : null;
	}
	
	public static Direction getDirectionToInitialArchonLoc() throws GameActionException
	{
		RobotController rc = RobotPlayer.rc;
		return rc.getLocation().directionTo(getOppArchonLoc());
	}
}
