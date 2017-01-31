package AlphaBattle;

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
		smartMove();
		
		// End of Turn Computations
		updateAreaOfInterest();
	}
	
	void executeMove() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		
		if (enemies.length > 0) {
			Util.tryMove(enemies[0].location, 2);
		} else {
			Direction dir = rc.getLocation().directionTo(TeamComms.getOppArchonLoc());
			if (dir != null) {
				Util.tryMove(dir);	
			} else {
				Util.tryMove(Util.randomDirection());
			}
		}
	}

	void smartMove() 
	{
		if(mem.isChopping)
		{
			return;
		}
		
		try
		{
			// Calculate where to move next
			Direction dir = calculateMoveDirection();

			// Move there 
			if (!Util.tryMove(dir))
			{
				// try turning left first
				Util.tryMove(dir.rotateLeftDegrees(90));
				
				// try right next
				Util.tryMove(dir.rotateRightDegrees(90));
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
		if(mem.enemiesInView.length > 0)
		{
			return rc.getLocation().directionTo(mem.enemiesInView[0].location);
		} 
		else if (mem.bestTree != null && mem.bestTree.radius > mem.MIN_TREE_RADIUS)
		{
			return rc.getLocation().directionTo(mem.bestTree.location);
		}
		else if (TeamComms.getAreaOfMilitaryInterest() != null)
		{
			return rc.getLocation().directionTo(TeamComms.getAreaOfMilitaryInterest());
		}
		else
		{
			return rc.getLocation().directionTo(TeamComms.getInitialArchonLocation());
		}
	}
	
	void executeAttack() {
		try
		{
			if (rc.canStrike() && mem.enemiesInRange.length > 0)
				rc.strike();
			if (mem.shouldChop())
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
		TreeInfo[] trees = rc.senseNearbyTrees(-1, rc.getTeam());
		if(mem.bestTree == null)
		{
			if (trees.length == 0)
			{
				return;
			}
			mem.bestTree = trees[0];
		}
		
		if (rc.canChop(mem.bestTree.ID))
		{
			mem.isChopping = true;
			rc.chop(mem.bestTree.ID);
		}
	}

}
