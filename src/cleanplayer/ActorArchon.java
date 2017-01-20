package cleanplayer;

import battlecode.common.*;

public class ActorArchon extends BaseActor {
	
	public ActorArchon(RobotController rc) {
		super(rc);
	}
	
	public void robotAct() {
		if(rc.getRoundNum()%20==1) {
			buildGardener();
		}
	}
	
	boolean buildGardener() {
		float rotationStep=10f;
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
