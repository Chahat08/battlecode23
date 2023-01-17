package samwise;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Random;

import static samwise.RobotPlayer.directions;

public class HeadquaterStrategy {
    static final Random rng = new Random(6147);
    static int numCarriers = 0;
    static int numLaunchers = 0;

    // 3:1 launcher:carrier creation ratio in the beginning of the game
    static final int[] startingStrategy = {1,1,1,2};


    static void runHeadquaters(RobotController rc) throws GameActionException {

        // transmit information
        int[] islands = rc.senseNearbyIslands(); // island indices
        WellInfo[] wells = rc.senseNearbyWells();
        MapInfo[] senseMapInfo = rc.senseNearbyMapInfos(rc.getType().visionRadiusSquared);



        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        if(rc.canBuildAnchor(Anchor.STANDARD)) {
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("BUILDING ANCHOR");
        }


        // starting strategy implementation
        int createBot = startingStrategy[rng.nextInt(startingStrategy.length)];
        if(createBot==2) {
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
