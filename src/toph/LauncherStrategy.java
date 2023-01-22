package toph;

import battlecode.common.*;

import javax.xml.stream.Location;

import java.util.ArrayList;
import java.util.Map;

import static toph.RobotPlayer.*;


public class LauncherStrategy {
    static boolean hasFinalisedSymmetry = false;

    static final int DEFENSE_LAUNCHER_RATIO = 4;
    static final int MAX_LAUNCHER_BOT_COUNT_PER_HQ = 5;
    static final int MAX_MOVES_PER_TURNCOUNT = 2;
    static final int MAX_ATTACKS_PER_TURNCOUNT = 3;


    static MapLocation currentTargetLocation = null;
    static boolean reachedEnemyHQ = false;
    static MapSymmetry.SymmetryType symmetryType = null;

    static int mapWidth, mapHeight;

    static void runLauncher(RobotController rc) throws GameActionException {

        // let's have every 4th launcher we create remain near our hq for defense
        if(rc.getID()%DEFENSE_LAUNCHER_RATIO!=0) {
            // ATTACK LAUNCHERS
            // launchers tasked to go to enemy hq
            // and explore things on the way

            rc.setIndicatorString("ATTACK LAUNCHER");

            // TODO: CHANGE SYMMETRY/SET NEW TARGET BASED ON DETECTED SYMMETRY IF REACHED TARGET AND HQ NOT FOUND

            // things to do just after creation
            if (turnCount == 1) attackLaunchersFirstTurnCountRoutine(rc);

            // every third turncount, detect nearby things
            if (turnCount % 3 == 0) detectNearbyThings(rc);

            if (!reachedEnemyHQ && turnCount % 2 == 1) { // MOVE every other turn
                moveToCurrentTargetLocation(rc);
            } else { // ATTACK every other turn
                attackEnemies(rc);
            }
            if(!hasFinalisedSymmetry) tryToFinaliseOrChangeSymmetry(rc);

        }
        else{
            // DEFENSE LAUNCHERS
            // Launchers tasked to defend our headquaters
            // remain near it and kill enemies approaching it

            rc.setIndicatorString("DEFENSE LAUNCHER");

            if(turnCount==1) defenseLaunchersFirstTurnCountRoutine(rc);
            attackEnemies(rc);
            moveRandomlyNearOurHQ(rc);
        }
    }

