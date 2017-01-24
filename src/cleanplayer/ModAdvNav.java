package cleanplayer;

import battlecode.common.*;
import battlecode.instrumenter.InstrumentationException.Type;

public class ModAdvNav extends ModNav{
	
	public ModAdvNav(BaseActor ra, RobotController rc) {
		super(ra, rc);
	}
	
	public boolean moveAroundEnemies() {
		RobotInfo nearestEnemy = ra.sensor.findNearestRobot(ra.team.opponent());
		if(nearestEnemy==null) {
			return false;
		}
		
		float optimalDist = 0;
		if(nearestEnemy.type.equals(RobotType.LUMBERJACK)) {
			optimalDist = ra.type.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS;
		}
		
		if(ra.loc.distanceTo(nearestEnemy.location) > optimalDist) {
			return(super.moveToLocation(nearestEnemy.location));
		} else {
			return(super.moveFromLocation(nearestEnemy.location));
		}
	}
	
	public boolean moveToNearestFullNeutralTree() {
		TreeInfo nearestTree = ra.sensor.findNearestFullNeutralTree();
		if(nearestTree==null) {
			return false;
		}
		
		return(moveToLocation(nearestTree.location));
	}
	
	public boolean moveToBroadcastChannel() {
		MapLocation target = ra.broadcast.readNearestEnemyBroadcast();
		if(target==null) {
			System.out.println("AAABBBCCC");
			return false;
		}
		//System.out.println(target + "broadcast location");
		if(!rc.canSenseLocation(target)) {
			if(moveToLocation(target)) {
				return true;
			}
		} else {
			try {
				ra.broadcast.clearChannel(555);
			} catch (Exception e) {e.printStackTrace();}
		}
		return false;
	}
}
