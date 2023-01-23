package toph;

import battlecode.common.*;

import static toph.RobotPlayer.turnCount;
import static toph.Constants.rng;
import static toph.Constants.directions;

// TODO: try to make launchers sit in clouds
public class LauncherStrategy {
    static boolean hasFinalisedSymmetry = false;

    static final int DEFENSE_LAUNCHER_RATIO = 4;
    static final int MAX_LAUNCHER_BOT_COUNT_PER_HQ = 5;
    static final int MAX_MOVES_PER_TURNCOUNT = 5;
    static final int MAX_ATTACKS_PER_TURNCOUNT = 5;

    static MapLocation birthHQ=null;

    static MapLocation currentTargetLocation = null;
    static boolean reachedEnemyHQ = false;
    static MapSymmetry.SymmetryType symmetryType = null;

    static int mapWidth, mapHeight;
    static boolean runback = false;

    static int[] dirIdx = {0,4,2,6,3,7,1,5};
    static int chosenDirIdx = 0;

    //static boolean stayAwayAttackLauncher = false;
    static void runLauncher(RobotController rc) throws GameActionException {


        // let's have every 4th launcher we create remain near our hq for defense
        if(rc.getID()%DEFENSE_LAUNCHER_RATIO!=0) {

            // ATTACK LAUNCHERS
            // launchers tasked to go to enemy hq
            // and explore things on the way


            // TODO: CHANGE SYMMETRY/SET NEW TARGET BASED ON DETECTED SYMMETRY IF REACHED TARGET AND HQ NOT FOUND

            // things to do just after creation
            if (turnCount == 1) attackLaunchersFirstTurnCountRoutine(rc);

            if (!reachedEnemyHQ && turnCount % 2 == 1) { // MOVE every other turn
                moveToCurrentTargetLocation(rc);
            } else { // ATTACK every other turn
                attackEnemies(rc);
            }
            if(!hasFinalisedSymmetry) tryToFinaliseOrChangeSymmetry(rc);
            //System.out.println("attack, "+turnCount+", "+currentTargetLocation);
            rc.setIndicatorString("ATTACK LAUNCHER, target: "+currentTargetLocation);


        }
        else{
            // DEFENSE LAUNCHERS
            // Launchers tasked to defend our headquaters
            // remain near it and kill enemies approaching it



            if(turnCount==1) defenseLaunchersFirstTurnCountRoutine(rc);
            attackEnemies(rc);
            attackHQSpottedEnemies(rc);
            //moveRandomlyNearOurHQ(rc);
            rc.setIndicatorString("DEFENSE LAUNCHER, target: "+currentTargetLocation);
            //System.out.println("def, "+turnCount+", "+currentTargetLocation);
        }
    }

    static void attackLaunchersFirstTurnCountRoutine(RobotController rc) throws GameActionException{
//        scanHQ(rc);
        // SET THE CURRENT TARGET LOCATION TO A PLAUSIBLE HQ LOCATION

        if (SharedArrayWork.readMapSymmetry(rc) == null) {
            // we dont yet know the symmetry type
            // pick a symmetry randomly, move to a plausible enemy HQ according to this info
            // TODO: rotational symmetry seems to be more freq. do a 2:1:1 ratio of picking it vs others

            symmetryType = SharedArrayWork.readCurrentLauncherSymmetryType(rc);
            SharedArrayWork.writeIncreaseCurrentLauncherSymmetryType(rc, symmetryType);
            birthHQ = getBirthHQLocation(rc);
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, birthHQ, symmetryType);

            // TODO: fix these null checks for symmetry detection

        } else {
            // we can detect enemy HQ corresponding to this one, since symmetry is known, set it as target
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, getBirthHQLocation(rc), SharedArrayWork.readMapSymmetry(rc));//MapSymmetry.getSymmetricalMapLocation(rc, rc.getLocation(), MapSymmetry.MAP_SYMMETRY_TYPE);
            hasFinalisedSymmetry=true;
        }
    }

    static void defenseLaunchersFirstTurnCountRoutine(RobotController rc) throws GameActionException{
        //currentTargetLocation = getBirthHQLocation(rc);
        //mapHeight=rc.getMapHeight(); mapWidth=rc.getMapWidth();
        Direction dir = null;
        int j=0;
        while(j++<2) {
            for (chosenDirIdx = 0; ; ++chosenDirIdx) {
                dir = directions[dirIdx[chosenDirIdx]];
                if(chosenDirIdx==directions.length-1) chosenDirIdx=0;
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    break;
                }
            }
        }
    }

    static void moveToCurrentTargetLocation(RobotController rc) throws GameActionException{
        Direction dir = rc.getLocation().directionTo(currentTargetLocation);
        int i=0;
        while(i++<MAX_MOVES_PER_TURNCOUNT) {
            if(rc.getLocation().isAdjacentTo(currentTargetLocation)) {
                reachedEnemyHQ=true;
                break;
            }
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

    static MapLocation pickRandomNewLocationNearHQ(RobotController rc) throws GameActionException{
        int radius = SharedArrayWork.readDefenseLauncherRadius(rc, currentTargetLocation);
        int addX = rng.nextInt((radius - (-radius)) + 1) + (-radius);
        int addY = rng.nextInt((radius - (-radius)) + 1) + (-radius);
        return currentTargetLocation.translate(addX, addY);
    }
    static void moveRandomlyNearOurHQ(RobotController rc) throws GameActionException {
        // defense robots which will stay near our headquaters only
        int i=0;
        while(i++<MAX_MOVES_PER_TURNCOUNT) {
            while (true) { // just finding a random direction to move in, wonder if iterating is better
                Direction dir = rc.getLocation().directionTo(pickRandomNewLocationNearHQ(rc).add(directions[rng.nextInt(directions.length)]));
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

        if (enemies.length > 0) { // enemies found
            for (RobotInfo enemy : enemies) {
                // TODO: this is not it
                if (enemy.getType() == RobotType.HEADQUARTERS){
                    MapLocation loc = enemy.getLocation();
                    if(loc.equals(currentTargetLocation)||loc.isAdjacentTo(currentTargetLocation)) {
//                        Direction dir = rc.getLocation().directionTo(loc);
//                        if (rc.canMove(dir)) rc.move(dir);
                        reachedEnemyHQ= true;
                    }
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
    static void attackHQSpottedEnemies(RobotController rc) throws GameActionException{
        MapLocation[] locations = SharedArrayWork.readHQSpottedEnemies(rc);
        if(locations.length>0){
            for(int i=0; i<locations.length; ++i){
                if(rc.canSenseRobotAtLocation(locations[i])){
                    RobotInfo enemyBotInfo = rc.senseRobotAtLocation(locations[i]);
                    if(enemyBotInfo.getTeam().equals(rc.getTeam().opponent()) && rc.canAttack(locations[i]))
                        rc.attack(locations[i]);
                    break;
                }

                Direction dir = rc.getLocation().directionTo(locations[i]);
                if(rc.canMove(dir))
                    rc.move(dir);
            }
        }
    }

    static void tryToFinaliseOrChangeSymmetry(RobotController rc) throws GameActionException{
        MapSymmetry.SymmetryType symmetry = SharedArrayWork.readMapSymmetry(rc);

        // change symmetry to send bots,if symmetry has already been detected
        if(symmetry!=null){
            symmetryType = symmetry;
            currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, birthHQ, symmetryType);
            hasFinalisedSymmetry=true;
        }
    }


}



