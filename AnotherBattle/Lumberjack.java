package AnotherBattle;

import battlecode.common.*;

public class Lumberjack extends RobotPlayer {

	RobotController rc;
	LumberjackMemory mem;
	static float attackRange = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;

	public Lumberjack(RobotController rc) {
		this.rc = rc;
		mem = new LumberjackMemory(rc);
	}

	public void run() {
		while (true) {
			try {

				logic();
				Clock.yield();

			} catch (Exception e) {
				System.out.println("Lumberjack Exception");
				e.printStackTrace();
			}
		}
	}

	public void logic() throws GameActionException {
		super.logic();
		// Gather Information Phase
		mem.updateMemory();

		// Attack Phase
		executeAttack();
		
		// Move Phase
		executeMove();
		
		// End of Turn Computations
		updateAreaOfInterest();
	}
	
	void executeMove() throws GameActionException {
		if (!mem.shouldMove() || mem.trees.length > 10) 
		{
			System.out.println("Should not move");
			return;
		} 
		
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		
		if (enemies.length > 0) {
			Util.tryMove(rc, enemies[0].location, 2);
		} else {
			Direction dir = rc.getLocation().directionTo(TeamComms.getOppArchonLoc());
			if (dir != null) {
				Util.tryMove(rc, dir);	
			} else {
				Util.tryMove(rc, Util.randomDirection());
			}
		}
	}

	void smartMove() 
	{
		if (!mem.shouldMove()) 
		{
			System.out.println("Should not move");
			return;
		}
		try
		{
			// Calculate where to move next
			Direction dir = calculateMoveDirection();

			// Move there 
			if (!Util.tryMove(rc, dir))
			{
				// try turning left first
				for(int i = 1; i < 6; i++)
				{
					if(Util.tryMove(rc, dir.rotateLeftDegrees(30 * i)))
						return;
				}
				
				// else try turning right
				for(int i = 1; i < 6; i++)
				{
					if(Util.tryMove(rc, dir.rotateRightDegrees(30 * i)))
						return;
				}
				
				Util.tryMove(rc, dir.opposite());
			}
		} 
		catch (GameActionException e) 
		{
			System.out.println("Lumberjack: Move Failed");
			e.printStackTrace();
		}
	}
	
	Direction calculateMoveDirection() throws GameActionException
	{
		RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		Direction dir = TeamComms.getDirectionToInitialArchonLoc();
		TreeInfo[] trees = rc.senseNearbyTrees(-1, rc.getTeam().opponent());
		TreeInfo[] neutral = rc.senseNearbyTrees(-1, Team.NEUTRAL);
		
		if (robots.length > 0) {
			return rc.getLocation().directionTo(robots[0].location);
		} 
		
		/*if(aoe != null) {
			System.out.println(aoe);
			return rc.getLocation().directionTo(aoe);
		
		}*/
		
		//if (dir == null)
		//{
			if(mem.enemiesInView.length > 0)
			{
				dir = rc.getLocation().directionTo(mem.enemiesInView[0].location);
			} 
			else if (trees.length > 0)
			{
				dir = rc.getLocation().directionTo(trees[0].location);
			} else if (neutral.length > 0) {
				dir = rc.getLocation().directionTo(neutral[0].location);
			}
			else
			{
				dir = Util.randomDirection();
			}
		//}
		
		return dir;
	}
	
	void executeAttack() {
		try
		{
			if (rc.canStrike() && mem.enemiesInRange.length > 0)
				rc.strike();
			else if (mem.shouldChop())
				chopTree();
		} 
		catch (GameActionException e) 
		{
			System.out.println("Lumberjack: executeAttack failed");
			e.printStackTrace();
		}
	}

	void chopTree() throws GameActionException
	{
		TreeInfo[] enemyTrees = rc.senseNearbyTrees(-1, rc.getTeam().opponent());
		TreeInfo[] neutralTrees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
		
		if (enemyTrees.length != 0) {
			TreeInfo tree = enemyTrees[0];
			
			if (rc.canChop(tree.ID)) {
				rc.chop(tree.ID);
			} else {
				Direction dir = rc.getLocation().directionTo(tree.location);
				Util.tryMove(rc, dir);
				System.out.println("Moving to tree");
			}
			
			return; 
			
		}
		
		if (neutralTrees.length != 0) {
			TreeInfo tree = neutralTrees[0];
			
			if (rc.canChop(tree.ID)) {
				rc.chop(tree.ID);
			} else {
				Direction dir = rc.getLocation().directionTo(tree.location);
				Util.tryMove(rc, dir);
			}
			
		}
		
	}

}
