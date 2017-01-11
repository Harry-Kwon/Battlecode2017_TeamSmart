package testplayer;

import battlecode.common.*;

public class RobotPlayer {
	
	public static void run(RobotController rc) {
		try{
            RobotType type = rc.getType();
            RobotActor actor;
            switch(type) {
                case ARCHON:
                    actor = new ArchonActor(rc);
                    break;
                case GARDENER:
                    actor = new GardenerActor(rc);
                    break;
                case LUMBERJACK:
                    actor = new LumberjackActor(rc);
                    break;
                /*case SCOUT:
                	actor = new MobileTurretActor(rc);
                	break;
                case SOLDIER:
                    actor = new ScoutActor(rc);
                    break;
                case TANK:
                	actor = new GuardActor(rc);
                	break;*/
                default:
                    actor = new RobotActor(rc);
                    break;
            }
            while(true){
            	actor.act();
                Clock.yield();
            }
        } catch(Exception e) {e.printStackTrace();}
	}
}
