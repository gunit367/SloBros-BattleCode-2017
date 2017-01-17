package AlphaBattle;
import battlecode.common.*;

public class ScoutMemory implements RobotMemory
{
	public Team enemyTeam;
	RobotController rc;
	public RobotInfo[] recentEnemies;
	
	public ScoutMemory(RobotController rc)
	{
		this.rc = rc;
		enemyTeam = rc.getTeam().opponent();
		this.recentEnemies = null;
	}
	
	public void updateMemory()
	{
		recentEnemies = rc.senseNearbyRobots(-1, enemyTeam);
	}
	
	public MapLocation getImportantLoc()
	{
		return null;
	}
}
