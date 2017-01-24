package cleanplayer;

import battlecode.common.*;

public class ActorScout extends BaseActorShooter {
	public ActorScout(RobotController rc) {
		super(rc);
	}
	
	public void robotAct() {
		//BroadcastArchon
		System.out.println("a" + Clock.getBytecodeNum());
		//shake
		shakeTree();
		
		//combat
		shootNearestRobot();
		
		//movement
		move();
		System.out.println("b" + Clock.getBytecodeNum());
	}
	
	public void move() {
		
		if(nav.moveAroundEnemies()	) {
			//moved around enemies
		} else if(nav.moveToNearestFullNeutralTree()) {
			//moved to nearest neutral tree
		} else if(nav.moveToBroadcastChannel()) {
			//moved to broadcast
		} else {
			wander();
		}
	}
	
	
	boolean broadcastArchonLoc(){
		RobotInfo ri = sensor.findNearestBotType(rc.getTeam().opponent(),RobotType.ARCHON);
		if(ri==null) {
			return false;
		}
		
		//potentially add in statement checking repeat broadcast
		if(broadcast.broadcastLocation(ri.location, 1)) {
			return true;
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
		
		RobotInfo ri = sensor.findNearestRobotNotArchon(rc.getTeam().opponent());
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
