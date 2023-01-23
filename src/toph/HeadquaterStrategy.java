package toph;

import battlecode.common.*;
import common.communication.Write;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import toph.SharedArray.SharedArrayAccess;
import static common.utils.Utils.locationToInt;
import static toph.RobotPlayer.*;

public class HeadquaterStrategy {
    static int iter = 0;
    // 3:1 launcher:carrier creation ratio in the beginning of the game

    static final Integer[] startingStrategy = {1,3};
    static final Integer[] firstStrategy = {2,2, 1,1,3};
    static final Integer[] secondStrategy = {2,2, 1,1};

    static final Map<Integer, Integer[]> strategies = new HashMap<Integer, Integer[]>(){{
        put(1, startingStrategy);
        put(2, firstStrategy);
        put(3, secondStrategy);
    }};

    static final int STARTING_STRATEGY_TURNCOUNTS=20;
    static final int FIRST_STRATEGY_TURNCOUNTS=200;
    //static final int SECOND_STRATEGY_TURNCOUNTS=20;

    static Direction dir;
    static MapLocation newLoc;
    static boolean buildRobots = true;

    static final Map<Integer, RobotType>  botTypes = new HashMap<Integer, RobotType>() {{
        put(1, RobotType.LAUNCHER);
        put(2, RobotType.CARRIER);
        put(3, RobotType.AMPLIFIER);
    }};
    static int MAX_BOTS_TO_BUILD_IN_ONE_TURNCOUNT = 2;
    static int currentLauncherSymmetry=1;
    static int INITIAL_DEFENSE_LAUNCHER_RADIUS=10;
    static int[] islands;
    static WellInfo[] wells;
    static MapInfo[] senseMapInfo;
    static void runHeadquaters(RobotController rc) throws GameActionException {
        // things to do in first turnCount
        if(turnCount==1) firstTurnCountRoutine(rc);

        // try to build an anchor every 50th turn?
        if(turnCount%40==0 && turnCount > 100) buildAnchor(rc);

        if(buildRobots) {
            // lets build multiple bots
            buildMultipleBots(rc);
        }



        // starvation strategy
        // check if resources are at a good level and do we need to stop feeding or what?

        // communication strategy bus method
        //Communication.tryWriteMessages(rc);

    }

    static void firstTurnCountRoutine(RobotController rc) throws GameActionException{


        // TODO: put all info in shared array
        SharedArrayWork.writeDefenseLauncherRadius(rc, INITIAL_DEFENSE_LAUNCHER_RADIUS, rc.getLocation());

    }



    static void buildAnchor(RobotController rc) throws GameActionException {
        //TODO: if not able to build, send carriers to collect resources of types required to build
        // in +5 turnCount
        // signal starvation of resources and change things accordingly

        if(rc.canBuildAnchor(Anchor.STANDARD) && rc.getNumAnchors(Anchor.STANDARD) < 2 && rc.getResourceAmount(ResourceType.ADAMANTIUM) > 100) {
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("BUILDING ANCHOR");
            buildRobots = true;
        }
        else{
            if(rc.getNumAnchors(Anchor.STANDARD) == 0) {
                buildRobots = false;
            }
            else{
                buildRobots = true;
            }
        }
    }

    static void buildMultipleBots(RobotController rc) throws GameActionException{
        int i=0;
        while(i++<MAX_BOTS_TO_BUILD_IN_ONE_TURNCOUNT){
            int createBot = 0, strategyNumber = 0;

            // pick a strategy, follow it
            if(turnCount<=STARTING_STRATEGY_TURNCOUNTS){
                createBot = startingStrategy[iter]; // what to build
                strategyNumber=1; // pick a
            }
            else if(turnCount<=FIRST_STRATEGY_TURNCOUNTS){
                if(turnCount==STARTING_STRATEGY_TURNCOUNTS+1) iter=0; //reset iter
                createBot = firstStrategy[iter]; // what to build
                strategyNumber=2;
            }
            else {
                if(turnCount==FIRST_STRATEGY_TURNCOUNTS+1) iter=0; //reset iter
                createBot = secondStrategy[iter]; // what to build
                strategyNumber=3;
            }


            int posloc = findbuildableloc(rc, botTypes.get(createBot)); // where to build
            // if possible build it else wait, posloc ensures it can be build n in that loc
            if(rc.isActionReady() && posloc != -1) iter = buildbot(rc, iter, posloc, createBot, strategyNumber);
            else rc.setIndicatorString("WAITING");


        }
    }

    static int buildbot(RobotController rc, int iter, int posloc, int createBot, int strategyNumber) throws GameActionException {
        dir = directions[posloc]; // direction to build
        newLoc = rc.getLocation().add(dir); // location to build
        if(rc.canBuildRobot(botTypes.get(createBot), newLoc))
        {
            rc.buildRobot(botTypes.get(createBot), newLoc); // what and where
            if(botTypes.get(createBot)==RobotType.LAUNCHER) {
                currentLauncherSymmetry+=1;
                if(currentLauncherSymmetry>symmetries.size()) currentLauncherSymmetry=1;
            }
        }

        return (iter >= strategies.get(strategyNumber).length-1) ? 0 : iter + 1; // iter control
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
