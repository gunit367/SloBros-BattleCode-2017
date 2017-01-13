package examplefuncsplayer;
import battlecode.common.*;

public class Tank {

	static RobotController rc;
	
	public Tank(RobotController rc)
	{
		Tank.rc = rc;
	}
	
	public void run()
	{
		System.out.println("I am a Tank!");
		
		while (true)
    	{
    		try
    		{
    			
    		} catch (Exception e)
    		{
    			System.out.println("Tank Exception");
    			e.printStackTrace();
    		}
    	}
	}
}
