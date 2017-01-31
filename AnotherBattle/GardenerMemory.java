package AnotherBattle;
import battlecode.common.*;

public class GardenerMemory extends RobotMemory
{
	Direction dir; 
	TreeInfo tree; 
	int strat;
	int count;

	public GardenerMemory(RobotController rc)
	{
		super(rc);
		strat = 0;
		tree = null; 
		count = 0;
	}
	
	public void updateMemory()
	{
		super.updateMemory();
		
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
	
	public void setWateringTree(TreeInfo tree) {
		this.tree = tree; 
	}
	
	public TreeInfo getWateringTree(int strat) {
		return tree; 
	}
}
