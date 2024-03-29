package cleanplayer;

import battlecode.common.*;

public class ActorSoldier extends BaseActorShooter {
	
	String fitnessMode = "wander";
	
	public ActorSoldier(RobotController rc) {
		super(rc);
	}
	
	public void robotAct() {
		//BroadcastArchon
		
		//combat
		shot = shootNearestRobot();
		
		//movement
		move();
	}
	
	public void move() {
		if(nav.moveAroundEnemies()) {
			rc.setIndicatorDot(loc, 0, 255, 0);
		} else if(nav.moveToBroadcastChannel()) {
			rc.setIndicatorDot(loc, 0, 0, 255);
		} else {
			fitnessMode="wander";
			wander();
			rc.setIndicatorDot(loc, 255, 0, 0);
		}
	}
	
	float getFitnessScore(MapLocation l) {
		float fitness = 0f;
		if(fitnessMode.equals("wander")) {
			fitness = super.getFitnessScore(l);
		} else if(fitnessMode.equals("toBroadcast")) {
			float targetBias = 20.0f;
			fitness -= 1f/l.distanceTo(lastLocation);
			fitness += targetBias * 1f/loc.distanceTo(l);
		}
		return fitness;
	}

}
