package stormwind;

import battlecode.common.*;

import static stormwind.RobotPlayer.*;

public class HeadquarterStrategy {

    static void runHeadquarters(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);
        if (turnCount == 1) {
            Communication.addHeadquarter(rc);
        } else if (turnCount == 2) {
            Communication.updateHeadquarterInfo(rc);
        }
        if (rc.canBuildAnchor(Anchor.STANDARD) && rc.getResourceAmount(ResourceType.ADAMANTIUM) > 100) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
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
        Communication.tryWriteMessages(rc);

    }
}
