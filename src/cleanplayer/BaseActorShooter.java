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
		
		if(rc.canFireSingleShot() && sensor.lineOfSightTo(ri.location)) {
			try{
				rc.fireSingleShot(loc.directionTo(ri.location));
				return true;
			} catch(Exception e){e.printStackTrace();}
		}
		return false;
	}
}
