package cleanplayer;

import battlecode.common.*;

public class BaseActorShooter extends BaseActor {
	
	ModAdvNav nav;
	
	public BaseActorShooter(RobotController rc) {
		super(rc);
		nav = new ModAdvNav(this, rc);
	}
	
	public void updateRoundVars() {
		super.updateRoundVars();
		shot = false;
	}
	
	public void closeRoundVars() {
		super.closeRoundVars();
		memory.updateLastLocTable();
	}
	
	boolean shootNearestRobot() {
		RobotInfo ri = sensor.findShootingTarget();
		if(ri==null) {
			return false;
		}
		MapLocation targetLoc = ri.location;
		
		MapLocation targetLastLoc = memory.getLastLocOf(ri.ID);
		if(targetLastLoc !=null) {
			Direction vel = targetLastLoc.directionTo(targetLoc);
			targetLoc = targetLoc.add(vel, ri.getRadius()/2f);
		}

		float friendlyRadius = super.type.bodyRadius;
		float enemyRadius = ri.getRadius();
		float paddingFactor = 1f;
		float distanceToTarget = loc.distanceTo(ri.location) - GameConstants.BULLET_SPAWN_OFFSET - friendlyRadius - paddingFactor;
		final float TRIAD_MULTIPLIER = 1f;
		final float OPTIMAL_SHOT_ANGLE_TRIAD = (float) Math.toRadians(GameConstants.TRIAD_SPREAD_DEGREES * TRIAD_MULTIPLIER);
		final float OPTIMAL_SHOT_ANGLE_PENTAD = (float) Math.toRadians(GameConstants.PENTAD_SPREAD_DEGREES * 2f);
		float shotAngle = (float) Math.asin(enemyRadius/distanceToTarget);
		if(rc.canFirePentadShot() && sensor.lineOfSightTo(targetLoc, ri.ID) && shotAngle >= OPTIMAL_SHOT_ANGLE_PENTAD){
			try{
				rc.firePentadShot(loc.directionTo(targetLoc));
				return true;
			} catch(Exception e){e.printStackTrace();}
		}
		else if(rc.canFireTriadShot() && sensor.lineOfSightTo(targetLoc, ri.ID) && shotAngle >= OPTIMAL_SHOT_ANGLE_TRIAD){
			try{
				rc.fireTriadShot(loc.directionTo(targetLoc));
				return true;
			} catch(Exception e){e.printStackTrace();}
		}
		else{
			if(rc.canFireSingleShot() && sensor.lineOfSightTo(targetLoc, ri.ID)) {
				try{
					rc.fireSingleShot(loc.directionTo(targetLoc));
					return true;
				} catch(Exception e){e.printStackTrace();}
			}
		}
		return false;
	}
}