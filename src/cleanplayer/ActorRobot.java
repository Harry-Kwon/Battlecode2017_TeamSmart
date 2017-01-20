package cleanplayer;

import battlecode.common.*;

public class ActorRobot {
	
	ModSensor sensor;
	ModNav nav;
	ModIndicator indicator;
	ModBroadcast broadcast;
	
	public RobotController rc;
	public MapLocation loc;
	float sensorRange;
	RobotType type;
	
	public RobotInfo[] allRobots;
	public TreeInfo[] allTrees;
	public MapLocation lastLocation;
	
	boolean leftTurnBias = false;
	
	public ActorRobot(RobotController rc){
		initializeActor(rc);
	}
	
	void initializeActor(RobotController rc) {
		this.rc = rc;
		this.type = rc.getType();
		this.sensorRange = rc.getType().sensorRadius;
		this.loc = rc.getLocation();
		
		lastLocation = loc;
		sensor = new ModSensor(this, rc);
		nav = new ModNav(this, rc);
		broadcast = new ModBroadcast(this, rc);
		indicator = new ModIndicator(this, rc);
	}
	
	public void act() {
		updateRoundVars();
		System.out.println("act");
		robotAct();
		closeRoundVars();
	}
	
	public void robotAct() {}
	
	public void updateRoundVars() {
		this.loc = rc.getLocation();
		indicator.drawIndicatorLine();
		senseAll();
	}
	
	public void closeRoundVars() {
		lastLocation = loc;
	}
	
	void senseAll() {
		allRobots = rc.senseNearbyRobots();
		allTrees = rc.senseNearbyTrees();
	}
	
	//Nav Code
	
	//binary search-ish, 4 iterations for now (360/2^4 = 22.5)
	//overwrite fitness Score
	
	public void wander() {
		MapLocation target = sensor.findTargetLocation();
		nav.moveToLocation(target);
	}
	
	
	
	float getFitnessScore(MapLocation l) {
		try{
			if(rc.canSenseLocation(l) && !rc.onTheMap(l)) {
				//System.out.println("dead square");
				return -999999.0f;
			}
		} catch(Exception e){e.printStackTrace();}
		
		float fitness = 0.0f;
		
		for(RobotInfo ri : allRobots) {
			fitness -= 1f/l.distanceSquaredTo(ri.location);
		}
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(Team.NEUTRAL)) {
				fitness -= 1f/l.distanceSquaredTo(ti.location);
			}
		}
		
		fitness -= 10f/l.distanceSquaredTo(lastLocation);
		
		return fitness;
	}
}
