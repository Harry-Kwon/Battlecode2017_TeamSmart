package testplayer;

import battlecode.common.*;
import java.math.*;

public class GardenerActor extends RobotActor {
	
	public GardenerActor(RobotController rc) {
		super(rc);
	}
	
	boolean anchored = false;
	RobotInfo[] nearbyRobots;
	TreeInfo[] nearbyTrees;
	
	RobotInfo[] allRobots;
	TreeInfo[] allTrees;
	
	MapLocation nearestLocation;
	
	MapLocation anchorLocation;
	
	public void act()  {
		updateRoundVars();
		
		if(!anchored) {
			//rc.canSenseCircle is broken so this is hopefully temporary
			
			if(senseNearbyObjects() == 0) {
				anchorLocation = loc;
				anchored = true;
				//System.out.println("Anchoring " + rc.getID());
			} else {
				senseAll();
				MapLocation target = findTargetLocation();
				moveToLocation(target);
			}
		} else {
			if(!plantTrees()) {
				waterTrees();
			}
		}
	}
	
	void senseAll() {
		allRobots = rc.senseNearbyRobots();
		allTrees = rc.senseNearbyTrees();
	}
	
	MapLocation findTargetLocation() {
		Direction angle = findTargetAngle();
		MapLocation target = findTargetLocation(angle);
		
		return target;
	}
	
	MapLocation findTargetLocation(Direction dir) {
		MapLocation target = loc.add(dir, sensorRange/2f);
		
		float startRange = 0f;
		float endRange = sensorRange;
		
		for(int i=0; i<8; i++) {
			float startScore = getFitnessScore(loc.add(dir, startRange));
			float endScore = getFitnessScore(loc.add(dir, endRange));
			
			if(startScore > endScore) {
				endRange = (startRange+endRange)/2;
			} else {
				startRange = (startRange+endRange)/2;
			}
			
			target = loc.add(dir, (startRange+endRange/2));
		}
		
		return target;
	}
	
	//angular binary search
	Direction findTargetAngle() {
		Direction angle = Direction.getWest();
		float rad = sensorRange/2f;
		System.out.println("RAD" + rad);
		float scope = 90f;
		
		for(int i=0; i<8; i++) {
			
			MapLocation D1 = loc.add(angle.rotateRightDegrees(scope), rad);
			MapLocation D2 = loc.add(angle.rotateLeftDegrees(scope), rad);
			
			float D1Score = getFitnessScore(D1);
			float D2Score = getFitnessScore(D2);
			System.out.println(angle);
			//System.out.println(D1 + ", " + D2);
			//System.out.println(D1Score + ", " + D2Score);
			
			if(D1Score > D2Score) {
				angle = angle.rotateRightDegrees(scope);
			} else {
				angle = angle.rotateLeftDegrees(scope);
			}
			
			scope/=2f;
		}
		
		System.out.println(angle.getAngleDegrees());
		return angle;
	}
	
	float getFitnessScore(MapLocation l) {
		if(!rc.canSenseLocation(l)) {
			return -999999.0f;
		}
		
		float fitness = 0.0f;
		
		for(RobotInfo ri : allRobots) {
			fitness -= 1f/l.distanceSquaredTo(ri.location);
		}
		for(TreeInfo ti : allTrees) {
			fitness -= 1f/l.distanceSquaredTo(ti.location);
		}
		
		return fitness;
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
		//this can all easily be done with sensecircle when it is fixed
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
