package cleanplayer;

import battlecode.common.*;

public class ModMemory {
	
	BaseActor ra;
	RobotController rc;
	
	RobotInfo[] lastRobotInfo = new RobotInfo[0];
	
	public ModMemory(BaseActor ra, RobotController rc) {
		this.ra = ra;
		this.rc = rc;
	}
	
	public void updateLastLocTable() {
		RobotInfo[] enemies = rc.senseNearbyRobots(ra.sensorRange, ra.team.opponent());
		lastRobotInfo = new RobotInfo[enemies.length];
		for(int i=0; i<enemies.length; i++) {
			lastRobotInfo[i] = enemies[i];
		}
	}
	
	public MapLocation getLastLocOf(int rId) {
		if(lastRobotInfo==null) {
			return null;
		}
		
		for(RobotInfo ri : lastRobotInfo) {
			if(ri==null) {
				continue;
			}
			
			if(ri.ID == rId) {
				return(ri.location);
			}
		}
		return null;
	}
	
}
