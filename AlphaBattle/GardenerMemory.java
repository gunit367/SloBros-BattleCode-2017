package AlphaBattle;
import battlecode.common.*;

public class GardenerMemory extends RobotMemory
{
	//final double FARM_RADIUS = (GameConstants.BULLET_TREE_RADIUS * 2) + RobotType.GARDENER.bodyRadius + 0.3;
	final float FARM_RADIUS = 1.5f;
	Direction dir; 
	TreeInfo tree; 
	int strat;
	int treeCount;
	boolean foundFarmland;
	TreeInfo[] farm;
	Direction[] farmDirections; // THE LAST SPOT (index length - 1) IS THE SPAWN DIRECTION!
	MapLocation farmLocation;
	
	MapLocation explorationLocation;
	Direction exploreDirection;

	public GardenerMemory(RobotController rc)
	{
		super(rc);
		strat = 0;
		tree = null; 
		treeCount = 0;
		foundFarmland = false;
		farmLocation = null;
		
		try
		{
			explorationLocation = rc.getLocation().add(TeamComms.getDirectionToInitialArchonLoc(), FARM_RADIUS);
			exploreDirection = archonLocation.directionTo(rc.getLocation());
		}
		catch (GameActionException e)
		{
			e.printStackTrace();
			explorationLocation = rc.getLocation();
		}
	}
	
	void updateExplorationLocation() throws GameActionException
	{
		if(rc.getLocation().distanceTo(archonLocation) > archonLocation.distanceTo(TeamComms.getInitialArchonLocation()) / 2)
		{
			explorationLocation = archonLocation;
			exploreDirection = rc.getLocation().directionTo(archonLocation);
		}
		
		Direction init = archonLocation.directionTo(rc.getLocation());
		float angle = (float)((Math.random() - .5) * 120);
		exploreDirection = init.rotateLeftDegrees(angle);
		explorationLocation = rc.getLocation().add(exploreDirection, rc.getType().sensorRadius);
	}
	
	public void updateMemory()
	{
		super.updateMemory();
		try
		{
			if(explorationLocation == null)
				return;
			if(!foundFarmland && rc.getLocation().distanceTo(explorationLocation) <= rc.getType().bodyRadius)
			{
				updateExplorationLocation();
			}
		}
		catch (GameActionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	Direction getInitialDirection() throws GameActionException
	{
		Direction dir = rc.getLocation().directionTo(TeamComms.getInitialArchonLocation());
		for(int i = 0; i < 12; i++)
		{
			if(rc.canPlantTree(dir))
				return dir;
			
			dir = dir.rotateLeftDegrees(10);
		}
		if(rc.canPlantTree(dir))
			return dir;
		return null;
	}
	
	public void newFarm() throws GameActionException
	{
		int openSpots = 0;
		farmLocation = rc.getLocation();
		Direction dir = getInitialDirection();
		if(dir == null)
		{
			dir = archonLocation.directionTo(rc.getLocation());
		}
		
		for(int i = 0; i < 5; i++)
		{
			Direction curDir = dir.rotateLeftDegrees((360 / 5) * (i + 1));
			// Find number of open spots
			if(rc.canBuildRobot(RobotType.SOLDIER, curDir) || rc.canPlantTree(curDir))
			{
				openSpots += 1;
			}
		}
		
		if(openSpots < 1)
		{
			foundFarmland = false;
			return;
		}
		
		farmDirections = new Direction[5];
		farm = new TreeInfo[4];
		
		openSpots = 0;
		// Populate Directions of Trees on farm.
		for(int i = 0; i < 5; i++)
		{
			Direction curDir = dir.rotateLeftDegrees((360 / 5) * i).opposite();
			farmDirections[i] = curDir;
		}
		
	}
}
