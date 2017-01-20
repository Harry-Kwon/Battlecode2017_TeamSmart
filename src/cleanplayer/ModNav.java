package cleanplayer;

import battlecode.common.*;

public class ModNav {
	
	RobotController rc;
	ActorRobot ra;
	float maxStride;
	boolean leftTurnBias;
	
	public ModNav(ActorRobot ra, RobotController rc) {
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
	
	public void moveInDirection(Direction dir) {
		if(rc.hasMoved()) {
			return;
		}
		
		float turnStep = 2f;
		Direction d = dir;
		for(int i=0; i<(int)(180f/turnStep); i++) {
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
					return;
				} catch(Exception e) {e.printStackTrace();}
			} else if(rc.canMove(left)) {
				try{
					//System.out.println("moved");
					rc.move(left);
					return;
				} catch(Exception e) {e.printStackTrace();}
			}*/
			
			if(rc.canMove(d)) {
				try{
					//System.out.println("moved");
					rc.move(d);
					return;
				} catch(Exception e) {e.printStackTrace();}
			} else {
				if(leftTurnBias) {
					d = d.rotateLeftDegrees(turnStep);
				} else {
					d = d.rotateRightDegrees(turnStep);
				}
			}
		}
	}
	
	public void moveToLocation(MapLocation l) {
		if(rc.hasMoved()) {
			return;
		}
		
		Direction dir = rc.getLocation().directionTo(l);
		if(rc.canMove(l)) {
			try{
				rc.move(l);
			} catch(Exception e) {e.printStackTrace();}
		} else {
			moveInDirection(dir);
		}
	}
}
