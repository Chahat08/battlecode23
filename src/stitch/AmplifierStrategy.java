package stitch;

import battlecode.common.*;

import static stitch.LauncherStrategy.getBirthHQLocation;
import static stitch.RobotPlayer.*;
public class AmplifierStrategy {
    static boolean isAtTargetLocation = false;
    static MapLocation currentTargetLocation = null;
    static MapSymmetry.SymmetryType symmetryType = null;
    static boolean symmetrySet = false;

    static int MAX_MOVES_PER_TURNCOUNT = 5;

    static RobotInfo[] enemyInfo;
    static MapLocation[] ourHQLocs;

    static void runAmplifier(RobotController rc) throws GameActionException {

        if(turnCount==1) amplifierFirstTurnCountRoutine(rc);
        if(!isAtTargetLocation) moveToMyCurrentTargetLocation(rc);
        //if(!symmetrySet) tryToSetSymmetry(rc);
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
            symmetrySet=true;
            ourHQLocs = SharedArrayWork.readOurHQLocations(rc);
            currentTargetLocation = getRandomLocationOnMap(rc);
//            if(rng.nextBoolean())
//                currentTargetLocation = MapSymmetry.getSymmetricalMapLocation(rc, getBirthHQLocation(rc), symmetryType);
//            else currentTargetLocation = getRandomLocationOnMap(rc);
        }

//        System.out.println("AMPLIFIER: "+currentTargetLocation);
    }
    static MapLocation getRandomLocationOnMap(RobotController rc) throws GameActionException{
        return new MapLocation(rng.nextInt(rc.getMapWidth()), rng.nextInt(rc.getMapHeight()));
    }

    static MapLocation detectEnemies(RobotController rc) throws GameActionException{
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();

        enemyInfo = rc.senseNearbyRobots(radius, opponent);
        if (enemyInfo.length > 0) { // enemies found
            for (RobotInfo enemy : enemyInfo) {
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
        if(symmetrySet) {
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
            if(!symmetrySet) tryToSetSymmetry(rc);
        }

    }

    static void tryToSetSymmetry(RobotController rc) throws GameActionException {
        if(symmetrySet) return;
        // try to confirm via checking at current target location
        if (rc.canSenseRobotAtLocation(currentTargetLocation)) {
            RobotInfo info = rc.senseRobotAtLocation(currentTargetLocation);
            if (info.getType().equals(RobotType.HEADQUARTERS)) {
                isAtTargetLocation = true;
                if (!symmetrySet && SharedArrayWork.readMapSymmetry(rc) == null) {
                    SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                    symmetrySet = true;
                }
            }
        } else {
            for (RobotInfo bot : enemyInfo) {
                if (bot.getType() == RobotType.HEADQUARTERS) {
                    for (MapLocation ourHQ : ourHQLocs) {
                        if (ourHQ.equals(MapSymmetry.getSymmetricalMapLocation(rc, bot.getLocation(), MapSymmetry.SymmetryType.ROTATIONAL))) {
                            symmetryType = MapSymmetry.SymmetryType.ROTATIONAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        } else if (ourHQ.equals(MapSymmetry.getSymmetricalMapLocation(rc, bot.getLocation(), MapSymmetry.SymmetryType.HORIZONTAL))) {
                            symmetryType = MapSymmetry.SymmetryType.HORIZONTAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        } else if (ourHQ.equals(MapSymmetry.getSymmetricalMapLocation(rc, bot.getLocation(), MapSymmetry.SymmetryType.VERTICAL))) {
                            symmetryType = MapSymmetry.SymmetryType.VERTICAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        }
                    }
                }
            }
        }
        // if symmetry still not set, try to set via looking at other features
        if(!symmetrySet){
            WellInfo[] wellInfos = rc.senseNearbyWells();

            MapLocation[] adaMapLocations = SharedArrayWork.readWellLocationsOfType(rc, ResourceType.ADAMANTIUM);
            MapLocation[] manMapLocations = SharedArrayWork.readWellLocationsOfType(rc, ResourceType.MANA);

            for(WellInfo wellInfo:wellInfos){
                if(wellInfo.getResourceType().equals(ResourceType.ADAMANTIUM)){
                    for(MapLocation mapLocation:adaMapLocations){
                        if(mapLocation.equals(MapSymmetry.getSymmetricalMapLocation(rc, wellInfo.getMapLocation(), MapSymmetry.SymmetryType.ROTATIONAL))){
                            symmetryType = MapSymmetry.SymmetryType.ROTATIONAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        }else if(mapLocation.equals(MapSymmetry.getSymmetricalMapLocation(rc, wellInfo.getMapLocation(), MapSymmetry.SymmetryType.HORIZONTAL))){
                            symmetryType = MapSymmetry.SymmetryType.HORIZONTAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        }else if(mapLocation.equals(MapSymmetry.getSymmetricalMapLocation(rc, wellInfo.getMapLocation(), MapSymmetry.SymmetryType.VERTICAL))){
                            symmetryType = MapSymmetry.SymmetryType.VERTICAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        }
                    }
                }
                else if(wellInfo.getResourceType().equals(ResourceType.MANA)){
                    for(MapLocation mapLocation:manMapLocations){
                        if(mapLocation.equals(MapSymmetry.getSymmetricalMapLocation(rc, wellInfo.getMapLocation(), MapSymmetry.SymmetryType.ROTATIONAL))){
                            symmetryType = MapSymmetry.SymmetryType.ROTATIONAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        }else if(mapLocation.equals(MapSymmetry.getSymmetricalMapLocation(rc, wellInfo.getMapLocation(), MapSymmetry.SymmetryType.HORIZONTAL))){
                            symmetryType = MapSymmetry.SymmetryType.HORIZONTAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        }else if(mapLocation.equals(MapSymmetry.getSymmetricalMapLocation(rc, wellInfo.getMapLocation(), MapSymmetry.SymmetryType.VERTICAL))){
                            symmetryType = MapSymmetry.SymmetryType.VERTICAL;
                            symmetrySet = true;
                            SharedArrayWork.writeMapSymmetry(rc, symmetryType);
                        }
                    }
                }
            }
        }

        if(symmetrySet) currentTargetLocation=getRandomLocationOnMap(rc);
    }

}
