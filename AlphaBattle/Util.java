package AlphaBattle;
import battlecode.common.*;

public class Util {

	/**
     * Returns a random Direction
     * @return a random Direction
     */
    public static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }
    
    
}
