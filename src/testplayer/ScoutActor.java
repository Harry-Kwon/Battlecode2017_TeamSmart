package testplayer;

import battlecode.common.*;

public class ScoutActor extends RobotActor{
	public ScoutActor(RobotController rc) {
		super(rc);
	}
	
	public void act() {
		if(!rc.isBuildReady()) {
			return;
		}
		updateRoundVars();
		
		//combat
		//shake
		shakeTree();
		
		//move
		move();
		
		//combat
		//shake
		
		closeRoundVars();
	}
	
	boolean shakeTree() {
		TreeInfo nearestTree = SensorMod.getNearestTree(rc, rc.getTeam());
		if(nearestTree==null){
			return false;
		}
		
		if(rc.canShake(nearestTree.ID)) {
			try{
				rc.shake(nearestTree.ID);
				return true;
			} catch(Exception e){e.printStackTrace();}
		}
		return false;
	}
	
	float getFitnessScore(MapLocation l) {
		try{
			if(rc.canSenseLocation(l) && !rc.onTheMap(l)) {
				//System.out.println("dead square");
				return -999999.0f;
			}
		} catch(Exception e){e.printStackTrace();}
		
		float fitness = 0.0f;
		
		for(RobotInfo ri : allRobots) {
		//	fitness -= 1f/l.distanceSquaredTo(ri.location);
		}
		
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(Team.NEUTRAL)) {
				fitness += 1f/l.distanceSquaredTo(ti.location);
			}
		}
		
		fitness -= 10f/l.distanceSquaredTo(lastLocation);
		
		return fitness;
	}
	
}
