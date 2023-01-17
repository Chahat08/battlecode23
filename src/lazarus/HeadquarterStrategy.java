package lazarus;

import battlecode.common.*;

import static lazarus.RobotPlayer.directions;
import static lazarus.RobotPlayer.rng;

public class HeadquarterStrategy {
     static WellInfo[] wells;
    /**
     * Run a single turn for a Headquarters.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runHeadquarters(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        if (wells == null){
            scanWells(rc);
            for (WellInfo w : wells){
                System.out.println("Well found:");
                System.out.println(w.getResourceType());
                System.out.println(w.getMapLocation());
            }
        }
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        if (rc.canBuildAnchor(Anchor.STANDARD) && rc.getResourceAmount(ResourceType.ADAMANTIUM) >= 300) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getAnchor());
        }
        if (rng.nextBoolean()) {
            // Let's try to build a carrier.
            rc.setIndicatorString("Trying to build a carrier");
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }
        } else {
            // Let's try to build a launcher.
            rc.setIndicatorString("Trying to build a launcher");
            if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }
        }

    }

    static void scanWells(RobotController rc) throws GameActionException {
        // Scan for wells
        wells = rc.senseNearbyWells();;

    }

    static WellInfo[] getWells(){
        return wells;
    }
}
