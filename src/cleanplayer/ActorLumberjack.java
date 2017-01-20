package cleanplayer;

import battlecode.common.*;

public class ActorLumberjack extends ActorRobot {
	
	public ActorLumberjack(RobotController rc) {
		super(rc);
	}
	
	public void robotAct() {
		updateRoundVars();
		
		if(attackEnemyUnits()){
			//System.out.println("attack");
		} else if(attackEnemyWorkers()) {
			//System.out.println("cut enemy trees");
		} else if(cutEnemyTrees()) {
			//System.out.println("attack workers");
		} else if(cutTrees()) {
			//System.out.println("cut trees");
		} else {
			wander();
			//System.out.println("attack");
		}
		
		closeRoundVars();
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
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 999999f;
		float lowestHealth = 999999f;
		
		for(TreeInfo ti : allTrees) {
			if(ti.team.equals(rc.getTeam().opponent())) {
				float dist = loc.distanceSquaredTo(ti.location);
				if(ti.health < lowestHealth) {
					lowestHealth = ti.health;
					nearestDist = dist;
					nearestTree = ti;
				} else if(ti.health == lowestHealth) {
					if(dist < nearestDist) {
						nearestDist = dist;
						nearestTree = ti;
					}
				}	
			}
		}
		
		
		if(nearestTree.team.equals(rc.getTeam().opponent())) {
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
		}
		return false;
	}
	
	boolean cutTrees() {
		if(allTrees.length ==0) {
			return false;
		}
		TreeInfo nearestTree = allTrees[0];
		float nearestDist = 999999f;
		float lowestHealth = 999999f;
		for(TreeInfo ti : allTrees) {
			if(!ti.team.equals(rc.getTeam()) && !sensor.isTreeSurrounded(nearestTree)) {
				float dist = loc.distanceSquaredTo(ti.location);
				if(ti.health < lowestHealth) {
					lowestHealth = ti.health;
					nearestDist = dist;
					nearestTree = ti;
				} else if(ti.health == lowestHealth) {
					if(dist < nearestDist) {
						nearestDist = dist;
						nearestTree = ti;
					}
				}	
			}
		}
		
		
		if(!nearestTree.team.equals(rc.getTeam()) && !sensor.isTreeSurrounded(nearestTree)) {
			broadcast.broadcastLocation(nearestTree.location, 555);
			if(rc.canShake(nearestTree.location)) {
				try{
					rc.shake(nearestTree.location);
				} catch(Exception e) {e.printStackTrace();}
			}
			//System.out.println(nearestTree.location);
			if(rc.canChop(nearestTree.location)){
				try{
					rc.chop(nearestTree.location);
					return true;
				} catch(Exception e){e.printStackTrace();}
			} else {
				nav.moveToLocation(nearestTree.location);
				return true;
			}
		}
		return false;
	}
	
	public void wander() {
		int msg = 0;
		try{
			msg = rc.readBroadcast(555);
		} catch(Exception e){e.printStackTrace();}
		
		if(msg!=0) {
			MapLocation target = new MapLocation((float) (msg%1000), (float) (msg/1000));
			if(!rc.canSenseLocation(target)) {
				nav.moveToLocation(target);
				return;
			} else {
				try {
					rc.broadcast(586, 0);
					return;
				} catch (GameActionException e) {e.printStackTrace();}
			}
		} else {
			super.wander();
		}
	}
}
