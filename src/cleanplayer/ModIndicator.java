package cleanplayer;

import battlecode.common.*;

public class ModIndicator{
	// Instance Variables:
	ActorRobot ra;
	RobotController rc;
	final int[] ColorCurrentLoc = {0, 255, 0}; // Green
	final int[] ColorPastLoc = {255, 0, 0}; // Red
	final int[] ColorDirectionCurrent = {0, 0, 255}; // Blue
	
	// Constructor:
	public ModIndicator(ActorRobot ra_in, RobotController rc_in){
		ra = ra_in;
		rc = rc_in;
	}
	
	// Methods:
	public void drawIndicatorDotLast(){
		rc.setIndicatorDot(ra.lastLocation, ColorPastLoc[0], ColorPastLoc[1], ColorPastLoc[2]);
	}
	public void drawIndicatorDotCurrent(){
		rc.setIndicatorDot(ra.loc, ColorCurrentLoc[0], ColorCurrentLoc[1], ColorCurrentLoc[2]);
	}
	public void drawIndicatorLine(){
		rc.setIndicatorLine(ra.lastLocation, ra.loc, ColorDirectionCurrent[0], ColorDirectionCurrent[1], ColorDirectionCurrent[2]);
	}
}