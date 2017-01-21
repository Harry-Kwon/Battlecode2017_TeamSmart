package cleanplayer;

import battlecode.common.*;

public class ModSensor {
	
	RobotController rc;
	BaseActor ra;
	float sensorRange;
	
	public ModSensor(BaseActor ra, RobotController rc) {
		this.ra = ra;
		this.rc = rc;
		sensorRange = rc.getType().sensorRadius;
	}
	
	public boolean isRobotSurrounded(RobotInfo r) {
		boolean surrounded = true;
		Direction angle = Direction.getEast();
		float rotateStep = 30;
		
		for(int i=0; i<360/rotateStep; i++) {
			MapLocation center = r.location.add(angle.rotateLeftDegrees(rotateStep*i), r.getRadius()+rc.getType().bodyRadius+0.1f);
			if(rc.canSenseAllOfCircle(center, rc.getType().bodyRadius)) {
				try{
					if(!rc.isCircleOccupiedExceptByThisRobot(center, ra.type.bodyRadius)) {
						return false;
					}
				} catch(Exception e){e.printStackTrace();}
			}
		}
		
		return surrounded;
	}
	
	public boolean isTreeSurrounded(TreeInfo t) {
		boolean surrounded = true;
		Direction angle = Direction.getEast();
		float rotateStep = 30f;
		
		//System.out.println("T" + t.location);
		for(int i=0; i<360/rotateStep; i++) {
			MapLocation center = t.location.add(angle.rotateLeftDegrees(rotateStep*i), t.getRadius()+ra.type.bodyRadius+0.1f);
			//System.out.println("center" + center);
			if(rc.canSenseAllOfCircle(center, ra.type.bodyRadius)) {
				try{
					if(!rc.isCircleOccupiedExceptByThisRobot(center, ra.type.bodyRadius)) {
						return false;
					}
				} catch(Exception e){e.printStackTrace();}
			}
		}
		
		return surrounded;
	}
	
	public TreeInfo findLowestNearestTree(Team team) {
		TreeInfo[] allTrees = rc.senseNearbyTrees(ra.type.sensorRadius, team);
		if(allTrees.length ==0) {
			return null;
		}
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 999999f;
		float lowestHealth = 999999f;
		
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(rc.getTeam().opponent())) {
				float dist = ra.loc.distanceSquaredTo(ti.location);
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
		
		return(nearestTree);
	}
	
	public TreeInfo findNearestFullNeutralTree() {
		TreeInfo[] allTrees = rc.senseNearbyTrees(ra.type.sensorRadius, Team.NEUTRAL);
		if(allTrees.length ==0) {
			return null;
		}
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 999999f;
		
		for(TreeInfo ti : allTrees) {
			if(ti.getContainedBullets()>0) {
				float dist = ra.loc.distanceSquaredTo(ti.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestTree = ti;
				}
			}
		}
		
		if(!(nearestTree.getContainedBullets()>0)) {
			return null;
		}
		
		return(nearestTree);
	}
	
	public TreeInfo findNearestTree(Team team) {
		TreeInfo[] allTrees = rc.senseNearbyTrees(ra.type.sensorRadius, team);
		if(allTrees.length ==0) {
			return null;
		}
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 999999f;
		
		for(TreeInfo ti : allTrees) {
			float dist = ra.loc.distanceSquaredTo(ti.location);
			if(dist < nearestDist) {
				nearestDist = dist;
				nearestTree = ti;
			}
		}
		
		return(nearestTree);
	}
	
	public RobotInfo findNearestRobot(Team team) {
		RobotInfo[] allRobots = rc.senseNearbyRobots(ra.type.sensorRadius, team);
		
		if(allRobots.length ==0) {
			return null;
		}
		RobotInfo nearestRobot = allRobots[0];
		float nearestDist = 999999f;
		
		for(RobotInfo ri : allRobots) {
			if(ri.team.equals(team)) {
				float dist = ra.loc.distanceSquaredTo(ri.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = ri;
				}
			}
		}
		return(nearestRobot);
	}
	
	//gets nearest non-gardener or archon robot in sensor range
	public RobotInfo findNearestAttacker(Team team) {
		RobotInfo[] allRobots = rc.senseNearbyRobots(ra.type.sensorRadius, team);
		if(allRobots.length ==0) {
			return null;
		}
		
		RobotInfo nearestRobot=null;
		float nearestDist = 999999f;
		
		for(RobotInfo ri : allRobots) {
			float dist = ra.loc.distanceSquaredTo(ri.location);
			if(!ri.type.equals(RobotType.GARDENER) && !ri.type.equals(RobotType.ARCHON)) {
				if(nearestRobot==null || dist<nearestDist)
				nearestDist = dist;
				nearestRobot = ri;
			}
		}
		
		return(nearestRobot);
	}

