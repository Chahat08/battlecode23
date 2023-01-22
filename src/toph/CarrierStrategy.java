package toph;


import battlecode.common.*;


import java.util.Map;

import static common.utils.Utils.intToLocation;
import static toph.MovementStrategy.*;
import static toph.RobotPlayer.*;
import static toph.SharedArrayWork.*;
public class CarrierStrategy {
    static MapLocation hqLoc;
    static MapLocation wellLoc;
    static MapLocation islandLoc;

    static boolean anchorMode = false;
    // carry resources if near hq, well
    // attack-> throws resources at enemies
    // used to put anchors on sky islands
    // get slower with amount carried
    static void runCarrier(RobotController rc) throws GameActionException {

        rc.setIndicatorString("Focus well at " + wellLoc);
        if(turnCount == 1){
            firstTurn(rc);
        }

        if(wellLoc == null){
            firstTurn1(rc);
            rc.setIndicatorString("Focus well at " + wellLoc);
        }

        if(wellLoc != null && turnCount%200==0 && turnCount >2 && wellLoc.isWithinDistanceSquared( hqLoc,50)){
            firstTurn1(rc);
            rc.setIndicatorString("Refocus well at " + wellLoc);
        }
        backforthMode = true;
        if(start != null && end != null){
            if(findMode){
                find(rc);
            }
            else if (backforthMode){
                backforth(rc);
            }
            else{
                // attack
            }
        }
        else{
            moveRandomNoRet(rc);
        }

        //Collect from well if close and inventory not full
        if(wellLoc != null && rc.canCollectResource(wellLoc, -1)) rc.collectResource(wellLoc, -1);
        //Transfer resource to headquarters
        depositResource(rc, ResourceType.ADAMANTIUM);
        depositResource(rc, ResourceType.MANA);

        if(rc.canTakeAnchor(hqLoc, Anchor.STANDARD)) {
            rc.takeAnchor(hqLoc, Anchor.STANDARD);
            anchorMode = true;
        }


        //no resources -> look for well
        if(anchorMode) {
            if(islandLoc == null) {
                MapLocation[] islands = readIslandLocations(rc);
                if(islands != null) {
                    islandLoc = islands[rng.nextInt(islands.length)];
                    backforthMode = false;
                    update(rc, hqLoc, islandLoc);
                }

            }
            else moveTowards(rc, islandLoc);

            if(rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(rc.getLocation())) == Team.NEUTRAL) {
                rc.placeAnchor();
                anchorMode = false;
            }
        }
        else {
            int total = getTotalResources(rc);
            if(total == 0) {
                //move towards well or search for well
                if(wellLoc == null) moveRandom(rc);
                else if(!rc.getLocation().isAdjacentTo(wellLoc)) moveTowards(rc, wellLoc);
            }
            if(total == GameConstants.CARRIER_CAPACITY) {
                //move towards HQ
                moveTowards(rc, hqLoc);
            }
        }

//        // NEW STUFF
//        // add hq info to shared array!
//        int radius = rc.getType().actionRadiusSquared;
//        Team opponent = rc.getTeam().opponent();
//
//        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
//        if (enemies.length >= 0) { // enemies found
//            for (RobotInfo enemy : enemies) {
//                if (enemy.getType() == RobotType.HEADQUARTERS) { // headquater type found
//                    Write.addEnemyHQLocation(rc, enemy.getLocation()); // add to shared info
//                }
//            }
//        }
//
//
//        Team myTeam = rc.getTeam();
//        RobotInfo[] robots = rc.senseNearbyRobots(1, myTeam);
//
//        Set<MapLocation> hqLocations = new HashSet<>();
//
//        // pick anchor if hq with anchor is near
//        for(RobotInfo robot: robots){
//            if(robot.getType().equals(RobotType.HEADQUARTERS)){
//                hqLocations.add(robot.getLocation());
//            }
//        }
//        for(MapLocation loc:hqLocations){
//            if(rc.canTakeAnchor(loc, Anchor.STANDARD)) {
//                rc.takeAnchor(loc, Anchor.STANDARD);
//                rc.setIndicatorString("picked anchor!");
//                break;
//            }
//        }
//
//        if (rc.getAnchor() != null) {
//            // If I have an anchor singularly focus on getting it to the first island I see
//            // TODO: read/write island info into shared array
//            int[] islands = rc.senseNearbyIslands();
//            Set<MapLocation> islandLocs = new HashSet<>();
//            for (int id : islands) {
//                MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
//                islandLocs.addAll(Arrays.asList(thisIslandLocs));
//            }
//
//            // TODO: go towards nearest island instead
//            if (islandLocs.size() > 0) {
//                MapLocation islandLocation = islandLocs.iterator().next();
//                rc.setIndicatorString("Moving my anchor towards " + islandLocation);
//                while (!rc.getLocation().equals(islandLocation)) {
//                    Direction dir = rc.getLocation().directionTo(islandLocation);
//                    if (rc.canMove(dir)) {
//                        rc.move(dir);
//                    }
//                }
//                if (rc.canPlaceAnchor()) {
//                    rc.setIndicatorString("Huzzah, placed anchor!");
//                    rc.placeAnchor();
//                }
//            }
//        }
//        // Try to gather from squares around us.
//        MapLocation me = rc.getLocation();
//        for (int dx = -1; dx <= 1; dx++) {
//            for (int dy = -1; dy <= 1; dy++) {
//                MapLocation wellLocation = new MapLocation(me.x + dx, me.y + dy);
//                if (rc.canCollectResource(wellLocation, -1)) {
//                    if (rng.nextBoolean()) {
//                        rc.collectResource(wellLocation, -1);
//                        rc.setIndicatorString("Collecting, now have, AD:" +
//                                rc.getResourceAmount(ResourceType.ADAMANTIUM) +
//                                " MN: " + rc.getResourceAmount(ResourceType.MANA) +
//                                " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
//                    }
//                }
//            }
//        }
//        // Occasionally try out the carriers attack
//        if (rng.nextInt(20) == 1) {
//            RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
//            if (enemyRobots.length > 0) {
//                if (rc.canAttack(enemyRobots[0].location)) {
//                    rc.attack(enemyRobots[0].location);
//                }
//            }
//        }
//
//        // If carrying some resources, go back to HQ to supply it
//        if(
//                rc.getResourceAmount(ResourceType.ADAMANTIUM) > 0
//                || rc.getResourceAmount(ResourceType.MANA) > 0
//        )
//        {
//            HashMap<Integer, MapLocation> ourHqLocations = Read.readOurHQLocations(rc);
//            MapLocation targetHQ = ourHqLocations.get(hqLocationNumber++);
//            if(hqLocationNumber>ourHqLocations.size()) hqLocationNumber=1;
//            Direction dir = me.directionTo(targetHQ);
//            if(rc.canMove(dir)){
//                rc.move(dir);
//            }
//
//            // transfer resources
//            if(rc.getLocation().isAdjacentTo(targetHQ)){
//                if(rc.canTransferResource(targetHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM))){
//                    rc.transferResource(targetHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM));
//                }
//                if(rc.canTransferResource(targetHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA))){
//                    rc.transferResource(targetHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA));
//                }
//            }

//        }


