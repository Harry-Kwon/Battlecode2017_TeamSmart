package testplayer;

import battlecode.common.*;

public class RobotActor {
	
	public RobotController rc;
	public MapLocation loc;
	
	public RobotActor(RobotController rc){
		this.rc = rc;
	}
	
	public void act() {
		//
	}
	
	public void updateRoundVars() {
		this.loc = rc.getLocation();
	}
	
	//Nav Code
	
	//binary search-ish, 4 iterations for now (360/2^4 = 22.5)
	public void moveFromLocation(MapLocation l) {
		Direction dir = l.directionTo(loc);
		
		if(rc.canMove(dir)) {
			try{
				rc.move(dir);
				return;
			} catch(Exception e) {e.printStackTrace();}
		}
	}
}
