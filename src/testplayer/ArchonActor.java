package testplayer;

import battlecode.common.*;

public class ArchonActor extends RobotActor {

	float rotationStep=90f;
	
	public ArchonActor(RobotController rc) {
		super(rc);
	}
	
	public void act() {
		updateRoundVars();
		
		try {
			buildGardener();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
