package cleanplayer;

import battlecode.common.*;

public class ActorSoldier extends BaseActorShooter {
	
	public ActorSoldier(RobotController rc) {
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
		MapLocation target = broadcast.readBroadcastLocation(ModBroadcast.ENEMY_SIGHTED_CHANNEL);
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
