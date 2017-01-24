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
		boolean shot = shootNearestRobot();
		
		//movement
		if (shot == false){
			move();
		}
	}
	
	public void move() {
		if(nav.moveAroundEnemies()) {
			rc.setIndicatorDot(loc, 0, 255, 0);
		} else if(nav.moveToBroadcastChannel()) {
			rc.setIndicatorDot(loc, 0, 0, 255);
		} else {
			wander();
			rc.setIndicatorDot(loc, 255, 0, 0);
		}
	}

}