	public RobotInfo findNearestRobotNotArchon(Team team) {
		RobotInfo[] allRobots = rc.senseNearbyRobots(ra.type.sensorRadius, team);
		
		if(allRobots.length ==0) {
			return null;
		}
		RobotInfo nearestRobot = allRobots[0];
		float nearestDist = 999999f;
		
		for(RobotInfo ri : allRobots) {
			if(ri.team.equals(team) && !ri.type.equals(RobotType.ARCHON)) {
				float dist = rc.getLocation().distanceSquaredTo(ri.location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = ri;
				}
			}
		}
		
		if(nearestRobot.type.equals(RobotType.ARCHON)) {
			return null;
		}
		return(nearestRobot);
	}
	
	//senses robots of 
	public RobotInfo[] findRobotsInRange(Team team, RobotType type, float range) {
		RobotInfo[] rawInfo;
		if(team != null) {
			rawInfo = rc.senseNearbyRobots(range, team);
		} else {
			rawInfo = rc.senseNearbyRobots(range);
		}
		
		//remove elements that do not meet criteria
		int removed=0;
		for(RobotInfo ri : rawInfo) {
			if(nequal(ri.team, team) || nequal(ri.type, type)) {
				ri = null;
				removed++;
			}
		}
		
		//create compact array
		RobotInfo[] filteredInfo = new RobotInfo[rawInfo.length-removed];
		int i=0;
		for(RobotInfo ri : rawInfo) {
			if(ri!=null) {
				filteredInfo[i++]=ri;
			}
		}
		return filteredInfo;
	}
	
	//helper equality functions. returns true if either is null
	<T> boolean nequal(T t1, T t2) {
		if(t1==null || t2==null) {	
			return false;
		} else {
			return(!t1.equals(t2));
		}
	}
	
	//Generic search method that will find nearest bot of a certain type 
	public RobotInfo findNearestBotType(Team team, RobotType type){
		RobotInfo[] allRobots = rc.senseNearbyRobots(ra.type.sensorRadius, team);
		if(allRobots.length==0) {
			return null;
		}
		
		float nearestDist = 999999f;
		RobotInfo nearestRobot = allRobots[0];
		//Saves index to not repeat values
		int x=0;
		for(int i=0;i<allRobots.length;i++){
			if(allRobots[i].type.equals(type))
			{
				nearestRobot = allRobots[0];
				break;
			}
			x++;
		}
		for(int i=x;i<allRobots.length;i++){
			if(allRobots[i].type.equals(type)){
				float dist = rc.getLocation().distanceSquaredTo(allRobots[i].location);
				if(dist < nearestDist) {
					nearestDist = dist;
					nearestRobot = allRobots[i];
				}
			}
		}
		
		//in case no robots of type are detected
		if(!nearestRobot.type.equals(type)) {
			return null;
		}
		
		return nearestRobot;
	}

	//radial binary search based on fitness function
	MapLocation findTargetLocation() {
		Direction angle = findTargetAngle();
		MapLocation target = findTargetLocation(angle);
		
		//System.out.println(target);
		return target;
	}
	
	Direction findTargetAngle() {
		Direction angle = Direction.getWest();
		float rad = sensorRange/2f;
		//System.out.println("RAD" + rad);
		float scope = 90f;
		
		for(int i=0; i<8; i++) {
			
			MapLocation D1 = ra.loc.add(angle.rotateRightDegrees(scope), rad);
			MapLocation D2 = ra.loc.add(angle.rotateLeftDegrees(scope), rad);
			
			float D1Score = ra.getFitnessScore(D1);
			float D2Score = ra.getFitnessScore(D2);
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
	
	MapLocation findTargetLocation(Direction dir) {
		MapLocation target = ra.loc.add(dir, sensorRange/2f);
		
		float startRange = 0f;
		float endRange = sensorRange;
		
		for(int i=0; i<8; i++) {
			float startScore = ra.getFitnessScore(ra.loc.add(dir, startRange));
			float endScore = ra.getFitnessScore(ra.loc.add(dir, endRange));
			
			if(startScore > endScore) {
				endRange = (startRange+endRange)/2;
			} else {
				startRange = (startRange+endRange)/2;
			}
			
			target = ra.loc.add(dir, (startRange+endRange/2));
		}
		
		return target;
	}

	boolean lineOfSightTo(MapLocation l) {	
		float[] vTarget = new float[]{ra.loc.x-l.x, ra.loc.y-l.y};
		float[] vOrtho = new float[]{-vTarget[1], vTarget[0]}; //orthogonal vector	
			
		for(RobotInfo ri : ra.allRobots) {
			float[] vObj = new float[]{ra.loc.x-ri.location.x, ra.loc.y-ri.location.y};	
			float minDist = Math.abs(vOrtho[0]*vObj[0] + vOrtho[1]*vObj[1]); //projection onto orthogonal vector gives minimum distance to line
			if(minDist <= ri.getRadius()) {
				return false;
			}
		}
		return true;	
	}	
}




