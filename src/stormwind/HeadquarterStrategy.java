package stormwind;

import battlecode.common.*;

import static stormwind.RobotPlayer.*;

public class HeadquarterStrategy {
    static int pos = 0;
    static boolean combotsetup = true;
    static void runHeadquarters(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);


        // used so each of them knows where every other headquarter
        if (turnCount == 1) {
            pos = Communication.addHeadquarter(rc); // position in array found, also tells which is the first base
        }
        else if (turnCount == 2) {
            Communication.updateHeadquarterInfo(rc);

            // setting up communication bots
            rc.writeSharedArray(62, 1);

            rc.buildRobot(RobotType.CARRIER, newLoc);
            rc.setIndicatorString("Updated value to 1 n created communicator bots ");
        }
        else {
            rc.writeSharedArray(62, 0);
            if (rc.canBuildAnchor(Anchor.STANDARD) && rc.getResourceAmount(ResourceType.ADAMANTIUM) > 100) {
                rc.buildAnchor(Anchor.STANDARD);
            }
            if (rng.nextBoolean()) {
                if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                    rc.buildRobot(RobotType.CARRIER, newLoc);
                }
            } else {
                if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                    rc.buildRobot(RobotType.LAUNCHER, newLoc);
                }
            }
            Communication.tryWriteMessages(rc);
        }
    }
}
