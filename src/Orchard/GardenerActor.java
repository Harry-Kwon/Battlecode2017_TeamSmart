package Orchard;

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
	
	MapLocation lastLocation;
	
	public void act()  {
		updateRoundVars();
		senseAll();
		
		//water trees
		if(water()) {
			return;
		}
		
		//immediate plant
		if(plant()) {
			return;
		}

		//moveToBestTree();
		
		/*
		//check urgent water
		if(urgentWater) {
			return;
		}
		
		//move and plant
		if(moveToPlant()) {
			return;
		}
		
		//make units
		if(makeUnits()) {
			return;
		}
		
		//move and water
		if(moveToWater) {
			return;
		}
		*/
		lastLocation = loc;
	}
	
	void senseAll() {
		allRobots = rc.senseNearbyRobots();
		allTrees = rc.senseNearbyTrees();
	}
	
	boolean plant() {
		float rotateStep = 30f;
		Direction bestAngle = Direction.getEast();
		Direction angle = bestAngle;
		float bestScore = 0.0f;
		
		for(int i=0; i<360/rotateStep;i++) {
			angle = angle.rotateLeftDegrees(rotateStep);
			
			float score = treeLocationScore(loc.add(angle, 1f));
			if(score > bestScore) {
				bestScore = score;
				bestAngle = angle;
			}
		}
		
		if(bestScore >0f && rc.canPlantTree(bestAngle)) {
			try {
				rc.plantTree(bestAngle);
				return true;
			} catch (Exception e) {e.printStackTrace();}
		}
		
		return false;
	}
	
	float treeLocationScore(MapLocation l) {
		try{
			if(!rc.onTheMap(l)) {
				return -1f;
			}
		} catch(Exception e){e.printStackTrace();}
		
		if(!rc.canSenseAllOfCircle(l, 1f)){
			return -1f;
		}
		try{
			if(rc.isCircleOccupiedExceptByThisRobot(l, 1f)) {
				return -1f;
			}
		}catch(Exception e){e.printStackTrace();}
		
		
		float treeScore = 0.1f;
		for(TreeInfo ti : allTrees) {
			if(ti.getTeam() != rc.getTeam()) {
				continue;
			}
			float dist = ti.location.distanceSquaredTo(l);
			if(dist < 16.1f) {
				return -1f;
			} else {
				treeScore += ti.location.distanceSquaredTo(l)-16.1f;
			}
		}

		return treeScore;
	}
	
	boolean water() {
		if(allTrees.length == 0) {
			return false;
		}
		
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 99999999f;
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(rc.getTeam()) && ti.health <= ti.maxHealth-5f) {
				float dist = loc.distanceSquaredTo(ti.location);
				System.out.println("water" + ", "+dist);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestTree = ti;
				}
			}
		}
		
		if(nearestTree.team.equals(rc.getTeam()) && nearestTree.health<=nearestTree.maxHealth) {
			if(nearestDist <= 4.0) {
				if(rc.canWater(nearestTree.location)) {
					try{
						rc.water(nearestTree.location);
						return true;
					} catch(Exception e){e.printStackTrace();}
				} else {
					moveToLocation(nearestTree.location);
					return true;
				}
			}
		}
		
		return false;
	}
	
	void moveIdle() {
		MapLocation target = findTarget();
		moveToLocation(target);
	}
	
	void moveToBestTree() {
		if(allTrees.length==0) {
			return;
		}
		
		TreeInfo bestTree = allTrees[0];
		float bestScore = 0f;
		for(TreeInfo i : allTrees) {
			if(!i.team.equals(rc.getTeam())) {continue;}
			
			float score = 0f;
			for(TreeInfo j : allTrees) {
				if(!j.team.equals(rc.getTeam())) {continue;}
				score += j.location.distanceSquaredTo(i.location);
			}
			
			if(score >= bestScore) {
				bestScore = score;
				bestTree = i;
			}			
		}
		
		if(bestTree.team.equals(rc.getTeam())) {
			moveToLocation(bestTree.location);
		} else {
			return;
		}
	}
	
	MapLocation findTarget() {
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
				//System.out.println("dead square");
				return -999999.0f;
			}
		} catch(Exception e){e.printStackTrace();}
		
		float fitness = 0.0f;
		
		for(RobotInfo ri : allRobots) {
			if(ri.type.equals(RobotType.ARCHON) && l.distanceSquaredTo(ri.location)<25f) {
				fitness -= 1f;
			}
		}
		
		for(TreeInfo ti : allTrees) {
			if(!ti.team.equals(Team.NEUTRAL)) {
				if(l.distanceSquaredTo(ti.location) < 1.0f) {
					fitness += 1f;
				}
			}
		}
		
		//fitness -= 10f/l.distanceSquaredTo(lastLocation);
		
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
