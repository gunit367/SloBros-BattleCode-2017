package AlphaBattle;
import battlecode.common.*;

public class SoldierMemory implements RobotMemory
{
	RobotController rc;
    RobotInfo enemyArchon[]; 
    MapLocation EnemyArchonLoc;
	
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
	
	public MapLocation getEnemyLocation() {
		if (enemyArchon != null) {
			//if ()
			
		} 
		return null; 
	}
}
