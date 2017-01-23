package AlphaBattle;
import battlecode.common.*;

public class GardenerMemory implements RobotMemory
{
	RobotController rc;
	Direction dir; 
	int strat = 0; 
	
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
	
	public void setDirectionToDeploy(Direction direction) {
		this.dir = direction; 
	}
	
	public Direction getDirectionToDeploy() {
		return dir; 
	}
	
	public int getStrat() {
		return this.strat;
	}
	
	public void setStrat(int strat) {
		this.strat = strat; 
	}
}
