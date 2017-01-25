package cleanplayer;

import battlecode.common.*;

public class BaseActor {
	
	ModSensor sensor;
	ModNav nav;
	ModIndicator indicator;
	ModBroadcast broadcast;
	ModMemory memory;
	
	public RobotController rc;
	public MapLocation loc;
	float sensorRange;
	RobotType type;
	Team team;
	
	public boolean shot = false;
	
	public RobotInfo[] allRobots;
	public TreeInfo[] allTrees;
	public MapLocation lastLocation;
	
	boolean leftTurnBias = false;
	
	String fitnessMode = "wander";
	
	public BaseActor(RobotController rc){
		initializeActor(rc);
	}
	
	//initialization
	void initializeActor(RobotController rc) {
		this.rc = rc;
		this.type = rc.getType();
		this.sensorRange = rc.getType().sensorRadius;
		this.loc = rc.getLocation();
		this.team = rc.getTeam();
		
		lastLocation = loc;
		sensor = new ModSensor(this, rc);
		nav = new ModNav(this, rc);
		broadcast = new ModBroadcast(this, rc);
		indicator = new ModIndicator(this, rc);
		memory = new ModMemory(this, rc);
	}
	
	//round actions
	public void act() {
		if(!rc.isBuildReady()) {
			return;
		}
		updateRoundVars();
		robotAct();
		closeRoundVars();
	}
	
	public void robotAct() {}
	
	public void updateRoundVars() {
		if(!rc.isBuildReady()) {
			return;
		}
		this.loc = rc.getLocation();
		indicator.drawIndicatorLine();
		senseAll();
		shakeTree();
	}
	
	public void closeRoundVars() {
		lastLocation = loc;
		broadcast.verifyEnemyLocationBroadcasts();
		broadcast.broadcastAllEnemies();
	}
	
	//sensing
	void senseAll() {
		allRobots = rc.senseNearbyRobots();
		allTrees = rc.senseNearbyTrees();
	}
	
	//Nav Code
	//uses radial binary search based on location fitness scoring function
	public boolean wander() {
		MapLocation target = sensor.findTargetLocation();
		return(nav.moveToLocation(target));
	}
	
	//overwrite fitness Score for each child class
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
		if(type.equals(RobotType.GARDENER)) {
			for(TreeInfo ti : allTrees) {
				if(ti.team.equals(Team.NEUTRAL)) {
					fitness -= 1f/l.distanceSquaredTo(ti.location);
				}
			}
		}
		
		
		fitness -= 10f/l.distanceSquaredTo(lastLocation);
		
		return fitness;
	}
	
	//misc
	boolean shakeTree() {
		TreeInfo nearestTree = sensor.findNearestTree(Team.NEUTRAL);
		if(nearestTree==null){
			return false;
		}
		
		if(rc.canShake(nearestTree.ID)) {
			try{
				rc.shake(nearestTree.ID);
				return true;
			} catch(Exception e){e.printStackTrace();}
		}
		return false;
	}
}
