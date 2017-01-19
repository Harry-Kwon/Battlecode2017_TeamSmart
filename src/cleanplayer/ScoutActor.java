package cleanplayer;

import battlecode.common.*;

public class ScoutActor extends RobotActor{
	public ScoutActor(RobotController rc) {
		super(rc);
	}
	
	public void robotAct() {
		if(!rc.isBuildReady()) {
			return;
		}
		updateRoundVars();
		
		//BroadcastArchon
		
		//combat
		//shake
		shootNearestRobot();
		shakeTree();
		
		//move
		wander();
		
		//combat
		//shake
		
		closeRoundVars();
	}
	boolean broadcastArchonLoc(){
		RobotInfo ri = sensor.findNearestBotType(rc.getTeam().opponent(),RobotType.ARCHON);
		//potentially add in statement checking repeat broadcast
		if(ri==null){
			return false;
		}
		int x= (int) ri.getLocation().x;
		int y= (int) ri.getLocation().y;
		//Maps technically have integer limits of 3 digits
		//shift x coordinates to the left half of the integer
		int enemyloc =(x*1000)+y;
		try {
			rc.broadcast(1, enemyloc);
			return true;
		} catch (GameActionException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	boolean shootNearestRobot() {
		RobotInfo ri = sensor.getNearestRobot(rc.getTeam().opponent());
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
		TreeInfo nearestTree = sensor.getNearestTree(Team.NEUTRAL);
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
		
		RobotInfo ri = sensor.getNearestRobotNotArchon(rc.getTeam().opponent());
		if(ri!=null) {
			float safeDist = 0;
			//Sets safe distance if Scout sees LUMBERJACK
			if(ri.getType().equals(RobotType.LUMBERJACK)) {
				safeDist = 64f;
			}
			//Sets safe distance if Scout sees SOLDIER
//			if(ri.getType().equals(RobotType.SOLDIER)){
//				safeDist = 64f;
//			}
			
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
