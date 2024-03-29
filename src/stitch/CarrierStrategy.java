package stitch;


import battlecode.common.*;

import static stitch.BotPrivateInfo.wallColiderDir;
import static stitch.MovementStrategy.*;
import static stitch.RobotPlayer.*;
import static stitch.SharedArrayWork.readIslandLocations;
public class CarrierStrategy {

    static MapLocation wellLoc;
    static MapLocation islandLoc;

    static boolean anchorMode = false;

    static boolean randomWellPick= false;

    static boolean adaormana = false;
    // carry resources if near hq, well
    // attack-> throws resources at enemies
    // used to put anchors on sky islands
    // get slower with amount carried
    static void runCarrier(RobotController rc) throws GameActionException {
        if (rc.getID() % 10 == 1 && islandAlert == false) {
            scanHQ(rc);
            rc.setIndicatorString(("WallColliderDir: " + wallColiderDir));
            wallcollider(rc);
            return;
        }
        if(islandAlert == true){
            moveTowards(rc, hqLoc);
            return;
        }
        // what to do for run carrier, hope it has a well to go to
        if(turnCount == 1){
            // well setup in start
            firstTurn(rc);
            rc.setIndicatorString("Focus well at " + wellLoc);
        }

        // check if our island loc is already ours
        if(islandLoc != null && rc.canSenseLocation(islandLoc)){
            Team team = rc.senseTeamOccupyingIsland(rc.senseIsland(islandLoc));
            if(team == rc.getTeam()){
                // we have the island, we can go home
                anchorMode = false;
                pickClosestWell(rc);
            }
        }

        if(wellLoc != null) pickClosestWell(rc);
        if(anchorMode == false && turnCount == 30 && turnCount< 500 ){
            pickWellOfType(rc, ResourceType.MANA);
            rc.setIndicatorString("Refocus well at " + wellLoc);
        }
        else if(turnCount > 500 && anchorMode == false && turnCount % 100 == 0){
            // we have been here for a while, lets go home
            pickAnyWell(rc);
            rc.setIndicatorString("Refocus well at " + wellLoc);
        }


        //Collect from well if close and inventory not full
        if(wellLoc != null && rc.canCollectResource(wellLoc, -1)) rc.collectResource(wellLoc, -1);

        //Transfer resource to headquarters
        depositResource(rc, ResourceType.ADAMANTIUM);
        depositResource(rc, ResourceType.MANA);

        if(anchorMode) {
            if(islandLoc == null) {
                MapLocation[] islands = readIslandLocations(rc);
                int[] islandLocs = rc.senseNearbyIslands();
                MapLocation closest = null;
                for(int isloc: islandLocs) {
                    MapLocation[] island = rc.senseNearbyIslandLocations(isloc);
                    for(MapLocation m: island) {
                        if(closest == null || m.distanceSquaredTo(hqLoc) < closest.distanceSquaredTo(hqLoc)) {
                            closest = m;
                        }
                    }
                }
                if(islands != null) {
                    islandLoc = islands[rng.nextInt(islands.length)];
                    backforthMode = false;
                    update(rc, hqLoc, islandLoc);
//                    System.out.println("Island: " + islandLoc);
                }
                else if(closest != null) {
                    islandLoc = closest;
                    backforthMode = false;
                    update(rc, hqLoc, islandLoc);
                    System.out.println("Island: " + islandLoc);
                }
                else{
                    moveRandom(rc);

                    if(rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(rc.getLocation())) == Team.NEUTRAL) {
                        rc.placeAnchor();
                        anchorMode = false;
                        backforthMode = false;
                        pickClosestWell(rc);
                    }

//                    rc.setIndicatorString("No islands found");
                }
            }
            else {
                moveTowards(rc, islandLoc);
            }

            if(rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(rc.getLocation())) == Team.NEUTRAL) {
                rc.placeAnchor();
                anchorMode = false;
                backforthMode = false;
                pickClosestWell(rc);
            }
        }

        if(rc.canTakeAnchor(hqLoc, Anchor.STANDARD) && rc.getNumAnchors(Anchor.STANDARD) == 0) {
            rc.takeAnchor(hqLoc, Anchor.STANDARD);
            anchorMode = true;
//            rc.setIndicatorString("Taking anchor");
        }



        if(start != null && end != null){
            if(findMode){
                find(rc);
            }
            else if (backforthMode){
                backforth(rc);
            }
        }
        else{
            moveRandomNoRet(rc);
        }
        // if on full capacity move back to hq

        // Occasionally try out the carriers attack
        if (rng.nextInt(20) == 1) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            if (enemyRobots.length > 0) {
                if (rc.canAttack(enemyRobots[0].location)) {
                    rc.attack(enemyRobots[0].location);
                }
            }
        }
    }

    static void firstTurn(RobotController rc) throws GameActionException {
        adaormana = rng.nextBoolean();
        ResourceType type = adaormana ? ResourceType.ADAMANTIUM : ResourceType.MANA;

        scanHQ(rc);
        MapLocation[] wells = SharedArrayWork.readWellLocationsOfType(rc, type);
        if(wells.length>0){
            MapLocation closest = wells[0];
            for(MapLocation well: wells){

                if (well.distanceSquaredTo(hqLoc) < closest.distanceSquaredTo(hqLoc)){
                    closest = well;
                }
            }
            wellLoc= closest;
//            wellLoc= wells[rng.nextInt(wells.length)];
            findMode = true;
            backforthMode = true;
            update(rc, hqLoc, wellLoc);
        }
        else{
//            rc.setIndicatorString("No wells found");
        }
    }

    static void pickAnyWell(RobotController rc) throws GameActionException {
        MapLocation[] wells = SharedArrayWork.readWellLocations(rc);
        if(wells.length>0){

            wellLoc= wells[rng.nextInt(wells.length)];
            findMode = true;
            backforthMode = true;
            update(rc, hqLoc, wellLoc);
        }
        else{
            rc.setIndicatorString("No wells found");
        }
    }

    static void pickClosestWell(RobotController rc) throws GameActionException {
        MapLocation[] wells = SharedArrayWork.readWellLocations(rc);
        if(wells.length>0){
            MapLocation closest = wells[0];
            for(MapLocation well: wells){
                if (well.distanceSquaredTo(hqLoc) < closest.distanceSquaredTo(hqLoc)){
                    closest = well;
                }
            }
            wellLoc= closest;
            findMode = true;
            backforthMode = true;
            update(rc, hqLoc, wellLoc);
        }
        else{
            rc.setIndicatorString("No wells found");
        }
    }
    static void pickWellOfType(RobotController rc, ResourceType type) throws GameActionException {
        MapLocation[] wells = SharedArrayWork.readWellLocationsOfType(rc, type);
        if(wells.length>0){
            MapLocation closest = wells[0];
            for(MapLocation well: wells){
                if (well.distanceSquaredTo(hqLoc) < closest.distanceSquaredTo(hqLoc)){
                    closest = well;
                }
            }
            wellLoc= closest;
            findMode = true;
            backforthMode = true;
            update(rc, hqLoc, wellLoc);
        }
        else{
            rc.setIndicatorString("No wells found");
        }
    }
    static void scanHQ(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots) {
            if(robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.HEADQUARTERS) {
                hqLoc = robot.getLocation();
                break;
            }
        }
    }

    static void scanWells(RobotController rc) throws GameActionException {
        WellInfo[] wells = rc.senseNearbyWells();
        if(wells.length > 0) wellLoc = wells[0].getMapLocation();
    }

    static void scanIslands(RobotController rc) throws GameActionException {
        int[] ids = rc.senseNearbyIslands();
        for(int id : ids) {
            if(rc.senseTeamOccupyingIsland(id) == Team.NEUTRAL) {
                MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                if (locs.length > 0) {
                    islandLoc = locs[0];
                    break;
                }
            }
        }
    }
    static void depositResource(RobotController rc, ResourceType type) throws GameActionException {
        int amount = rc.getResourceAmount(type);
        if(amount > 0) {
            if(rc.canTransferResource(hqLoc, type, amount)) rc.transferResource(hqLoc, type, amount);
        }
    }

    static int getTotalResources(RobotController rc) {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM)
                + rc.getResourceAmount(ResourceType.MANA)
                + rc.getResourceAmount(ResourceType.ELIXIR);
    }

    static void record(RobotController rc) throws GameActionException {
        WellInfo[] wells = rc.senseNearbyWells();
    }
}