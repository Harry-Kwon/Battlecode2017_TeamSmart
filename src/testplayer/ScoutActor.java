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
		TreeInfo nearestTree = SensorMod.getNearestTree(rc, Team.NEUTRAL);
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
		
		RobotInfo ri = SensorMod.getNearestRobot(rc, rc.getTeam().opponent());
		if(ri!=null) {
			float distance = rc.getType().bodyRadius+ri.getRadius();
			if(ri.type.equals(RobotType.LUMBERJACK)) {
				distance += 2;
			}
			fitness += Math.pow(loc.x-ri.location.x, 2)+Math.pow(loc.y-ri.location.y, 2)-Math.pow(distance, 2);
		}
		
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(Team.NEUTRAL)) {
				fitness += ti.containedBullets*1f/l.distanceSquaredTo(ti.location);
			}
		}
		
		//fitness -= 0.1f/l.distanceSquaredTo(lastLocation);
		
		return fitness;
	}
	
}
