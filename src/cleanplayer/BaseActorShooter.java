package cleanplayer;

import battlecode.common.*;

public class BaseActorShooter extends BaseActor {
	
	public BaseActorShooter(RobotController rc) {
		super(rc);
		nav = new ModAdvNav(this, rc);
		System.out.println(nav.hi());
	}
}
