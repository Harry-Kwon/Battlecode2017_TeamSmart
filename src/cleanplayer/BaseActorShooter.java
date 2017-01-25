package cleanplayer;

import battlecode.common.*;

public class BaseActorShooter extends BaseActor {
	
	ModAdvNav nav;
	boolean shot = false;
	
	public BaseActorShooter(RobotController rc) {
		super(rc);
		nav = new ModAdvNav(this, rc);
	}
	
	public void updateRoundVars() {
		super.updateRoundVars();
		shot = false;
	}
	
	boolean shootNearestRobot() {
		RobotInfo ri = sensor.findShootingTarget();
		if(ri==null) {
			return false;
		}
		float friendlyRadius = super.type.bodyRadius;
		float enemyRadius = ri.getRadius();
		float distanceToTarget = loc.distanceTo(ri.location) - GameConstants.BULLET_SPAWN_OFFSET - friendlyRadius;
		final float TRIAD_MULTIPLIER = 1f;
		final float OPTIMAL_SHOT_ANGLE_TRIAD = (float) Math.toRadians(GameConstants.TRIAD_SPREAD_DEGREES * TRIAD_MULTIPLIER);
		final float OPTIMAL_SHOT_ANGLE_PENTAD = (float) Math.toRadians(GameConstants.PENTAD_SPREAD_DEGREES * 2f);
		float shotAngle = (float) Math.asin(enemyRadius/distanceToTarget);
		if(rc.canFirePentadShot() && sensor.lineOfSightTo(ri.location) && shotAngle >= OPTIMAL_SHOT_ANGLE_PENTAD){
			try{
				rc.firePentadShot(loc.directionTo(ri.location));
				return true;
			} catch(Exception e){e.printStackTrace();}
		}
		else if(rc.canFireTriadShot() && sensor.lineOfSightTo(ri.location) && shotAngle >= OPTIMAL_SHOT_ANGLE_TRIAD){
			try{
				rc.fireTriadShot(loc.directionTo(ri.location));
				return true;
			} catch(Exception e){e.printStackTrace();}
		}
		else{
			if(rc.canFireSingleShot() && sensor.lineOfSightTo(ri.location)) {
				try{
					rc.fireSingleShot(loc.directionTo(ri.location));
					return true;
				} catch(Exception e){e.printStackTrace();}
			}
		}
		return false;
	}
}