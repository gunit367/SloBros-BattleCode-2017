package AlphaBattle;

import battlecode.common.*;

public class Soldier extends RobotPlayer {
	RobotController rc;
	SoldierMemory mem;

	public Soldier(RobotController rc) {
		this.rc = rc;
		mem = new SoldierMemory(rc);
		
	}

	public void run() {
		// helper functions: isEnemyClose(), getEnemyLocation(),
		// shoot(location),
		while (true) 
		{
			try 
			{
				logic(1);
				Clock.yield();
			} 
			catch (Exception e) 
			{
				System.out.println("Soldier Exception");
				e.printStackTrace();
			}
		}
	}

	public void logic(int strat) throws GameActionException 
	{
		mem.updateMemory();
		updateAreaOfInterest();
		switch (strat) {
		case 1:
			MilitaryUtil.offense();
		case 2:
			//defense();
		}
	}

}
