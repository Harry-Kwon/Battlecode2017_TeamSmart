package cleanplayer;

import battlecode.common.*;

public class ActorTank extends BaseActorShooter {

	public ActorTank(RobotController rc) {
		super(rc);
	}
	
	public void robotAct() {
		//BroadcastArchon
		
		//shake
		shakeTree();
		
		//combat
		shootNearestRobot();
		
		//movement
		idle();
	}
	
	public void idle() {
		MapLocation target = broadcast.readNearestEnemyBroadcast();
		if(target!=null) {
			if(!rc.canSenseLocation(target)) {
				nav.moveToLocation(target);
				return;
			} else {
				try {
					broadcast.clearChannel(555);
					return;
				} catch (Exception e) {e.printStackTrace();}
			}
		}
		
		super.wander();
	}
}
