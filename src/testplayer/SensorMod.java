package testplayer;

import battlecode.common.*;

public class SensorMod {
	
	public static boolean isRobotSurrounded(RobotController rc, RobotInfo r) {
		boolean surrounded = true;
		Direction angle = Direction.getEast();
		float rotateStep = 30;
		
		for(int i=0; i<360/rotateStep; i++) {
			MapLocation center = r.location.add(angle.rotateLeftDegrees(rotateStep*i), r.getRadius()+rc.getType().bodyRadius+0.1f);
			if(rc.canSenseAllOfCircle(center, rc.getType().bodyRadius)) {
				try{
					if(!rc.isCircleOccupiedExceptByThisRobot(center, rc.getType().bodyRadius)) {
						return false;
					}
				} catch(Exception e){e.printStackTrace();}
			}
		}
		
		return surrounded;
	}
	
	public static boolean isTreeSurrounded(RobotController rc, TreeInfo t) {
		boolean surrounded = true;
		Direction angle = Direction.getEast();
		float rotateStep = 30f;
		
		//System.out.println("T" + t.location);
		for(int i=0; i<360/rotateStep; i++) {
			MapLocation center = t.location.add(angle.rotateLeftDegrees(rotateStep*i), t.getRadius()+rc.getType().bodyRadius+0.1f);
			//System.out.println("center" + center);
			if(rc.canSenseAllOfCircle(center, rc.getType().bodyRadius)) {
				try{
					if(!rc.isCircleOccupiedExceptByThisRobot(center, rc.getType().bodyRadius)) {
						return false;
					}
				} catch(Exception e){e.printStackTrace();}
			}
		}
		
		return surrounded;
	}
	
	public static TreeInfo getNearestTree(RobotController rc, Team team) {
		TreeInfo[] allTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, team);
		
		if(allTrees.length ==0) {
			return null;
		}
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 999999f;
		
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(team)) {
				float dist = rc.getLocation().distanceSquaredTo(ti.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestTree = ti;
				}
			}
		}
		return(nearestTree);
	}
	
	public static RobotInfo getNearestRobot(RobotController rc, Team team) {
		RobotInfo[] allRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, team);
		
		if(allRobots.length ==0) {
			return null;
		}
		RobotInfo nearestRobot = allRobots[0];
		float nearestDist = 999999f;
		
		for(RobotInfo ri : allRobots) {
			if(ri.team.equals(team)) {
				float dist = rc.getLocation().distanceSquaredTo(ri.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = ri;
				}
			}
		}
		return(nearestRobot);
	}
	
	public static RobotInfo getNearestRobotNotArchon(RobotController rc, Team team) {
		RobotInfo[] allRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, team);
		
		if(allRobots.length ==0) {
			return null;
		}
		RobotInfo nearestRobot = allRobots[0];
		float nearestDist = 999999f;
		
		for(RobotInfo ri : allRobots) {
			if(ri.team.equals(team) && !ri.type.equals(RobotType.ARCHON)) {
				float dist = rc.getLocation().distanceSquaredTo(ri.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = ri;
				}
			}
		}
		
		if(nearestRobot.type.equals(RobotType.ARCHON)) {
			return null;
		}
		return(nearestRobot);
	}
	//Generic search method that will find nearest bot of a certain type 
	public static RobotInfo findNearestBotType(RobotController rc, Team team,RobotType robit){
		RobotInfo[] allRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, team);
		float nearestDist = 999999f;
		RobotInfo nearestRobot = allRobots[0];
		//Saves index to not repeat values
		int x=0;
		for(int i=0;i<allRobots.length;i++){
			if(allRobots[i].type==robit)
			{
				nearestRobot = allRobots[0];
				break;
			}
			x++;
		}
		for(int i=x;i<allRobots.length;i++){
			if(allRobots[i].type.equals(robit)){
				float dist = rc.getLocation().distanceSquaredTo(allRobots[i].location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = allRobots[i];
				}
			}
		}
		return nearestRobot;
	}
}
