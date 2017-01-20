package cleanplayer;

import battlecode.common.*;

public class ModBroadcast {

	ActorRobot ra;
	RobotController rc;
	
	public ModBroadcast(ActorRobot ra, RobotController rc) {
		this.ra = ra;
		this.rc = rc;
	}
	
	boolean broadcastLocation(MapLocation l, int chan) {
		if(l==null) {
			return false;
		}
		
		int msg = ((int) l.x)*1000 + ((int) l.y);
		try{
			rc.broadcast(chan, msg);
			return true;
		} catch(Exception e){
			System.err.println("error writing location " + l + " to channel: " + chan);
			e.printStackTrace();
		}
		return false;
	}
	
	MapLocation readBroadcastLocation(int chan) {
		try{
			int msg = rc.readBroadcast(chan);
			
			//Maps technically have integer limits of 3 digits
			//shift x coordinates to the left half of the integer
			MapLocation l = new MapLocation((float)(msg/1000), (float)(msg%1000));
			return l;
		} catch(Exception e) {
			System.err.println("error reading broadcast from channel: " + chan);
			e.printStackTrace();
		}
		
		return null;
		
	}
}
