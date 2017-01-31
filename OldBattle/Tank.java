package OldBattle;
import battlecode.common.*;

public class Tank extends RobotPlayer {

	RobotController rc;
	TankMemory mem;
	
	public Tank(RobotController rc)
	{
		this.rc = rc;
		mem = new TankMemory(rc);
	}
	
	public void run()
	{
		System.out.println("I am a Tank!");
		
		while (true)
    	{
    		try
    		{
    			MilitaryUtil.offense(rc);
    			
    		}
    		catch (Exception e)
    		{
    			System.out.println("Tank Exception");
    			e.printStackTrace();
    		}
    		Clock.yield();
    	}
	}
}
