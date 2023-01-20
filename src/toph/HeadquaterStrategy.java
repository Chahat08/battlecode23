package toph;

import battlecode.common.*;
import common.communication.Write;

import java.util.HashMap;
import java.util.Map;

import static toph.RobotPlayer.directions;
import static toph.RobotPlayer.turnCount;

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
    static int MAX_BOTS_TO_BUILD_IN_ONE_TURNCOUNT = 2;
    static int[] islands;
    static WellInfo[] wells;
    static MapInfo[] senseMapInfo;
    static void runHeadquaters(RobotController rc) throws GameActionException {

        // things to do in first turnCount
        if(turnCount==1) firstTurnCountRoutine(rc);

        // try to build an anchor every 50th turn?
        if(turnCount%50==0) buildAnchor(rc);

        // lets build multiple bots
        buildMultipleBots(rc);
    }

    static void firstTurnCountRoutine(RobotController rc) throws GameActionException{
        // TODO: put all info in shared array

        islands = rc.senseNearbyIslands();
        wells = rc.senseNearbyWells();
        senseMapInfo = rc.senseNearbyMapInfos(rc.getType().visionRadiusSquared);

        Write.addOurHQLocation(rc);

        MapSymmetry.setMapDimensions(rc.getMapHeight(), rc.getMapWidth());
    }

    static void buildAnchor(RobotController rc) throws GameActionException {
        //TODO: if not able to build, send carriers to collect resources of types required to build
        // in +5 turnCount

        if(rc.canBuildAnchor(Anchor.STANDARD)) {
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("BUILDING ANCHOR");
        }
    }

    static void buildMultipleBots(RobotController rc) throws GameActionException{
        int i=0;
        while(i++<MAX_BOTS_TO_BUILD_IN_ONE_TURNCOUNT){
            int createBot = startingStrategy[iter]; // what to build
            int posloc = findbuildableloc(rc, botTypes.get(createBot)); // where to build
            // if possible build it else wait, posloc ensures it can be build n in that loc
            if(rc.isActionReady() && posloc != -1) iter = buildbot(rc, iter, posloc, createBot);
            else rc.setIndicatorString("WAITING");
        }
    }

    static int buildbot(RobotController rc, int iter, int posloc, int createBot) throws GameActionException {
        dir = directions[posloc]; // direction to build
        newLoc = rc.getLocation().add(dir); // location to build
        if(rc.canBuildRobot(botTypes.get(createBot), newLoc))
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
