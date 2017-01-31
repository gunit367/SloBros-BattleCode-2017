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
	MapLocation[] areasOfInterest;
	public  boolean canSeeOppArchon;
	public int birthCount;
	
	public float mapMinX;
	public float mapMinY;
	public float mapMaxX;
	public float mapMaxY;
	
	public RobotMemory(RobotController rc)
	{
		this.rc = rc;
		enemy = rc.getTeam().opponent();
		updateMemory();
		birthCount = 0;
		
		MapLocation loc = rc.getLocation();
		mapMinX = loc.x - rc.getType().sensorRadius;
		mapMaxX = loc.x + rc.getType().sensorRadius;
		mapMinY = loc.y - rc.getType().sensorRadius;
		mapMaxY = loc.y + rc.getType().sensorRadius;
	}
	
	boolean inCurrentMapBounds(MapLocation l)
	{
		return l.x > mapMinX && l.x < mapMaxX && l.y > mapMinY && l.y < mapMaxY;
	}
	
	void updateMapBounds(MapLocation l)
	{
		if (l.x < mapMinX)
			mapMinX = l.x;
		if(l.x > mapMaxX)
			mapMaxX = l.x;
		if(l.y < mapMinY)
			mapMinY = l.y;
		if(l.y > mapMaxY)
			mapMaxY = l.y;
	}
	
	void updateMapInfo() throws GameActionException
	{
		// Check that N,W,E,S point in view are on map.
		float dist =(float) (rc.getType().sensorRadius - .5);
		MapLocation n = rc.getLocation().add(Direction.getNorth(), dist);
		MapLocation w = rc.getLocation().add(Direction.getWest(), dist);
		MapLocation e = rc.getLocation().add(Direction.getEast(), dist);
		MapLocation s = rc.getLocation().add(Direction.getSouth(), dist);
		
		if(!inCurrentMapBounds(n) && rc.onTheMap(n))
		{
			updateMapBounds(n);
		}
		
		if(!inCurrentMapBounds(w) && rc.onTheMap(w))
		{
			updateMapBounds(w);
		}
		
		if(!inCurrentMapBounds(e) && rc.onTheMap(e))
		{
			updateMapBounds(e);
		}
		
		if(!inCurrentMapBounds(s) && rc.onTheMap(s))
		{
			updateMapBounds(s);
		}
	}
	
	void updateMemory()
	{
		try 
		{
			float attackRange = getAttackRange();

			// Update Entities that can be seen
			enemiesInView = rc.senseNearbyRobots(-1, enemy);
			enemiesInRange = rc.senseNearbyRobots(attackRange, enemy);
			alliesInView = rc.senseNearbyRobots(-1, rc.getTeam());
			alliesInRange = rc.senseNearbyRobots(attackRange, enemy);
			trees = rc.senseNearbyTrees();
			
			updateMapInfo();
			
			if(alliesInView.length > 0)
			{
				analyzeAllies();
			}
			
			if(enemiesInView.length > 0)
			{
				analyzeEnemies();
			}
			birthCount++;
		} 
		catch (GameActionException e)
		{
			System.out.println("Failed to Update Memory");
			e.printStackTrace();
		}
	}
	
	void analyzeAllies()
	{
		for(int i = 0; i < alliesInView.length; i++)
		{
			RobotInfo ally = alliesInView[i];
			if(ally.getType().equals(RobotType.ARCHON))
			{
				archonLocation = ally.location;
			}
		}
	}
	
	void analyzeEnemies()
	{
		boolean canSeeArchon = false;
		try
		{
			for(int i = 0; i < enemiesInView.length; i++)
			{
				RobotInfo enemy = enemiesInView[i];
				if(enemy.getType().equals(RobotType.ARCHON))
				{
					TeamComms.broadcastOppArchonLoc(enemy.location);
					canSeeArchon = true;
				}
			}
			this.canSeeOppArchon = canSeeArchon;
		} 
		catch (GameActionException e)
		{
			System.out.println("Exception while analyzing enemies");
			e.printStackTrace();
		}
	}
	
	float getAttackRange()
	{
		RobotType type = rc.getType();
		switch(type){
		case ARCHON:
			return 0;
		case GARDENER:
			return 0;
		case SCOUT:
			return type.sensorRadius;
		case TANK:
			return type.sensorRadius;
		case SOLDIER:
			return type.sensorRadius;
		case LUMBERJACK:
			return GameConstants.LUMBERJACK_STRIKE_RADIUS;
		default:
			return 0;
			
		}
	}
	
	public boolean canSeeAllyArchon()
	{
		for(int i = 0; i < alliesInView.length; i++)
		{
			if(alliesInView[i].getType() == RobotType.ARCHON)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean canSeeAllyArchon(float radius)
	{
		RobotInfo[] alliesInView = rc.senseNearbyRobots(radius);
		for(int i = 0; i < alliesInView.length; i++)
		{
			if(alliesInView[i].getType() == RobotType.ARCHON)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean canSeeAlly(RobotType type)
	{
		for(int i = 0; i < alliesInView.length; i++)
		{
			if(alliesInView[i].getType() == type)
				return true;
		}
		return false;
	}
	
	// Returns info of the first robot of given type in view
	public RobotInfo findInView(RobotType type)
	{
		for(int i = 0; i < alliesInView.length; i++)
		{
			if(alliesInView[i].getType() == type)
				return alliesInView[i];
		}
		return null;
	}
}