        // If we can see a well, move towards it
//        WellInfo[] wells = rc.senseNearbyWells();
//        if (wells.length > 1 && rng.nextInt(3) == 1) {
//            WellInfo well_one = wells[1];
//            Direction dir = me.directionTo(well_one.getMapLocation());
//            if (rc.canMove(dir))
//                rc.move(dir);
//        }
//        // Also try to move randomly.
//        Direction dir = directions[rng.nextInt(directions.length)];
//        if (rc.canMove(dir)) {
//            rc.move(dir);
//        }
    }

    static void firstTurn(RobotController rc) throws GameActionException {
        scanHQ(rc);
        MapLocation[] wells = SharedArrayWork.readWellLocations(rc);
        if(wells.length>0){
            wellLoc= wells[rng.nextInt(wells.length)];
            rc.setIndicatorString("Focus well at " + wellLoc);
            findMode = true;
            backforthMode = true;
            update(rc, hqLoc, wellLoc);
        }
        else{
            rc.setIndicatorString("No wells found");
        }
    }

    static void firstTurn1(RobotController rc) throws GameActionException {
//        scanHQ(rc);
        MapLocation[] wells = SharedArrayWork.readWellLocations(rc);
        if(wells.length>0){
            wellLoc= wells[rng.nextInt(wells.length)];
            rc.setIndicatorString("Focus well at " + wellLoc);
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
}