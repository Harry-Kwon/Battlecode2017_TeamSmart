package testplayer;

import battlecode.common.*;

public class GardenerActor extends RobotActor {
	
	public GardenerActor(RobotController rc) {
		super(rc);
	}
	
	boolean anchored = false;
	RobotInfo[] nearbyRobots;
	TreeInfo[] nearbyTrees;
	MapLocation nearestLocation;
	
	MapLocation anchorLocation;
	
	public void act()  {
		updateRoundVars();
		
		if(!anchored) {
			//rc.canSenseCircle is broken so this is hopefully temporary
			
			if(senseNearbyObjects() == 0) {
				anchorLocation = loc;
				anchored = true;
				System.out.println("Anchoring " + rc.getID());
			} else {
				moveFromLocation(nearestLocation);
			}
		} else {
			if(!plantTrees()) {
				waterTrees();
			}
		}
	}
	
	boolean plantTrees() {
		Direction dir = Direction.getEast();
		boolean planted = false;
		float rotationStep = 60f;
		
		for(int i=0; i<((int) 360f/rotationStep); i++){
			
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
	
	void waterTrees() {
		TreeInfo[] myTrees = rc.senseNearbyTrees(1.1f);
		if(myTrees.length == 0) {
			return;
		}
		
		TreeInfo lowestTree = myTrees[0];
		for(TreeInfo ti : myTrees){ 
			if(ti.health < lowestTree.health) {
				lowestTree = ti;
			}
		}
		
		if(rc.canWater(lowestTree.ID)) {
			try{
				rc.water(lowestTree.ID);
			} catch(Exception e) {e.printStackTrace();}
		}
	}
	
	int senseNearbyObjects() {
		try{
			nearbyRobots = rc.senseNearbyRobots(2.6f);
			nearbyTrees = rc.senseNearbyTrees(2.6f);
		} catch(Exception e) {e.printStackTrace();}
		
		if(nearbyRobots.length == 0) {
			if(nearbyTrees.length == 0) {
				return 0;
			} else {
				nearestLocation = nearbyTrees[0].location;
			}
		} else {
			nearestLocation = nearbyRobots[0].location;
		}
		
		//find nearest object
		for(RobotInfo ri : nearbyRobots) {
			if(loc.distanceSquaredTo(ri.location) < loc.distanceSquaredTo(nearestLocation)) {
				nearestLocation = ri.location;
			}
		}
		
		for(TreeInfo ti : nearbyTrees) {
			if(loc.distanceSquaredTo(ti.location) < loc.distanceSquaredTo(nearestLocation)){
				nearestLocation = ti.location;
			}
		}
		
		return(nearbyRobots.length + nearbyTrees.length);
		
	}
}
