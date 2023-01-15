package samwise;

import battlecode.common.*;

import java.util.Random;

import static samwise.RobotPlayer.directions;

public class HeadquaterStrategy {
    static final Random rng = new Random(6147);
    static void runHeadquaters(RobotController rc) throws GameActionException {



        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        if(rc.canBuildAnchor(Anchor.STANDARD)) {
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("BUILDING ANCHOR: "+rc.getAnchor());
        }


        if(rng.nextBoolean()) {
            rc.setIndicatorString("Trying to build a carrier");
            if(rc.canBuildRobot(RobotType.CARRIER, newLoc)){
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }
        } else {
            rc.setIndicatorString("Trying to build a launcher");
            if(rc.canBuildRobot(RobotType.LAUNCHER, newLoc)){
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }
        }

    }
}
