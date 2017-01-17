package testplayer;

import battlecode.common.*;
import java.math.*;

public class GardenerActor extends RobotActor {
	
	public GardenerActor(RobotController rc) {
		super(rc);
		lastLocation = rc.getLocation();
	}
	
	boolean anchored = false;
	
	MapLocation anchorLocation;
	MapLocation lastLocation;
	
	boolean builtScout = false;
	
	public void act()  {
		updateRoundVars();
		
		if(!anchored) {
			//rc.canSenseCircle is broken so this is hopefully temporary
			
			if(clearToAnchor()) {
				anchorLocation = loc;
				anchored = true;
				//System.out.println("Anchoring " + rc.getID());
			} else {
				MapLocation target = findTargetLocation();
				moveToLocation(target);
			}
		} else {
			plantTrees();
			waterTrees();
			buildUnits();
		}
		
		closeRoundVars();
	}
	
	boolean buildUnits() {
		//sense for nearby trees
		/*boolean hasTree=false, hasEnemy=false;
		for(TreeInfo ti : allTrees) {
			if(!ti.team.equals(rc.getTeam())) {
				hasTree = true;
				break;
			}
		}
		
		for(RobotInfo ri : allRobots) {
			if(!ri.team.equals(rc.getTeam())) {
				hasEnemy = true;
				break;
			}
		}
		
		if(!(hasTree || hasEnemy)) {
			return false;
		}*/
		
		Direction dir = Direction.getEast().rotateRightDegrees(60f);
		RobotType type = RobotType.LUMBERJACK;
		
		//select robot type
		if(rc.getRobotCount()<100 && !builtScout) {
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

	boolean clearToAnchor() {
		//this can all easily be done with sensecircle when it is fixed
		//all this needs to do right now is sense if anything intersects with LZ
		
		RobotInfo[] smallRobots;
		TreeInfo[] smallTrees;
		
		RobotInfo[] bigRobots;
		TreeInfo[] bigTrees;
		
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

			//bigRobots = rc.senseNearbyRobots(7.1f);
			//bigTrees = rc.senseNearbyTrees(5.1f);
		} catch(Exception e) {e.printStackTrace();}
		
		
		
		
		return true;
	}
}
