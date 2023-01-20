package testplayer;

import battlecode.common.*;
import common.communication.Read;
import common.communication.Write;

import java.util.*;

import static common.utils.Utils.intToLocation;
import static common.utils.Utils.locationToInt;
import static testplayer.RobotPlayer.*;

class MapData{
    static MapLocation loc;
    static int fvalue;

    public MapData(MapLocation mapLocation, Integer integer) {
        loc = mapLocation;
        fvalue = integer;
    }
    int getFvalue(){
        return fvalue;
    }
}
public class CarrierStrategy {
    static int hqLocationNumber = 1;

    static MapLocation start;
    static MapLocation end;
//    static int[] path;

    static ArrayList<MapLocation> openList = new ArrayList<>();
    static ArrayList<MapLocation> closedList = new ArrayList<>();
    // carry resources if near hq, well
    // attack-> throws resources at enemies
    // used to put anchors on sky islands
    // get slower with amount carried
    static void runCarrier(RobotController rc) throws GameActionException {
        System.out.println("Starting carrier:" + rc.getLocation());
        if(turnCount == 1) {
            MapLocation start = rc.adjacentLocation(Direction.NORTH);
            end = new MapLocation(1, 1);
            openList.add(start);
        }
        System.out.println("Open List:"+openList);
        // cleans up open list to ensure memory issue dont occur
        if(openList.size() >= 10){
            System.out.println("Open list too big, cleaning up");
            openList.subList(9, openList.size()).clear();
        }
        System.out.println("Is rc action ready?" + rc.isActionReady());
        // if open list is empty, no point in move forward
        if(!openList.isEmpty() && rc.isActionReady()){
            // get current best move
            MapLocation current = openList.get(0);
            Direction dir = rc.getLocation().directionTo(current);

            System.out.println("A* Debugg: "+ openList.size()+ "Current loc:" + rc.getLocation()+ "Want to move to:" + current);

            if(rc.canMove(dir)){
                System.out.println("Moving to: " + current);
                rc.move(dir);
                openList.remove(current);
                closedList.add(current);
                if(current.equals(end)){
                    // found the path
                    System.out.println("Found the path");
                }
                for(Direction d: directions){
                    MapLocation adj = rc.adjacentLocation(d);
                    System.out.println("Adding to open list"+ adj);
                    if(rc.onTheMap(adj)) {
                        openList.add(adj);
                    }
                }

                System.out.println("Gonna Dive in F Calculations");
                System.out.println("Open List:"+openList);
                // calculate f value n reorder the array
                fcalc(rc);
            }
            else{
                System.out.println("Cannot move to: " + current);
                System.out.println("Waiting for turn");
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
    // implement a star algorithm to move from start to end target

    static void fcalc(RobotController rc) throws GameActionException {
        List<Integer> f = new ArrayList<>();
        for (MapLocation loc : openList) {
            int p = 0;
            Boolean test = rc.canSenseLocation(loc);
            MapInfo mapInfo = rc.senseMapInfo(loc);
            if ( test && mapInfo.isPassable() && rc.canMove(rc.getLocation().directionTo(loc))) {
                  p -= loc.distanceSquaredTo(end);
//                if (mapInfo.isPassable()) p += 5;
//                if (rc.getLocation().distanceSquaredTo(end) > loc.distanceSquaredTo(end)) p += 10;
//                if (mapInfo.hasCloud()) p += 2;
//                if (rc.senseRobotAtLocation(mapInfo.getMapLocation()).getTeam() != rc.getTeam()) p -= 5;
            } else {
                p -= 100;
            }
            f.add(p);
        }
        System.out.println(openList);
        System.out.println(f);
//        openList = openList.sorted(Comparator.comparingInt(f::get));
//        openList.sort(Comparator.comparingInt(f::indexOf));
//        Collections.sort(openList, Comparator.comparingInt(f::indexOf));
//        openList.sort(Comparator.comparingInt(f::get));
        MapData[] data = new MapData[openList.size()];
        for(int i = 0; i < openList.size(); i++){
            data[i] = new MapData(openList.get(i), f.get(i));
            System.out.println(data[i].loc + " " + data[i].fvalue);
//            System.out.println(openList.get(i) + " " + f.get(i));
        }
        Collections.sort(Arrays.asList(data), new Comparator<MapData>() {
            @Override
            public int compare(MapData o1, MapData o2) {
                System.out.println(o1.getFvalue() + " "+ o2.getFvalue());
                return Integer.compare(o1.getFvalue(), o2.getFvalue());
            }
        });
//        Collections.sort(data, (MapData a1, MapData a2) -> a1.fvalue -a2.fvalue);
//        Collections.sort(data, Comparator.comparing(MapData::getFvalue));
        System.out.println("After sort:"+ openList);
//        Collections.reverse(openList);

        System.out.println("We reached here dw!!");
    }
}
