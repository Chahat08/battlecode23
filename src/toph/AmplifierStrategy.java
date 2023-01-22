package toph;

import battlecode.common.*;
import common.communication.Read;
import common.communication.Write;

import java.util.HashMap;

import static toph.LauncherStrategy.getBirthHQLocation;
import static toph.RobotPlayer.*;

public class AmplifierStrategy {
    static boolean isAtEnemyHQ = false;
    static MapLocation currentTargetLocation = null;

    static int MAX_MOVES_PER_TURNCOUNT = 2;

    static void runAmplifier(RobotController rc) throws GameActionException {
        if(turnCount==1) amplifierFirstTurnCountRoutine(rc);
        if(!isAtEnemyHQ) moveToMyCurrentTargetLocation(rc);
    }
    static void amplifierFirstTurnCountRoutine(RobotController rc) throws GameActionException{
        if(SharedArrayWork.readMapSymmetry(rc)==null){
            MapSymmetry.SymmetryType symmetryType = SharedArrayWork.readCurrentAmplifierSymmetryType(rc);
            SharedArrayWork.writeIncreaseCurrentAmplifierSymmetryType(rc, symmetryType);
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, getBirthHQLocation(rc), symmetryType);
        }
        else {
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, getBirthHQLocation(rc), SharedArrayWork.readMapSymmetry(rc));
        }

        System.out.println("AMPLIFIER: "+currentTargetLocation);
    }
    static void moveToMyCurrentTargetLocation(RobotController rc) throws GameActionException{
        Direction dir = rc.getLocation().directionTo(currentTargetLocation);
        int i=0;
        while(i++<MAX_MOVES_PER_TURNCOUNT) {
            if (rc.canMove(dir))
                rc.move(dir);
            else {// TODO: pathfinding otherwise, move randomly for now
                while(true){ // just finding a random direction to move in, wonder if iterating is better
                    dir = directions[rng.nextInt(directions.length)];
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                        break;
                    }
                }
            }
        }
        if(rc.canSenseRobotAtLocation(currentTargetLocation)){
            RobotInfo info = rc.senseRobotAtLocation(currentTargetLocation);
            if(info.getType().equals(RobotType.HEADQUARTERS))
                isAtEnemyHQ=true;
        }
    }

}
