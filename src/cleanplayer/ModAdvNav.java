package cleanplayer;

import battlecode.common.*;

public class ModAdvNav extends ModNav{
	
	public ModAdvNav(BaseActor ra, RobotController rc) {
		super(ra, rc);
	}
	
	public boolean moveToNearestFullNeutralTree() {
		TreeInfo nearestTree = ra.sensor.findNearestFullNeutralTree();
		if(nearestTree==null) {
			return false;
		}
		
		return(moveToLocation(nearestTree.location));
	}
	
	public boolean moveToBroadcastChannel() {
		MapLocation target = ra.broadcast.readBroadcastLocation(ModBroadcast.ENEMY_SIGHTED_CHANNEL);
		if(target==null) {
			return false;
		}
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
