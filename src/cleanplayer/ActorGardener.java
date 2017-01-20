package cleanplayer;

import battlecode.common.*;

public class ActorGardener extends ActorRobot {
	
	public ActorGardener(RobotController rc) {
		super(rc);
		lastLocation = rc.getLocation();
	}
	
	boolean anchored = false;
	
	MapLocation anchorLocation;
	MapLocation lastLocation;
	
	boolean builtScout = false;
	
	public void robotAct()  {
		buildUnits();
		
		if(!anchored) {
			if(tryToAnchor()) {
				anchorLocation = loc;
				anchored = true;
			} else {
				wander();
			}
		} else {
			buildUnits();
			plantTrees();
			waterTrees();
		}
	}
	
	/*
	attempts to anchor and returns true if successful */
	boolean tryToAnchor() {
		RobotInfo[] smallRobots;
		TreeInfo[] smallTrees;
		
		try{
			smallRobots = rc.senseNearbyRobots(4.1f);
			smallTrees = rc.senseNearbyTrees(6.1f);
			
			for(RobotInfo ri : smallRobots) {
				if(ri.type.equals(RobotType.ARCHON)) {
					return false;
				}
			}
			
			for(TreeInfo ti : smallTrees) {
				if(!ti.team.equals(Team.NEUTRAL)) {
					return false;
				}
			}
		} catch(Exception e) {e.printStackTrace();}
		
		return true;
	}
	
	/*
	attempts to build units and returns true if successful*/
	boolean buildUnits() {
		Direction dir = Direction.getEast().rotateRightDegrees(60f);
		RobotType type = RobotType.LUMBERJACK;
		
		//select robot type
		if(rc.getRoundNum()<200 && !builtScout) {
			type = RobotType.SCOUT;
		}
		
		if(rc.canBuildRobot(type, dir)) {
			try{
				rc.buildRobot(type, dir);
				return true;
			} catch(Exception e) {e.printStackTrace();};
		}
		
		return false;
	}
	/*
	attemps to plant trees and returns true if successful*/
	boolean plantTrees() {
		Direction dir = Direction.getEast();
		boolean planted = false;
		float rotationStep = 60f;
		
		for(int i=0; i<((int) 360f/rotationStep)-1; i++){
			
			if(rc.canPlantTree(dir)) {
				try{ 
					rc.plantTree(dir);
					planted = true;
					break;
				} catch(Exception e) {e.printStackTrace();}
			} else {
				dir = dir.rotateLeftDegrees(rotationStep);
			}
		}
		return planted;
	}
	
	/*
	attempts to water trees and returns true if successful*/
	boolean waterTrees() {
		TreeInfo[] myTrees = rc.senseNearbyTrees(1.1f);
		if(myTrees.length == 0) {
			return false;
		}
		
		TreeInfo lowestTree = myTrees[0];
		for(TreeInfo ti : myTrees){ 
			if(!lowestTree.team.equals(rc.getTeam())) {
				lowestTree = ti;
			} else if(ti.health < lowestTree.health && ti.team.equals(rc.getTeam())) {
				lowestTree = ti;
			}
		}
		
		if(rc.canWater(lowestTree.ID) && lowestTree.team.equals(rc.getTeam())) {
			try{
				rc.water(lowestTree.ID);
				return true;
			} catch(Exception e) {e.printStackTrace();}
		}
		
		return false;
	}

	
}
