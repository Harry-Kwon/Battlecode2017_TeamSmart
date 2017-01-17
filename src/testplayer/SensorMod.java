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
}
