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
		shootNearestRobot();
		shakeTree();
		
		//move
		move();
		
		//combat
		//shake
		
		closeRoundVars();
	}
	
	boolean shootNearestRobot() {
		RobotInfo ri = SensorMod.getNearestRobot(rc, rc.getTeam().opponent());
		if(ri==null) {
			return false;
		}
		
		if(rc.canFireSingleShot()) {
			try{
				rc.fireSingleShot(loc.directionTo(ri.location));
				return true;
			} catch(Exception e){e.printStackTrace();}
		}
		return false;
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
		
		RobotInfo ri = SensorMod.getNearestRobotNotArchon(rc, rc.getTeam().opponent());
		if(ri!=null) {
			float safeDist = 0;
			if(ri.getType().equals(RobotType.LUMBERJACK)) {
				safeDist = 64f;
			}
			fitness += 1/(l.distanceSquaredTo(ri.location)-safeDist);
			return fitness;
		}
		
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(Team.NEUTRAL)) {
				fitness += ti.containedBullets*1f/l.distanceSquaredTo(ti.location);
			}
		}
		
		fitness -= 0.1f/l.distanceSquaredTo(lastLocation);
		
		return fitness;
	}
	
}
