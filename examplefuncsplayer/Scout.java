package examplefuncsplayer;
import battlecode.common.*;

public class Scout {

	static RobotController rc;
	
	public Scout(RobotController rc)
	{
		Scout.rc = rc;
	}
	
	public void run()
	{
		System.out.println("I am a Scout!");
		
		while (true)
    	{
    		try
    		{
    			
    		} catch (Exception e)
    		{
    			System.out.println("Scout Exception");
    			e.printStackTrace();
    		}
    	}
	}
}
