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
				spreadout();
			}
		} else {
			if(!plantTrees()) {
				waterTrees();
			}
		}
	}
	
	void spreadout() {
		RobotInfo[] robots = rc.senseNearbyRobots();
		TreeInfo[] trees = rc.senseNearbyTrees();
		
		float[] dirVector = {0, 0};
		
		for(RobotInfo ri : robots) {
			float dx = loc.x - ri.location.x;
			float dy = loc.y - ri.location.y;
			float mod = 1f;
			if(ri.type.equals(RobotType.ARCHON)) {
				mod = 1f;
			}
			
			if(dx != 0f) {
				if(dx<0f) { //there must be an easier way. dx/Math.abs(dx) doesn't work?
					dirVector[0] += -1*(10-Math.abs(dx))*mod;
				} else {
					dirVector[0] += (10-Math.abs(dx))*mod;
				}
			}
			if(dy != 0f) {
				if(dy<0f) {
					dirVector[0] += -1*(10-Math.abs(dy))*mod;
				} else {
					dirVector[0] += (10-Math.abs(dy))*mod;
				}
			}
		}
		
		/*for(TreeInfo ti : trees) {
			float dx = loc.x - ti.location.x;
			float dy = loc.y - ti.location.y;
			float mod = 0f;
			if(ti.team.equals(Team.NEUTRAL)) {
				mod = 1f;
			}
			
			if(dx != 0f) {
				if(dx<0f) {
					dirVector[0] += -1*(10-Math.abs(dx))*mod;
				} else {
					dirVector[0] += (10-Math.abs(dx))*mod;
				}
			}
			if(dy != 0f) {
				if(dy<0f) {
					dirVector[0] += -1*(10-Math.abs(dy))*mod;
				} else {
					dirVector[0] += (10-Math.abs(dy))*mod;
				}
			}
		}*/
		//System.out.println("direction" + rc.getID() + "[" + dirVector[0] + ", " + dirVector[1] + "]");
		//System.out.println(new Direction(dirVector[0], dirVector[1]).getAngleDegrees());
		
		moveInDirection(new Direction(dirVector[0], dirVector[1]));
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
			nearbyRobots = rc.senseNearbyRobots(4.1f);
			nearbyTrees = rc.senseNearbyTrees(3.1f);
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
