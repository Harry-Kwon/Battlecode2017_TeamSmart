package cleanplayer;

import battlecode.common.*;

public class ActorLumberjack extends ActorRobot {
	
	public ActorLumberjack(RobotController rc) {
		super(rc);
	}
	
	public void robotAct() {	
		if(attackEnemyUnits()){
			//System.out.println("attack");
		} else if(attackEnemyWorkers()) {
			//System.out.println("cut enemy trees");
		} else if(cutEnemyTrees()) {
			//System.out.println("attack workers");
		} else if(cutTrees()) {
			//System.out.println("cut trees");
		} else {
			idle();
			//System.out.println("attack");
		}
	}
	
	boolean attackEnemyUnits() {		
		RobotInfo nearestRobot = sensor.findNearestAttacker(rc.getTeam().opponent());
		if(nearestRobot==null) {
			return false;
		}
		
		broadcast.broadcastLocation(nearestRobot.location, 555);
		if(loc.distanceSquaredTo(nearestRobot.location) < Math.pow(1.5f+nearestRobot.getRadius(), 2)){
			try{
				rc.strike();
				return true;
			} catch(Exception e){e.printStackTrace();}
		} else if(sensor.isRobotSurrounded(nearestRobot)) {
			return false;
		} else {
			nav.moveToLocation(nearestRobot.location);
			return true;
		}
		
		return false;
	}
	
	boolean attackEnemyWorkers() {
		RobotInfo nearestRobot = sensor.findNearestBotType(rc.getTeam().opponent(), RobotType.GARDENER);
		if(nearestRobot==null) {
			nearestRobot = sensor.findNearestBotType(rc.getTeam().opponent(), RobotType.ARCHON);
			if(nearestRobot==null) {
				return false;
			}
		}
		
		if(!nearestRobot.team.equals(rc.getTeam()) && (nearestRobot.type.equals(RobotType.GARDENER) || nearestRobot.type.equals(RobotType.ARCHON))) {
			broadcast.broadcastLocation(nearestRobot.location, 555);
			//required distance to hit is attack radius
			if(loc.distanceSquaredTo(nearestRobot.location) < Math.pow(2f+nearestRobot.getRadius(), 2)){
				try{
					rc.strike();
					return true;
				} catch(Exception e){e.printStackTrace();}
			} else if(sensor.isRobotSurrounded(nearestRobot)) {
				return false;
			} else {
				nav.moveToLocation(nearestRobot.location);
				return true;
			}
		}
		return false;
	}
	
	boolean cutEnemyTrees() {
		// cut closest lowest health tree. lowest health prioritized
		if(allTrees.length ==0) {
			return false;
		}
		TreeInfo nearestTree = sensor.findLowestNearestTree(rc.getTeam().opponent());
		if(nearestTree==null) {
			return false;
		}
		
		broadcast.broadcastLocation(nearestTree.location, 555);
		if(rc.canChop(nearestTree.location)){
			try{
				rc.chop(nearestTree.location);
				return true;
			} catch(Exception e){e.printStackTrace();}
		} else if(sensor.isTreeSurrounded(nearestTree)) {
			return false;
		} else {
			nav.moveToLocation(nearestTree.location);
			return true;
		}
		
		return false;
	}
	
	boolean cutTrees() {
		// cut closest lowest health tree. lowest health prioritized
		if(allTrees.length ==0) {
			return false;
		}
		TreeInfo nearestTree = sensor.findLowestNearestTree(Team.NEUTRAL);
		if(nearestTree==null) {
			return false;
		}
		
		broadcast.broadcastLocation(nearestTree.location, 555);
		if(rc.canChop(nearestTree.location)){
			try{
				rc.chop(nearestTree.location);
				return true;
			} catch(Exception e){e.printStackTrace();}
		} else if(sensor.isTreeSurrounded(nearestTree)) {
			return false;
		} else {
			nav.moveToLocation(nearestTree.location);
			return true;
		}
		
		return false;
	}
	
	public void idle() {
		MapLocation target = broadcast.readBroadcastLocation(555);
		if(target!=null) {
			if(!rc.canSenseLocation(target)) {
				nav.moveToLocation(target);
				return;
			} else {
				try {
					broadcast.clearChannel(555);
					return;
				} catch (Exception e) {e.printStackTrace();}
			}
		}
		
		super.wander();
	}
}
