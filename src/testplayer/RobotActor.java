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
	
	public void moveInDirection(Direction dir) {
		
		float turnStep = 10f;
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
