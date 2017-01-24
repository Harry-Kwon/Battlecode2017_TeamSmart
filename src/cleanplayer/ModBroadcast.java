package cleanplayer;

import java.util.ArrayList;

import battlecode.common.*;

public class ModBroadcast {
	
	public static final int ENEMY_SIGHTED_CHANNEL_START=1000;
	public static final int ENEMY_SIGHTED_CHANNELS = 10;
	public static final int NEUTRAL_TREE_CHANNEL=400;

	BaseActor ra;
	RobotController rc;
	ArrayList<MapLocation> enemyLocations = new ArrayList<MapLocation>(0);
	
	
	public ModBroadcast(BaseActor ra, RobotController rc) {
		this.ra = ra;
		this.rc = rc;
	}
	
	public void verifyEnemyLocationBroadcasts() {
		int start = ModBroadcast.ENEMY_SIGHTED_CHANNEL_START;
		int num = ModBroadcast.ENEMY_SIGHTED_CHANNELS;
		enemyLocations.clear();
		for(int i = start; i < start+num; i++) {
			MapLocation l = readBroadcastLocation(start+i);
			if(l==null) {
				continue;
			}

			if(rc.canSenseLocation(l)) {
				try{
					RobotInfo ri = rc.senseRobotAtLocation(l);
					if(ri==null || ri.team==ra.team) {
						clearChannel(start+i);
					} else if(!enemyLocations.contains(l)){
						enemyLocations.add(l);
					}
				} catch(Exception e){e.printStackTrace();}
			} else if(!enemyLocations.contains(l)) {
				enemyLocations.add(l);
			}
		}
	}
	
	public void broadcastAllEnemies() {
		RobotInfo[] enemies = rc.senseNearbyRobots(ra.sensorRange, ra.team.opponent());
		if(enemies.length==0) {
			return;
		}
		
		for(RobotInfo ri : enemies) {
			int channel = nextOpenChannel(ModBroadcast.ENEMY_SIGHTED_CHANNEL_START, ModBroadcast.ENEMY_SIGHTED_CHANNELS);
			if(channel==-1 || enemyLocations.contains(ri.location)) {
				return;
			}
			try{
				broadcastLocation(ri.location, channel);
			} catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public MapLocation readNearestEnemyBroadcast() {
		float bestDist = 9999999;
		MapLocation bestLocation = null;
		for(MapLocation l : enemyLocations) {
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
