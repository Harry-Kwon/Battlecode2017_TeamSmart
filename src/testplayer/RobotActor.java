package testplayer;

import battlecode.common.*;

public class RobotActor {
	
	public RobotController rc;
	public MapLocation loc;
	float sensorRange;
	
	RobotInfo[] allRobots;
	TreeInfo[] allTrees;
	MapLocation lastLocation;
	
	public RobotActor(RobotController rc){
		this.rc = rc;
		this.sensorRange = rc.getType().sensorRadius;
		this.loc = rc.getLocation();
		lastLocation = loc;
	}
	
	public void act() {
		//
	}
	
	public void updateRoundVars() {
		this.loc = rc.getLocation();
		senseAll();
	}
	
	public void closeRoundVars() {
		lastLocation = loc;
	}
	
	void senseAll() {
		allRobots = rc.senseNearbyRobots();
		allTrees = rc.senseNearbyTrees();
	}
	
	/*boolean robotIsSurrounded(RobotInfo r) {
		
	}*/
	
	//Nav Code
	
	MapLocation findTargetLocation() {
		Direction angle = findTargetAngle();
		MapLocation target = findTargetLocation(angle);
		
		//System.out.println(target);
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
		//System.out.println("RAD" + rad);
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
		
		//System.out.println(angle.getAngleDegrees());
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
	
	
	//binary search-ish, 4 iterations for now (360/2^4 = 22.5)
	public void moveToLocation(MapLocation l) {
		if(rc.hasMoved()) {
			return;
		}
		if(rc.canMove(l)) {
			try{
				rc.move(l);
			} catch(Exception e) {e.printStackTrace();}
		} else {
			Direction dir = loc.directionTo(l);
			moveInDirection(dir);
		}
		
		
		
	}
	
	public void moveFromLocation(MapLocation l) {
		Direction dir = l.directionTo(loc);
		
		if(rc.canMove(dir)) {
			try{
				rc.move(dir);
				return;
			} catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public void moveInDirection(Direction dir) {
		if(rc.hasMoved()) {
			return;
		}
		
		float turnStep = 3f;
		Direction d = dir;
		for(int i=0; i<(int)(180f/turnStep); i++) {
			Direction right = d.rotateLeftDegrees(turnStep*((float) i));
			Direction left = d.rotateRightDegrees(turnStep*((float) i));
			if(rc.canMove(right)) {
				try{
					//System.out.println("moved");
					rc.move(right);
					return;
				} catch(Exception e) {e.printStackTrace();}
			} else if(rc.canMove(left)) {
				try{
					//System.out.println("moved");
					rc.move(left);
					return;
				} catch(Exception e) {e.printStackTrace();}
			} else {
				d = d.rotateLeftDegrees(turnStep);
				//System.out.println(i + ", " + d.getAngleDegrees());
			}
		}
		
		
	}
}
