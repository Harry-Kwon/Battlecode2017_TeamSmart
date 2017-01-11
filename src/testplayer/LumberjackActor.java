package testplayer;

import battlecode.common.*;

public class LumberjackActor extends RobotActor {
	
	public LumberjackActor(RobotController rc) {
		super(rc);
	}
	
	public void act() {
		updateRoundVars();
		
		if(!attackEnemy()) {
			if(!cutTrees()) {
				wander();
			}
		}
		
		closeRoundVars();
	}
	
	boolean attackEnemy() {
		
		if(allRobots.length ==0) {
			return false;
		}
		
		RobotInfo nearestRobot = allRobots[0];
		float nearestDist = 999999f;
		for(RobotInfo ri : allRobots) {
			if(!ri.team.equals(rc.getTeam())) {
				System.out.println(nearestRobot.getType());
				float dist = loc.distanceSquaredTo(ri.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = ri;
				}
			}
		}
		
		
		if(!nearestRobot.team.equals(rc.getTeam())) {
			System.out.println(nearestRobot.getType());
			if(nearestDist < (1.5f+nearestRobot.getRadius())*(1.5f+nearestRobot.getRadius())){
				try{
					rc.strike();
					return true;
				} catch(Exception e){e.printStackTrace();}
			} else {
				moveToLocation(nearestRobot.location);
				return true;
			}
		}
		return false;
	}
	
	boolean cutTrees() {
		if(allTrees.length ==0) {
			return false;
		}
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 999999f;
		for(TreeInfo ti : allTrees) {
			if(!ti.team.equals(rc.getTeam())) {
				float dist = loc.distanceSquaredTo(ti.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestTree = ti;
				}
			}
		}
		
		
		if(!nearestTree.team.equals(rc.getTeam())) {
			if(rc.canShake(nearestTree.location)) {
				try{
					rc.shake(nearestTree.location);
				} catch(Exception e) {e.printStackTrace();}
			}
			
			if(rc.canChop(nearestTree.location)){
				try{
					rc.chop(nearestTree.location);
					return true;
				} catch(Exception e){e.printStackTrace();}
			} else {
				moveToLocation(nearestTree.location);
				return true;
			}
		}
		return false;
	}
	
	void wander() {
		MapLocation target = findTargetLocation();
		moveToLocation(target);
	}
}