    static void attackLaunchersFirstTurnCountRoutine(RobotController rc) throws GameActionException{

        // SET THE CURRENT TARGET LOCATION TO A PLAUSIBLE HQ LOCATION

        if (SharedArrayWork.readMapSymmetry(rc) == null) {
            // we dont yet know the symmetry type
            // pick a symmetry randomly, move to a plausible enemy HQ according to this info
            // TODO: rotational symmetry seems to be more freq. do a 2:1:1 ratio of picking it vs others

            symmetryType = SharedArrayWork.readCurrentLauncherSymmetryType(rc);
            SharedArrayWork.writeIncreaseCurrentLauncherSymmetryType(rc, symmetryType);
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, getBirthHQLocation(rc), symmetryType);

            // TODO: fix these null checks for symmetry detection
//            do {
//                MapSymmetry.SymmetryType symmetryType = SharedArrayWork.readCurrentLauncherSymmetryType(rc);
//                SharedArrayWork.writeIncreaseCurrentLauncherSymmetryType(rc, symmetryType);
//                currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, rc.getLocation(), symmetryType);
//            } while (currentTargetLocation==null);

        } else {
            // we can detect enemy HQ corresponding to this one, since symmetry is known, set it as target
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, getBirthHQLocation(rc), SharedArrayWork.readMapSymmetry(rc));//MapSymmetry.getSymmetricalMapLocation(rc, rc.getLocation(), MapSymmetry.MAP_SYMMETRY_TYPE);
        }
    }

    static void defenseLaunchersFirstTurnCountRoutine(RobotController rc) throws GameActionException{
        currentTargetLocation = getBirthHQLocation(rc);
        mapHeight=rc.getMapHeight(); mapWidth=rc.getMapWidth();
    }

    static void moveToCurrentTargetLocation(RobotController rc) throws GameActionException{
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
    }
    static MapLocation getBirthHQLocation(RobotController rc) throws GameActionException{
        MapLocation[] possibleHqLocs = SharedArrayWork.readOurHQLocations(rc);
        for(int i=0; i<possibleHqLocs.length; ++i){
            if(possibleHqLocs[i].isAdjacentTo(rc.getLocation())) {
                return possibleHqLocs[i];
            }
        }
        return rc.getLocation();
    }

    static boolean checkLocationInMap(int x, int y){
        if(x<0 || y<0|| x>=mapWidth || y>=mapHeight) return false;
        return true;
    }
    static MapLocation pickRandomNewLocationNearHQ(RobotController rc) throws GameActionException{
        int radius = SharedArrayWork.readDefenseLauncherRadius(rc, currentTargetLocation);
        int x, y;
        do{
            int addX = rng.nextInt((radius - (-radius)) + 1) + (-radius);
            int addY = rng.nextInt((radius - (-radius)) + 1) + (-radius);
            x=currentTargetLocation.x+addX; y= currentTargetLocation.y+addY;
        } while(checkLocationInMap(x, y));
        return new MapLocation(x, y);
    }
    static void moveRandomlyNearOurHQ(RobotController rc) throws GameActionException {
        // defense robots which will stay near our headquaters only
        int i=0;
        //while(i++<MAX_MOVES_PER_TURNCOUNT) {
            while (true) { // just finding a random direction to move in, wonder if iterating is better
                Direction dir = rc.getLocation().directionTo(pickRandomNewLocationNearHQ(rc));
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    break;
                }
          //  }
        }
    }

    static void attackEnemies(RobotController rc) throws GameActionException {
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();

        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);

        if (enemies.length >= 0) { // enemies found
            for (RobotInfo enemy : enemies) {
                // TODO: this is not it
                if (enemy.getType() == RobotType.HEADQUARTERS){
                    reachedEnemyHQ = true;
                }
                else {
                //if(isAtEnemyHQ){ //TODO: maybe try more than one attack
                    // attack any other enemy bots detected
                    if(rc.canAttack(enemy.getLocation())){
                        rc.attack(enemy.getLocation());
                    }
                }
            }
        }

    }

    static void tryToFinaliseOrChangeSymmetry(RobotController rc) throws GameActionException{
        MapSymmetry.SymmetryType symmetry = SharedArrayWork.readMapSymmetry(rc);

        // change symmetry to send bots,if symmetry has already been detected
        if(symmetry!=null){
            symmetryType = symmetry;
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, currentTargetLocation, symmetryType);
            hasFinalisedSymmetry=true;
            return;
        }
        if(rc.canSenseRobotAtLocation(currentTargetLocation)) {
            System.out.println("haha");
            System.out.println(currentTargetLocation);
            RobotInfo bot = rc.senseRobotAtLocation(currentTargetLocation);
            if (bot.getType() == RobotType.HEADQUARTERS) {
                System.out.println("here");
                SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                System.out.println("-------------------------------");
                System.out.println("!!!!!!!!MAP SYMMETRY SET!!!!!!!");
                MapSymmetry.SymmetryType symmet = SharedArrayWork.readMapSymmetry(rc);
                if (symmet == MapSymmetry.SymmetryType.ROTATIONAL)
                    System.out.println("ROTATION");
                if (symmet == MapSymmetry.SymmetryType.HORIZONTAL) System.out.println("HORI");
                if (symmet == MapSymmetry.SymmetryType.VERTICAL) System.out.println("VERTI");
                System.out.println("-------------------------------");
                hasFinalisedSymmetry = true;
            }
        }
    }

    static void detectNearbyThings(RobotController rc) throws GameActionException {
            // TODO: detection logic

    }


}



