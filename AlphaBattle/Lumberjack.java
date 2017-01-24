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
		// Gather Information Phase
		mem.updateMemory();

		//MilitaryUtil.dodge();

		// Attack Phase
		executeAttack();
		
		// Move Phase
		executeMove();
		
		// End of Turn Computations
		updateAreaOfInterest();
	}

	void executeMove() {
		if (!mem.shouldMove()) {
			return;
		}
		try {
			// Calculate where to move next
			MapLocation aoe = TeamComms.getAreaOfInterest(rc);
			Direction dir = TeamComms.getDirectionToInitialArchonLoc(rc);

			// Move there 
			// Null Pointer Exception at this if statement
			if (aoe == null || !Util.tryMove(rc, dir)) {
				if (!Util.tryMove(rc, dir.rotateLeftDegrees(90)))
				{
					
				}
				else if (!Util.tryMove(rc, dir.rotateRightDegrees(90)))
				{
					
				}
				else if (!Util.tryMove(rc, dir.rotateLeftDegrees(120)))
				{
					
				}
				else if (!Util.tryMove(rc, dir.rotateRightDegrees(120)))
				{
					
				}
				else
					Util.tryMove(rc, dir.rotateLeftDegrees(180));
			} else if (mem.trees.length > 0 && mem.trees[0].getTeam() != rc.getTeam() && !Util.tryMove(rc, mem.trees[0].location, 1)) {
				
			}
			else
			{
				Util.tryMove(rc, rc.getLocation().directionTo(TeamComms.getArchonLoc(rc)));
			}
			
		} catch (GameActionException e) {
			System.out.println("Lumberjack: Move Failed");
			e.printStackTrace();
		}
	}
	
	void executeAttack() {
		try {
			if (rc.canStrike())
				rc.strike();
			else if (mem.shouldChop())
				chopTree();
		} catch (GameActionException e) {
			System.out.println("Lumberjack: executeAttack failed");
			e.printStackTrace();
		}
	}

	void chopTree() {
		TreeInfo tochop = mem.trees[0];
		if (tochop == null || !rc.canChop(tochop.ID)) {
			mem.isChopping = false;
			return;
		}
		try {
			if (rc.canChop(tochop.ID) && tochop.getTeam() != rc.getTeam()) {
				rc.chop(tochop.ID);
				mem.isChopping = true;
			}

		} catch (Exception e) {
			System.out.println("Lumberjack: Chop Failed");
			e.printStackTrace();
		}
	}

}
