package cleanplayer;

import battlecode.common.*;

public class RobotPlayer {
	
	public static void run(RobotController rc) {
		try{
            RobotType type = rc.getType();
            BaseActor actor;
            switch(type) {
                case ARCHON:
                    actor = new ActorArchon(rc);
                    break;
                case GARDENER:
                    actor = new ActorGardener(rc);
                    break;
                case LUMBERJACK:
                    actor = new ActorLumberjack(rc);
                    break;
                case SCOUT:
                	actor = new ActorScout(rc);
                	break;
                case SOLDIER:
                    actor = new ActorSoldier(rc);
                    break;
                case TANK:
                	actor = new ActorTank(rc);
                	break;
                default:
                    actor = new BaseActor(rc);
                    break;
            }
            
            while(true){
            	if(rc.getTeamBullets() >= GameConstants.VICTORY_POINTS_TO_WIN*(7.5f + (rc.getRoundNum())*12.5f / 3000f)) {
        			try{
        				rc.donate(GameConstants.VICTORY_POINTS_TO_WIN*(7.5f + (rc.getRoundNum())*12.5f / 3000f));
        			} catch(Exception e) {e.printStackTrace();}
        		}
            	
            	actor.act();
                Clock.yield();
            }
        } catch(Exception e) {e.printStackTrace();}
	}
}
