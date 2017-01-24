package cleanplayer;

import battlecode.common.*;

public class ModBroadcast {
	
	public static final int ENEMY_SIGHTED_CHANNEL_START=1000;
	public static final int ENEMY_SIGHTED_CHANNELS = 100;
	public static final int NEUTRAL_TREE_CHANNEL=400;

	BaseActor ra;
	RobotController rc;
	
	
	public ModBroadcast(BaseActor ra, RobotController rc) {
		this.ra = ra;
		this.rc = rc;
	}
	
	public void broadcastAllEnemies() {
		RobotInfo[] enemies = ra.sensor.findRobotsInRange(ra.team.opponent(), null, ra.sensorRange);
		for(RobotInfo ri : ra.allRobots) {
			int channel = nextOpenChannel(ModBroadcast.ENEMY_SIGHTED_CHANNEL_START, ModBroadcast.ENEMY_SIGHTED_CHANNELS);
			if(channel==-1) {
				return;
			}
			try{
				broadcastLocation(ri.location, channel);
			} catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public MapLocation readNearestEnemyBroadcast() {
		int start = ModBroadcast.ENEMY_SIGHTED_CHANNEL_START;
		int num = ModBroadcast.ENEMY_SIGHTED_CHANNELS;
		MapLocation bestLocation=null;
		float bestDist = 9999999f;
		for(int i = start; i < start+num; i++) {
			MapLocation l = readBroadcastLocation(start+i);
			if(l==null) {
				continue;
			}
			float dist = ra.loc.distanceTo(l);
			if(dist < bestDist) {
				bestLocation = l;
				bestDist = dist;
			}
		}
		return(bestLocation);
	}
	
	int nextOpenChannel(int start, int number) {
		for(int i=start; i<start+number; i++) {
			try{
				if(rc.readBroadcast(start+i)==0) {
					return(start+i);
				}
			} catch(Exception e){e.printStackTrace();}
		}
		return -1;
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
			//ignore default value
			if(msg==0) {
				return null;
			}
			
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
	
	boolean clearChannel(int chan) {
		try{
			rc.broadcast(chan, 0);
			return true;
		} catch(Exception e){e.printStackTrace();}
		return false;
		
	}
}
