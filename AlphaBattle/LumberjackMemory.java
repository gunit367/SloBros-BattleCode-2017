package AlphaBattle;
import battlecode.common.*;

public class LumberjackMemory extends RobotMemory
{
	static float attackRange = RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
	final float MIN_TREE_RADIUS = 1.1f;
	
	TreeInfo bestTree;
	public boolean isChopping;
	
	
	public LumberjackMemory(RobotController rc)
	{
		super(rc);
		isChopping = false;
	}
	
	void findBestTree(TreeInfo[] trees)
	{
		float largest = trees[0].radius;
		int index = 0;
		for(int i = 0; i < trees.length; i++)
		{
			if (trees[i].radius > MIN_TREE_RADIUS && trees[i].radius > largest && trees[i].team != rc.getTeam())
			{
				largest = trees[i].radius;
				index = i;
			}
		}
		bestTree = trees[index];
	}
	
	public void updateMemory()
	{
		super.updateMemory();
		TreeInfo[] neutral = rc.senseNearbyTrees(-1, Team.NEUTRAL);
		if (trees.length == 0)
		{
			bestTree = null;
			return;
		}
		if (bestTree != null && rc.canSenseTree(bestTree.ID))
			return; // no need to reupdate best tree
		
		isChopping = false;
		TreeInfo[] enemyTrees = rc.senseNearbyTrees(-1, rc.getTeam().opponent());
		if(enemyTrees.length > 0)
			findBestTree(enemyTrees);
		else if (neutral.length > 0)
			findBestTree(neutral);
		else
			bestTree = null;
	}
	
	public MapLocation getImportantLoc()
	{
		return null;
	}
	
	public boolean shouldAttack()
	{
		if(alliesInRange.length == 0 && enemiesInRange.length > 0)
			return true;
		return false;
	}
	
	public boolean shouldChop()
	{
		return trees.length > 0;
	}
	
	public boolean shouldMove() {
		return !isChopping;
	}
	
}
