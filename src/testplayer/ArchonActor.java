package testplayer;

import battlecode.common.*;

public class ArchonActor extends RobotActor {

	float rotationStep=10f;
	
	public ArchonActor(RobotController rc) {
		super(rc);
	}
	
	public void act() {
		updateRoundVars();
		
		if(rc.getTeamBullets() >= GameConstants.VICTORY_POINTS_TO_WIN*GameConstants.BULLET_EXCHANGE_RATE) {
			try{
				rc.donate(GameConstants.VICTORY_POINTS_TO_WIN*GameConstants.BULLET_EXCHANGE_RATE);
			} catch(Exception e) {e.printStackTrace();}
		}
		
		if(rc.getRoundNum()%20==1) {
			buildGardener();
		}
		
		
		closeRoundVars();
	}
	
	boolean buildGardener() {
		
		Direction dir = Direction.getEast();
		boolean spawned = false;
		
		for(int i=0; i<((int) 360f/rotationStep); i++){
			
			if(rc.canBuildRobot(RobotType.GARDENER, dir)) {
				try{ 
					rc.buildRobot(RobotType.GARDENER, dir);
					spawned = true;
					break;
				} catch(Exception e) {e.printStackTrace();}
			} else {
				dir = dir.rotateLeftDegrees(rotationStep);
			}
		}
		return spawned;
	}
}
