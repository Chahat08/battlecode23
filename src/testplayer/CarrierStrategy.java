package testplayer;

import battlecode.common.*;
import common.communication.Read;
import common.communication.Write;

import java.util.*;

import static common.utils.Utils.intToLocation;
import static common.utils.Utils.locationToInt;
import static testplayer.RobotPlayer.*;

public class CarrierStrategy {
    static int hqLocationNumber = 1;

    static MapLocation start;
    static MapLocation end;
//    static int[] path;

    static List<MapLocation> openList = new ArrayList<>();
    static List<MapLocation> closedList = new ArrayList<>();
    // carry resources if near hq, well
    // attack-> throws resources at enemies
    // used to put anchors on sky islands
    // get slower with amount carried
    static void runCarrier(RobotController rc) throws GameActionException {
        if(turnCount == 1) {
            MapLocation start = rc.getLocation();
            end = new MapLocation(1, 1);
            openList.add(start);
        }
        rc.setIndicatorString("Trying to implement A* boss!!");
//        System.out.println(openList.size());
        // h = 1
        // f = g + h;
        // g = distance from start
        // cleans up open list to ensure memory issue dont occur
        if(openList.size() > 10){
            openList.subList(10, openList.size()).clear();
        }
        // if open list is empty, no point in move forward
        if(!openList.isEmpty()){
            // get current best move
            MapLocation current = openList.get(0);
//            System.out.println("Current: " + current);
            Direction dir = rc.getLocation().directionTo(current);
            if(rc.canMove(dir)){
                rc.move(dir);
            }
            openList.remove(current);
            closedList.add(current);
            if(current.equals(end)){
                // found the path
                System.out.println("Found the path");
            }
            // add currents neighbors to open list
            MapLocation[] neighbors = rc.getAllLocationsWithinRadiusSquared(current, 1);
            for(MapLocation neighbor : neighbors){
                if(!closedList.contains(neighbor)){
                    if(!openList.contains(neighbor)){
                        openList.add(neighbor);
                    }
                }
            }
            // calculate f value n reorder the array
            fcalc(rc, current);
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
    // implement a star algorithm to move from start to end target

    static void fcalc(RobotController rc, MapLocation current) throws GameActionException {
        List<Integer> f = new ArrayList<>();
        for (MapLocation loc : openList) {
            int p = 0;
            if (rc.canSenseLocation(loc)) {
                MapInfo mapInfo = rc.senseMapInfo(loc);
                if (mapInfo.isPassable()) p += 10;
                if (mapInfo.getMapLocation().distanceSquaredTo(end) < start.distanceSquaredTo(end)) p -= 5;
                if (mapInfo.hasCloud()) p += 2;
                if (rc.senseRobotAtLocation(mapInfo.getMapLocation()).getTeam() != rc.getTeam()) p -= 5;

            } else {
                p -= 1000;
            }
            f.add(p);
        }
        openList.sort(Comparator.comparingInt(f::indexOf));

        for(int i=0;i<f.size();i++){
           System.out.println("Current: "+current+" OpenList: "+openList.get(i)+" F: "+f.get(i));
        }

    }
}
