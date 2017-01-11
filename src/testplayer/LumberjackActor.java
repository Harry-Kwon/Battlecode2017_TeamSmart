package testplayer;

import battlecode.common.*;

public class LumberjackActor extends RobotActor {
	
	public LumberjackActor(RobotController rc) {
		super(rc);
	}
	
	public void act() {
		updateRoundVars();
		
		if(attackEnemyUnits()){
			//System.out.println("attack");
		} else if(attackEnemyWorkers()) {
			//System.out.println("cut enemy trees");
		} else if(cutEnemyTrees()) {
			//System.out.println("attack workers");
		} else if(cutTrees()) {
			//System.out.println("cut trees");
		} else {
			wander();
			//System.out.println("attack");
		}
		
		closeRoundVars();
	}
	
	boolean attackEnemyUnits() {
		if(allRobots.length ==0) {
			return false;
		}
		
		RobotInfo nearestRobot = allRobots[0];
		float nearestDist = 999999f;
		for(RobotInfo ri : allRobots) {
			if(!ri.team.equals(rc.getTeam()) && !ri.type.equals(RobotType.GARDENER) && !ri.type.equals(RobotType.ARCHON)) {
				float dist = loc.distanceSquaredTo(ri.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = ri;
				}
			}
		}
		
		
		if(!nearestRobot.team.equals(rc.getTeam()) && !nearestRobot.type.equals(RobotType.GARDENER) && !nearestRobot.type.equals(RobotType.ARCHON)) {
			if(nearestDist < (1.5f+nearestRobot.getRadius())*(1.5f+nearestRobot.getRadius())){
				try{
					rc.strike();
					return true;
				} catch(Exception e){e.printStackTrace();}
			} else if(robotIsSurrounded(nearestRobot)) {
				return false;
			} else {
				moveToLocation(nearestRobot.location);
				return true;
			}
		}
		return false;
	}
	
	boolean attackEnemyWorkers() {
		if(allRobots.length ==0) {
			return false;
		}
		
		RobotInfo nearestRobot = allRobots[0];
		float nearestDist = 999999f;
		for(RobotInfo ri : allRobots) {
			if(!ri.team.equals(rc.getTeam()) && (ri.type.equals(RobotType.GARDENER) || ri.type.equals(RobotType.ARCHON))) {
				float dist = loc.distanceSquaredTo(ri.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = ri;
				}
			}
		}
		
		
		if(!nearestRobot.team.equals(rc.getTeam()) && (nearestRobot.type.equals(RobotType.GARDENER) || nearestRobot.type.equals(RobotType.ARCHON))) {
			if(nearestDist < (2f+nearestRobot.getRadius())*(2f+nearestRobot.getRadius())){
				try{
					rc.strike();
					return true;
				} catch(Exception e){e.printStackTrace();}
			} else if(robotIsSurrounded(nearestRobot)) {
				System.out.println("surrounded");
				return false;
			} else {
				moveToLocation(nearestRobot.location);
				return true;
			}
		}
		return false;
	}
	
	boolean cutEnemyTrees() {
		// cut closest lowest health tree. lowest health prioritized
		if(allTrees.length ==0) {
			return false;
		}
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 999999f;
		float lowestHealth = 999999f;
		
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(rc.getTeam().opponent())) {
				float dist = loc.distanceSquaredTo(ti.location);
				if(ti.health < lowestHealth) {
					lowestHealth = ti.health;
					nearestDist = dist;
					nearestTree = ti;
				} else if(ti.health == lowestHealth) {
					if(dist < nearestDist) {
						nearestDist = dist;
						nearestTree = ti;
					}
				}	
			}
		}
		
		
		if(nearestTree.team.equals(rc.getTeam().opponent())) {
			if(rc.canChop(nearestTree.location)){
				try{
					rc.chop(nearestTree.location);
					return true;
				} catch(Exception e){e.printStackTrace();}
			} else if(treeIsSurrounded(nearestTree)) {
				return false;
			} else {
				moveToLocation(nearestTree.location);
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
		float lowestHealth = 999999f;
		for(TreeInfo ti : allTrees) {
			if(!ti.team.equals(rc.getTeam())) {
				float dist = loc.distanceSquaredTo(ti.location);
				if(ti.health < lowestHealth) {
					lowestHealth = ti.health;
					nearestDist = dist;
					nearestTree = ti;
				} else if(ti.health == lowestHealth) {
					if(dist < nearestDist) {
						nearestDist = dist;
						nearestTree = ti;
					}
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
			} else if(treeIsSurrounded(nearestTree)) {
				return false;
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
