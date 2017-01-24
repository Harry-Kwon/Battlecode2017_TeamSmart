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
		move();
	}
	
	public void move() {
		if(nav.moveAroundEnemies()	) {
			//moved around enemies
		} else if(nav.moveToBroadcastChannel()) {
			//moved to broadcast
		} else {
			wander();
		}
	}

}
