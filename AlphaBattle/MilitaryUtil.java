package AlphaBattle;

import battlecode.common.*;

public class MilitaryUtil {

	// s the enemy with the given id with the type 0 - single shot; type 1 - triad shot; type 2 - pentad shot
	public static void shootEnemy(RobotController rc, int type, int id) throws GameActionException {
		RobotInfo enemy;
		Direction dir; 
		
		if (rc.canSenseRobot(id)) {
			enemy = rc.senseRobot(id);
		} else {
			return;
		}
		
		dir = rc.getLocation().directionTo(enemy.getLocation());

		
		switch (type) {
			case 0:  
				trySingleShot(rc, dir); 
				break;
			case 1: 
				tryTriadShot(rc, dir);
				break;
			case 2: 
				tryPentadShot(rc, dir); 
				break;
			case 3: 
				if (tryPentadShot(rc, dir)) {
					
				} else if (tryTriadShot(rc, dir)) {

				} else {
					trySingleShot(rc, dir);
				}
 		}	
	}
	
	public static void offense(RobotController rc) throws GameActionException
	{
		Team enemy = rc.getTeam().opponent();
		RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
		TreeInfo[] enemyTrees = rc.senseNearbyTrees(-1, enemy);
		
		// Fetch Current Area of Interest
		MapLocation aoi = TeamComms.getAreaOfMilitaryInterest();
		Direction random = Util.randomDirection();
		
		// try and dodge if needed
		MilitaryUtil.dodge();

		if (robots.length > 0)
		{
			if(Util.pathClearTo(robots[0].location)) {
				MilitaryUtil.shootEnemy(rc, 3, robots[0].getID());
			} else if (Util.tryMove(followEnemy(rc, robots[0], 3f))) {
		
			}
			//followEnemy(robots[0], 2.5f);
			TeamComms.setAreaOfMilitaryInterest(robots[0].location);
			rc.setIndicatorLine(rc.getLocation(), robots[0].location, 100, 222, 55);
		}
		else if (enemyTrees.length > 0 && rc.canFireSingleShot()) 
		{
			if (Util.pathClearTo(enemyTrees[0].location)) {
				rc.fireSingleShot(rc.getLocation().directionTo(enemyTrees[0].location));	
			}
			rc.setIndicatorLine(rc.getLocation(), robots[0].location, 22, 23, 155);
		}
		else if (aoi != null && rc.canMove(aoi))
		{
			Util.tryMove(rc.getLocation().directionTo(TeamComms.getAreaOfMilitaryInterest()));
			rc.setIndicatorLine(rc.getLocation(), aoi, 220, 155, 0);
		} 
		else if (moveTowardsEnemyArchon(rc))
		{
			
		} 
		else if (rc.canMove(random))
		{
			Util.tryMove(random);
		} 
		
	}
	
    public static Direction followEnemy(RobotController rc, RobotInfo enemy, float distance)
	{
		Direction dir = enemy.location.directionTo(rc.getLocation()).rotateRightDegrees(20);
		MapLocation toLoc = enemy.location.add(dir, distance);
		return rc.getLocation().directionTo(toLoc);
	}
	
	public static boolean moveTowardsEnemyArchon(RobotController rc) throws GameActionException
	{
		MapLocation enemyArchon = TeamComms.getAreaOfMilitaryInterest();
		
		if (enemyArchon == null)
		{
			return false; 
		}
		
		rc.setIndicatorDot(enemyArchon, 100, 55, 55);

		Direction dirToEnemyArchon = rc.getLocation().directionTo(enemyArchon);
		if (rc.canMove(dirToEnemyArchon) && Util.tryMove(dirToEnemyArchon))
		{
			return true; 
		} 
		else if (rc.canFireSingleShot() && MilitaryUtil.noFriendlyFire(rc, dirToEnemyArchon))
		{
			//rc.fireSingleShot(dirToEnemyArchon);
		}
		
		return false; 
	}
		
	// Try to fire a single shot
	public static boolean trySingleShot(RobotController rc, Direction dir) throws GameActionException {
		if (rc.canFireSingleShot()) {
			rc.fireSingleShot(dir);
			return true; 
		}
		return false; 
	}

	// Try to fire a Triad shot 
	public static boolean tryTriadShot(RobotController rc, Direction dir) throws GameActionException {
		if (rc.canFireTriadShot()) {
			rc.fireTriadShot(dir);
			return true; 
		}
		return false;
	}
	
	// Try to fire a Pentad shot 
	public static boolean tryPentadShot(RobotController rc, Direction dir) throws GameActionException {
		if (rc.canFirePentadShot()) {
			rc.firePentadShot(dir);
			return true; 
		}
		return false;
	}
	
	static boolean willHitMe(BulletInfo b)
	{
		MapLocation myLocation = RobotPlayer.rc.getLocation();
		
		// Calculate bullet relations to this robot
		Direction directionToRobot = b.location.directionTo(myLocation);
		float distToRobot = b.location.distanceTo(myLocation);
		float theta = b.dir.radiansBetween(directionToRobot);
		
		// If theta > 90 degrees, then the bullet is traveling away from us and we can break early
		if (Math.abs(theta) > Math.PI / 2)
		{
		    return false;
		}
		
		// distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
		// This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
		// This corresponds to the smallest radius circle centered at our location that would intersect with the
		// line that is the path of the bullet.
		float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta));

        return (perpendicularDist <= RobotPlayer.rc.getType().bodyRadius);
	}
	
	static void trySidestep(BulletInfo b)
	{
		try 
		{
			MapLocation myLoc = RobotPlayer.rc.getLocation();
			Direction bulletToRobot = b.location.directionTo(myLoc);
			float theta = b.dir.radiansBetween(bulletToRobot);
			if(theta < 0)
			{
				// Bullet on My Left
				Direction moveDir = myLoc.directionTo(b.location).rotateRightDegrees(90);
				Util.tryMove(moveDir);
			} 
			else
			{
				// Bullet on My Right
				Direction moveDir = myLoc.directionTo(b.location).rotateLeftDegrees(90);
				Util.tryMove(moveDir);
			}
		} 
		catch (GameActionException e) 
		{
			System.out.println("Sidestep Attempt Failed!");
			e.printStackTrace();
		}
	}
	
	public static boolean noFriendlyFire(RobotController rc, Direction dir) {
		RobotInfo[] friendly = rc.senseNearbyRobots(-1, rc.getTeam());
		
		for (int i = 0; i < friendly.length; i++) {
			if (rc.getLocation().directionTo(friendly[i].location).degreesBetween(dir) < 20) {
				return false; 
			}
		}
		return true;	
	}
	
	public static void dodge()
	{
		BulletInfo[] bullets = RobotPlayer.rc.senseNearbyBullets();
		for(int i = 0; i < bullets.length; i++)
		{
			if(willHitMe(bullets[i]))
			{
				trySidestep(bullets[i]);
			}
		}
	}
}
