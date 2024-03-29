package testplayer;

import battlecode.common.*;
import common.communication.Write;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static testplayer.RobotPlayer.*;

public class HeadquaterStrategy {
    static int iter = 0;
    // 3:1 launcher:carrier creation ratio in the beginning of the game
    static final int[] startingStrategy = {1,1,1,1,2,2,2,2,3};
    static Direction dir;
    static MapLocation newLoc;

    static Map<Integer, RobotType>  botTypes = new HashMap<Integer, RobotType>() {{
        put(1, RobotType.LAUNCHER);
        put(2, RobotType.CARRIER);
        put(3, RobotType.AMPLIFIER);
    }};
    static int[] islands;
    static WellInfo[] wells;
    static MapInfo[] senseMapInfo;
    static void runHeadquaters(RobotController rc) throws GameActionException {

        // get information
        // optimization check: do we need/want to read it again n again
        islands = rc.senseNearbyIslands();
        wells = rc.senseNearbyWells();
        senseMapInfo = rc.senseNearbyMapInfos(rc.getType().visionRadiusSquared);

        // why adding it again n again?
        Write.addOurHQLocation(rc);

        // TODO: build anchor at apt time
//        if(rc.canBuildAnchor(Anchor.STANDARD)) {
//            rc.buildAnchor(Anchor.STANDARD);
//            rc.setIndicatorString("BUILDING ANCHOR");
//        }

        // note: understand and improve this based on cooldown info
        int createBot = startingStrategy[iter]; // what to build
        int posloc = findbuildableloc(rc, botTypes.get(createBot)); // where to build
        // if possible build it else wait, posloc ensures it can be build n in that loc
        if(rc.isActionReady() && posloc != -1) iter = buildbot(rc, iter, posloc, createBot);
        else rc.setIndicatorString("WAITING");
    }

    static int buildbot(RobotController rc, int iter, int posloc, int createBot) throws GameActionException {
        dir = directions[posloc]; // direction to build
        newLoc = rc.getLocation().add(dir); // location to build
        rc.buildRobot(botTypes.get(createBot), newLoc); // what and where
        return (iter >= startingStrategy.length-1) ? 0 : iter + 1; // iter control
    }

    static int findbuildableloc(RobotController rc, RobotType r) throws GameActionException {
        newLoc = rc.getLocation();
        for(int i=0; i<directions.length; i++) {
            newLoc = rc.getLocation().add(directions[i]);
            if(rc.canBuildRobot(r, newLoc)) return i;
        }
        return -1;
    }
}
