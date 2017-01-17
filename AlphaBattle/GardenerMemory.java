package AlphaBattle;
import battlecode.common.*;

public class GardenerMemory implements RobotMemory
{
	RobotController rc;
	
	public GardenerMemory(RobotController rc)
	{
		this.rc = rc;
	}
	
	public void updateMemory()
	{
		
	}
	
	public MapLocation getImportantLoc()
	{
		return null;
	}
}
