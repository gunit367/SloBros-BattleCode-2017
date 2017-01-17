package AlphaBattle;
import battlecode.common.*;

public class TankMemory implements RobotMemory
{
	RobotController rc;
	
	public TankMemory (RobotController rc)
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
