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
	MapLocation archonLocation;

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
		
		if(alliesInView.length > 0)
		{
			analyzeAllies();
		}
		
		if(enemiesInView.length > 0)
		{
			analyzeEnemies();
		}
	}
	
	void analyzeAllies()
	{
		for(int i = 0; i < alliesInView.length; i++)
		{
			RobotInfo ally = alliesInView[i];
			if(ally.getType().equals(RobotType.ARCHON))
				archonLocation = ally.location;
		}
	}
	
	void analyzeEnemies()
	{
		try
		{
			for(int i = 0; i < enemiesInView.length; i++)
			{
				RobotInfo enemy = enemiesInView[i];
				if(enemy.getType().equals(RobotType.ARCHON))
				{
					TeamComms.broadcastOppArchon(enemy.location);
				}
			}
		} 
		catch (GameActionException e)
		{
			System.out.println("Exception while analyzing enemies");
			e.printStackTrace();
		}
	}
	
	float getAttackRange()
	{
		return 0;
	}
}
