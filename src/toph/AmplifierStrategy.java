package toph;

import battlecode.common.*;


import java.util.HashMap;

import static toph.LauncherStrategy.MAX_LAUNCHER_BOT_COUNT_PER_HQ;
import static toph.MovementStrategy.moveRandom;
import static toph.RobotPlayer.directions;
import static toph.RobotPlayer.rng;
import static toph.RobotPlayer.turnCount;
import static toph.LauncherStrategy.getBirthHQLocation;
public class AmplifierStrategy {
    static boolean isAtTargetLocation = false;
    static MapLocation currentTargetLocation = null;
    static MapSymmetry.SymmetryType symmetryType = null;
    static boolean symmetrySet = false;

    static int MAX_MOVES_PER_TURNCOUNT = 2;

    static void runAmplifier(RobotController rc) throws GameActionException {

        if(turnCount==1) amplifierFirstTurnCountRoutine(rc);
        if(!isAtTargetLocation) moveToMyCurrentTargetLocation(rc);
        rc.setIndicatorString("target: "+currentTargetLocation);
    }
    static void amplifierFirstTurnCountRoutine(RobotController rc) throws GameActionException{
        symmetryType = SharedArrayWork.readMapSymmetry(rc);
        if(symmetryType==null){
            symmetryType = SharedArrayWork.readCurrentLauncherSymmetryType(rc);
            //SharedArrayWork.writeIncreaseCurrentAmplifierSymmetryType(rc, symmetryType);
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, getBirthHQLocation(rc), symmetryType);
        }
        else {
            if(rng.nextBoolean())
                currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, getBirthHQLocation(rc), symmetryType);
            else currentTargetLocation = getRandomLocationOnMap(rc);
            symmetrySet=true;
        }

//        System.out.println("AMPLIFIER: "+currentTargetLocation);
    }
    static MapLocation getRandomLocationOnMap(RobotController rc) throws GameActionException{
        return new MapLocation(rng.nextInt(rc.getMapWidth()), rng.nextInt(rc.getMapHeight()));
    }

    static MapLocation detectEnemies(RobotController rc) throws GameActionException{
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();

        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) { // enemies found
            for (RobotInfo enemy : enemies) {
                RobotType type = enemy.getType();
                if(type==RobotType.LAUNCHER||type==RobotType.DESTABILIZER)
                    return enemy.getLocation();
            }
        }
        return null;
    }
    static void moveToMyCurrentTargetLocation(RobotController rc) throws GameActionException{
        Direction dir = rc.getLocation().directionTo(currentTargetLocation);
        // run from enemies!!
        if(!symmetrySet) {
            MapLocation enemyLoc = detectEnemies(rc);
            if(enemyLoc!=null) dir = rc.getLocation().directionTo(enemyLoc).opposite();
        }
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
            if(info.getType().equals(RobotType.HEADQUARTERS)) {
                isAtTargetLocation = true;
                if(!symmetrySet && SharedArrayWork.readMapSymmetry(rc)==null){
                    SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                    symmetrySet=true;
                }
            }
        }

    }

}
