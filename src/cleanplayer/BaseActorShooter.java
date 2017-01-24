package cleanplayer;

import battlecode.common.*;

public class BaseActorShooter extends BaseActor {
	
	ModAdvNav nav;
	
	public BaseActorShooter(RobotController rc) {
		super(rc);
		nav = new ModAdvNav(this, rc);
	}
	
	boolean shootNearestRobot() {
		RobotInfo ri = sensor.findNearestRobot(rc.getTeam().opponent());
		if(ri==null) {
			return false;
		}
		float friendlyRadius = super.type.bodyRadius;
		float enemyRadius = ri.getRadius();
		float distanceToTarget = loc.distanceTo(ri.location) - enemyRadius - friendlyRadius;
		final float TRIAD_MULTIPLIER = 2/3;
		final float OPTIMAL_SHOT_ANGLE_TRIAD = (float) Math.toRadians(GameConstants.TRIAD_SPREAD_DEGREES * TRIAD_MULTIPLIER);
		final float OPTIMAL_SHOT_ANGLE_PENTAD = (float) Math.toRadians(GameConstants.PENTAD_SPREAD_DEGREES);
		float shotAngle = (float) Math.atan(enemyRadius/(distanceToTarget));
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