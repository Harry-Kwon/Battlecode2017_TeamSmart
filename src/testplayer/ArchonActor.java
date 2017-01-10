package testplayer;

import battlecode.common.*;

public class ArchonActor extends RobotActor {

	float rotationStep=90f;
	
	public ArchonActor(RobotController rc) {
		super(rc);
	}
	
	public void act() {
		try {
			buildGardener();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	boolean buildGardener() {
		System.out.println("archon");
		Direction dir = Direction.getEast();
		boolean spawned = false;
		System.out.println(((int) 360/rotationStep));
		
		for(int i=0; i<((int) 360/rotationStep); i++){
			
			if(rc.canBuildRobot(RobotType.GARDENER, dir)) {
				try{ 
					rc.buildRobot(RobotType.GARDENER, dir);
					spawned = true;
					break;
				} catch(Exception e) {e.printStackTrace();}
			} else {
				System.out.println(dir.getAngleDegrees());
				dir = dir.rotateLeftDegrees(rotationStep);
			}
		}
		System.out.println(dir.getAngleDegrees());
		System.out.println(spawned);
		return spawned;
	}
}
