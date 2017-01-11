package testplayer;

import battlecode.common.*;
import java.math.*;

public class GardenerActor extends RobotActor {
	
	public GardenerActor(RobotController rc) {
		super(rc);
		lastLocation = rc.getLocation();
	}
	
	boolean anchored = false;
	
	RobotInfo[] allRobots;
	TreeInfo[] allTrees;
	
	MapLocation anchorLocation;
	MapLocation lastLocation;
	
	public void act()  {
		updateRoundVars();
		
		if(!anchored) {
			//rc.canSenseCircle is broken so this is hopefully temporary
			
			if(clearToAnchor()) {
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
		
		lastLocation = loc;
	}
	
	void senseAll() {
		allRobots = rc.senseNearbyRobots();
		allTrees = rc.senseNearbyTrees();
	}
	
	MapLocation findTargetLocation() {
		Direction angle = findTargetAngle();
		MapLocation target = findTargetLocation(angle);
		
		System.out.println(target);
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
			//System.out.println(angle);
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
		try{
			if(rc.canSenseLocation(l) && !rc.onTheMap(l)) {
				System.out.println("dead square");
				return -999999.0f;
			}
		} catch(Exception e){e.printStackTrace();}
		
		float fitness = 0.0f;
		
		for(RobotInfo ri : allRobots) {
			fitness -= 1f/l.distanceSquaredTo(ri.location);
		}
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(Team.NEUTRAL)) {
				fitness -= 1f/l.distanceSquaredTo(ti.location);
			}
		}
		
		fitness -= 10f/l.distanceSquaredTo(lastLocation);
		
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
			if(!lowestTree.team.equals(rc.getTeam())) {
				lowestTree = ti;
			} else if(ti.health < lowestTree.health && ti.team.equals(rc.getTeam())) {
				lowestTree = ti;
			}
		}
		
		if(rc.canWater(lowestTree.ID) && lowestTree.team.equals(rc.getTeam())) {
			try{
				rc.water(lowestTree.ID);
			} catch(Exception e) {e.printStackTrace();}
		}
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
			
			System.out.println(smallRobots.length + smallTrees.length);
			
			for(RobotInfo ri : smallRobots) {
				if(ri.type.equals(RobotType.ARCHON)) {
					System.out.println(false);
					return false;
				}
			}
			
			for(TreeInfo ti : smallTrees) {
				if(!ti.team.equals(Team.NEUTRAL)) {
					System.out.println(false);
					return false;
				}
			}

			//bigRobots = rc.senseNearbyRobots(7.1f);
			//bigTrees = rc.senseNearbyTrees(5.1f);
		} catch(Exception e) {e.printStackTrace();}
		
		
		
		
		return true;
	}
}
