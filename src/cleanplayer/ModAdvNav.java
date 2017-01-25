package cleanplayer;

import battlecode.common.*;
import battlecode.instrumenter.InstrumentationException.Type;

public class ModAdvNav extends ModNav{
	
	public ModAdvNav(BaseActor ra, RobotController rc) {
		super(ra, rc);
	}
	
	public boolean moveAroundEnemies() {
		//return true;
		RobotInfo targetRi = ra.sensor.findShootingTarget();
		if(targetRi==null) {
			return false;
		}
		
		Direction dir = ra.loc.directionTo(targetRi.location);
		//default
		if(leftTurnBias) {
			dir.rotateLeftDegrees(90f);
		} else {
			dir.rotateRightDegrees(90f);
		}
		
		if(!rc.canMove(dir) && safeToMove(dir)) {
			dir.rotateLeftDegrees(180f);
		}
		
		//around gardener
		if(targetRi.type.equals(RobotType.GARDENER)) {
			if(ra.sensor.lineOfSightTo(targetRi.location)) {
				dir=null;
			}
		}
		
		return(moveInDirection(dir));
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
			return false;
		}
		//System.out.println(target + "broadcast location");
		if(!rc.canSenseLocation(target)) {
			ra.fitnessMode="toBroadcast";
			return(ra.wander());
		}
		return false;
	}
}
