package AlphaBattle;
import battlecode.common.*;

public abstract class RobotMemory {
	
	RobotController rc;
	public Team enemy;
	public RobotInfo[] enemiesInView;
	public RobotInfo[] enemiesInRange;
	public RobotInfo[] alliesInView;
	public RobotInfo[] alliesInRange;
	public TreeInfo[] trees;
	
	public RobotMemory(RobotController rc)
	{
		this.rc = rc;
		enemy = rc.getTeam().opponent();
		updateMemory();
	}
	
	void updateMemory()
	{
		float attackRange = getAttackRange();
		
		// Update Entities that can be seen
		enemiesInView = rc.senseNearbyRobots(-1, enemy);
		enemiesInRange = rc.senseNearbyRobots(attackRange, enemy);
		alliesInView = rc.senseNearbyRobots(-1, rc.getTeam());
		alliesInRange = rc.senseNearbyRobots(attackRange, enemy);
		trees = rc.senseNearbyTrees();
	}
	
	
	float getAttackRange()
	{
		return 0;
	}
}
