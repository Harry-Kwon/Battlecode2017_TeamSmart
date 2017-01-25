package cleanplayer;

import battlecode.common.*;

public class ActorGardener extends BaseActor {
	
	public ActorGardener(RobotController rc) {
		super(rc);
		lastLocation = rc.getLocation();
	}
	
	boolean anchored = false;
	int buildNum = 0;
	
	MapLocation anchorLocation;
	MapLocation lastLocation;
	
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
			if(sensor.findNearestRobot(team.opponent()) == null || rc.getTeamBullets()>100) {
				plantTrees();
			}
			waterTrees();
		}
	}
	
	/*
	attempts to anchor and returns true if successful */
	boolean tryToAnchor() {
		
		try{
			
			for(RobotInfo ri : allRobots) {
				if(ri.team.equals(team)){
					if(ri.type.equals(RobotType.ARCHON)) {
						if(loc.distanceTo(ri.location) < type.bodyRadius + 2f + ri.getRadius()) {
							return false;
						}
					}
					if(ri.type.equals(RobotType.GARDENER)) {
						if(loc.distanceTo(ri.location) < type.bodyRadius*2f + 2f + GameConstants.BULLET_TREE_RADIUS*2f*2f){
							return false;
						}
					}
				}
			}
			
			/*RobotInfo[] smallRobots;
			TreeInfo[] smallTrees;
			smallRobots = rc.senseNearbyRobots(4.1f);
			smallTrees = rc.senseNearbyTrees(6.1f);
			
			for(RobotInfo ri : smallRobots) {
				return false;
			}
			
			for(TreeInfo ti : smallTrees) {
				if(!ti.team.equals(Team.NEUTRAL)) {
					return false;
				}
			}*/
		} catch(Exception e) {e.printStackTrace();}
		
		return true;
	}
	
	/*
	attempts to build units and returns true if successful*/

	boolean buildUnits() {
		Direction dir = Direction.getEast().rotateRightDegrees(60f);
		RobotType type = RobotType.SOLDIER;
		
		//select robot type
		if(buildNum==0) {
			//type = RobotType.SCOUT;
		} else if(buildNum%5==1) {
			type = RobotType.LUMBERJACK;
		}
		
		if(rc.canBuildRobot(type, dir)) {
			try{
				rc.buildRobot(type, dir);
				buildNum++;
				return true;
			} catch(Exception e) {e.printStackTrace();};
		}
		
		return false;
	}
	/*
	attempts to plant trees and returns true if successful*/
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
		TreeInfo[] myTrees = rc.senseNearbyTrees(1.1f, rc.getTeam());
		if(myTrees.length == 0) {
			return false;
		}
		
		TreeInfo lowestTree = UtilTrees.findLowestTree(myTrees);
		
		if(rc.canWater(lowestTree.ID)) {
			try{
				rc.water(lowestTree.ID);
				return true;
			} catch(Exception e) {e.printStackTrace();}
		}
		
		return false;
	}
	
}
