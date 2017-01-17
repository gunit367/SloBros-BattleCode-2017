package AlphaBattle;
import battlecode.common.*;

public class LumberjackMemory implements RobotMemory
{
	RobotController rc;
	
	public LumberjackMemory(RobotController rc)
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
