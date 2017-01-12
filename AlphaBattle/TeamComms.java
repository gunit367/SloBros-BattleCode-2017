package AlphaBattle;
import battlecode.common.*;

// Maximum channels: 1000

public class TeamComms {

	// Friendly Team Archon Location: Channels 0 and 1
	public static final int archonX = 0;
	public static final int archonY = 1;
	
	// Friendly Team Unit Counts: Channels 2 - 6
	public static final int numGardeners = 2;
	public static final int numSoldiers = 3;
	public static final int numLumberjacks = 4;
	public static final int numScouts = 5;
	public static final int numTanks = 6;
	
	//
	
	
	public static MapLocation getArchonLoc(RobotController rc) throws GameActionException
	{
		int x = rc.readBroadcast(archonX);
		int y = rc.readBroadcast(archonY);
		return new MapLocation(x,y);
	}
	
	
}
