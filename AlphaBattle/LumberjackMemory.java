package AlphaBattle;
import battlecode.common.*;

public class LumberjackMemory implements RobotMemory
{
	static float attackRange = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
	
	RobotController rc;
	public Team enemy;
	public RobotInfo[] enemiesInView;
	public RobotInfo[] enemiesInRange;
	public RobotInfo[] alliesInView;
	public RobotInfo[] alliesInRange;
	public TreeInfo[] trees;
	public boolean isChopping; 
	
	
	public LumberjackMemory(RobotController rc)
	{
		this.rc = rc;
		enemy = rc.getTeam().opponent();
		isChopping = false;
		updateMemory();
	}
	
	public void updateMemory()
	{
		// Update Entities that can be seen
		enemiesInView = rc.senseNearbyRobots(-1, enemy);
		enemiesInRange = rc.senseNearbyRobots(attackRange, enemy);
		alliesInView = rc.senseNearbyRobots(-1, rc.getTeam());
		alliesInRange = rc.senseNearbyRobots(attackRange, enemy);
		trees = rc.senseNearbyTrees();
	}
	
	public MapLocation getImportantLoc()
	{
		return null;
	}
	
	public boolean shouldAttack()
	{
		if (alliesInRange.length > 0)
			return false;
		
		
		return enemiesInRange.length > 0;
	}
	
	public boolean shouldChop()
	{
		return trees.length > 0;
	}
	
	public boolean shouldMove() {
		return !isChopping;
	}
	
}
