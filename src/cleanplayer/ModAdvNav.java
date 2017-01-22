package cleanplayer;

import battlecode.common.*;

public class ModAdvNav extends ModNav{
	
	public ModAdvNav(BaseActor ra, RobotController rc) {
		super(ra, rc);
	}
	
	public boolean moveAroundEnemies() {
		RobotInfo[] enemies = ra.sensor.findRobotsInRange(ra.team.opponent(), null, ra.sensorRange);
		float optimalDistSq = 40f;
		if(ra.type.equals(RobotType.SCOUT)) {
			optimalDistSq = 100f;
		}
		if(enemies.length==0) {
			return false;
		}
		
		float[] vDir = new float[]{0f, 0f};
		for(RobotInfo ri : enemies) {
			float dist = ra.loc.distanceSquaredTo(ri.location);
			vDir[0] += (ra.loc.x - ri.location.x) * (optimalDistSq-dist);
			vDir[1] += (ra.loc.y - ri.location.y) * (optimalDistSq-dist);
		}
		
		return(super.moveInDirection(new Direction(vDir[0], vDir[1])));
		
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
