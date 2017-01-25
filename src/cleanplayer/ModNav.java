package cleanplayer;

import battlecode.common.*;

public class ModNav {
	
	RobotController rc;
	BaseActor ra;
	float maxStride;
	boolean leftTurnBias;
	
	public ModNav(BaseActor ra, RobotController rc) {
		this.rc = rc;
		this.ra = ra;
		this.maxStride = ra.type.strideRadius;
		decideLeftTurnBias();
	}
	
	void decideLeftTurnBias() {
		if(Math.random()>0.5) {
			 leftTurnBias = true;
		} else {
			leftTurnBias = false;
		}
	}
	
	public boolean moveInDirection(Direction dir) {
		if(rc.hasMoved() || dir==null) {
			return false;
		}
		
		float turnStep = 10f;
		Direction d = dir;
		for(int i=0; i<(int)(360f/turnStep); i++) {
			/*Direction right = d.rotateRightDegrees(turnStep*((float) i));
			Direction left = d.rotateLeftDegrees(turnStep*((float) i));
			
			if(leftTurnBias) {
				right = d.rotateLeftDegrees(turnStep*((float) i));
				left = d.rotateRightDegrees(turnStep*((float) i));
			}
			
			
			if(rc.canMove(right)) {
				try{
					//System.out.println("moved");
					rc.move(right);
					return true;
				} catch(Exception e) {e.printStackTrace();}
			} else if(rc.canMove(left)) {
				try{
					//System.out.println("moved");
					rc.move(left);
					return true;
				} catch(Exception e) {e.printStackTrace();}
			}*/
			try{
				MapLocation simLoc = ra.loc.add(d);
				if(rc.canMove(simLoc)&&rc.canMove(d) && safeToMove(d)) {
					rc.move(d);
					return true;
				} else {
					if(leftTurnBias) {
						d = d.rotateLeftDegrees(turnStep);
					} else {
						d = d.rotateRightDegrees(turnStep);
					}
				}
			}catch(Exception e) {/*e.printStackTrace();*/}
		}
		return false;
	}
	
	//ONLY TAKES BULLETS INTO ACCOUNT
	public boolean safeToMove(Direction d) {
		BulletInfo[] bullets = rc.senseNearbyBullets();
		MapLocation simLoc = ra.loc.add(d, ra.type.strideRadius);
		for(BulletInfo bi : bullets) {
			if(simLoc.distanceTo(bi.location) < ra.type.bodyRadius) {
				return false;
			}
		}
		return true;
	}
	
	public boolean moveFromLocation(MapLocation l) {
		if(rc.hasMoved()) {
			return false;
		}
		Direction dir = rc.getLocation().directionTo(l).opposite();
		return(moveInDirection(dir));
	}
	
	public boolean wanderToLocation(MapLocation l) {
		if(rc.hasMoved()) {
			return false;
		}
		//EXPERIMENTAL
		float angleToLocation = ra.loc.directionTo(l).radians;
		Direction dirToLast = ra.loc.directionTo(ra.lastLocation);
		float angleFromLast=0f;
		if(dirToLast!=null) {
			angleFromLast = dirToLast.opposite().radians;
		}
		Direction dir = new Direction((angleToLocation+angleFromLast)/2f);
		//END EXPERIMENTAL DIRECTION
		//OLD PRE-EXPERIMENTAL CODE: Direction dir = ra.loc.directionTo(l);
		if(rc.canMove(l)) {
			try{
				rc.move(l);
				return true;
			} catch(Exception e) {e.printStackTrace();}
		} else {
			return(moveInDirection(dir));
		}
		return false;
	}
	
	public boolean moveToLocation(MapLocation l) {
		if(rc.hasMoved()) {
			return false;
		}
		
		Direction dir = ra.loc.directionTo(l);
		if(rc.canMove(l)) {
			try{
				rc.move(l);
				return true;
			} catch(Exception e) {e.printStackTrace();}
		} else {
			return(moveInDirection(dir));
		}
		return false;
	}
}
