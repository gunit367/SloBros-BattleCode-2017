package AlphaBattle;
import battlecode.common.*;

public class SoldierMemory implements RobotMemory
{
	RobotController rc;
	
	public SoldierMemory(RobotController rc)
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
