package AlphaBattle;
import battlecode.common.*;

public class ArchonMemory extends RobotMemory
{
	RobotController rc;
	Direction myDir; 
	int lastSpawnTurn;
	
	public ArchonMemory(RobotController rc)
	{
		super(rc);
		myDir = Util.randomDirection();
		lastSpawnTurn = -100;
	}
	
	public void updateMemory()
	{
		super.updateMemory();
	}
	
	public MapLocation getImportantLoc()
	{
		return null;
	}
	
	public void updateDir() {
		myDir = Util.randomDirection();
	}
	
}
