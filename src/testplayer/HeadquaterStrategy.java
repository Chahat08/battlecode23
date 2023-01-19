package testplayer;

import battlecode.common.*;
import common.communication.Write;

import java.util.Random;

import static testplayer.RobotPlayer.directions;


public class HeadquaterStrategy {
    static final Random rng = new Random(6147);
    static int numCarriers = 0;
    static int numLaunchers = 0;

    // 3:1 launcher:carrier creation ratio in the beginning of the game
    static final int[] startingStrategy = {1,1,2,3};
    static int iter = 0;


    static void runHeadquaters(RobotController rc) throws GameActionException {

        // transmit information
        int[] islands = rc.senseNearbyIslands(); // island indices
        WellInfo[] wells = rc.senseNearbyWells();
        MapInfo[] senseMapInfo = rc.senseNearbyMapInfos(rc.getType().visionRadiusSquared);

        Write.addOurHQLocation(rc);


        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        // TODO: build anchor at apt time
//        if(rc.canBuildAnchor(Anchor.STANDARD)) {
//            rc.buildAnchor(Anchor.STANDARD);
//            rc.setIndicatorString("BUILDING ANCHOR");
//        }


        // starting strategy implementation
        int createBot = startingStrategy[iter++];
        if(iter>=startingStrategy.length) iter = 0;

        if(createBot==1) {
            rc.setIndicatorString("Trying to build a launcher");
            if(rc.canBuildRobot(RobotType.LAUNCHER, newLoc)){
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }

        } else if(createBot==2){
            rc.setIndicatorString("Trying to build a carrier");
            if(rc.canBuildRobot(RobotType.CARRIER, newLoc)){
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }
        }
        else{
            rc.setIndicatorString("Trying to build a amplifier");
            if(rc.canBuildRobot(RobotType.AMPLIFIER, newLoc)){
                rc.buildRobot(RobotType.AMPLIFIER, newLoc);
            }
        }
    }
}
