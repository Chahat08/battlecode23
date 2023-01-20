package toph;

import battlecode.common.*;

import static toph.RobotPlayer.*;


public class LauncherStrategy {
    static boolean isAtEnemyHQ = false;
    static boolean didIncreaseCount = false;

    static final int DEFENSE_LAUNCHER_RATIO = 4;
    static final int MAX_LAUNCHER_BOT_COUNT_PER_HQ = 5;
    static final int MAX_MOVES_PER_TURNCOUNT = 2;
    static final int MAX_ATTACKS_PER_TURNCOUNT = 3;


    static MapLocation currentTargetLocation = null;
    static boolean reachedEnemyHQ = false;

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

        if (MapSymmetry.MAP_SYMMETRY_TYPE == null) {
            // we dont yet know the symmetry type
            // pick a symmetry randomly, move to a plausible enemy HQ according to this info
            // TODO: rotational symmetry seems to be more freq. do a 2:1:1 ratio of picking it vs others

            MapSymmetry.SymmetryType symmetryType = symmetries.get(rng.nextInt(symmetries.size()));
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, rc.getLocation(), symmetryType);

        } else {
            // we can detect enemy HQ corresponding to this one, since symmetry is known, set it as target
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, rc.getLocation(), MapSymmetry.MAP_SYMMETRY_TYPE);
        }
    }

    static void defenseLaunchersFirstTurnCountRoutine(RobotController rc) throws GameActionException{
        currentTargetLocation=rc.getLocation();
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

    static void moveRandomlyNearOurHQ(RobotController rc) throws GameActionException {
        // defense robots which will stay near our headquaters only
        int i=0;
        while(i++<MAX_MOVES_PER_TURNCOUNT) {
            while (true) { // just finding a random direction to move in, wonder if iterating is better
                Direction dir = rc.getLocation().directionTo(currentTargetLocation.add(directions[rng.nextInt(directions.length)]));
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    break;
                }
            }
        }
    }

    static void attackEnemies(RobotController rc) throws GameActionException {
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();

        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);

        if (enemies.length >= 0) { // enemies found
            for (RobotInfo enemy : enemies) {
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

    static void detectNearbyThings(RobotController rc) throws GameActionException {
            // TODO: detection logic
    }
}



