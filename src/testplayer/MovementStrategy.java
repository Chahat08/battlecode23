package testplayer;

import battlecode.common.*;

import java.util.*;

import static testplayer.RobotPlayer.directions;
import static testplayer.RobotPlayer.rng;

public class MovementStrategy {
    static MapLocation start;
    static MapLocation end;

    static ArrayList<ArrayList<MapLocation>> pathMaps = new ArrayList<>();
    static boolean isPathSet = false;
    static boolean findMode = true;
    static boolean backforth = true;
    static boolean moveforward = false;
    static ArrayList current1 = new ArrayList<MapLocation>();
    static MapLocation moved;
    static int pathIndex = 0;
    static ArrayList<MapLocation> openList = new ArrayList<>();
    static ArrayList<MapLocation> closedList = new ArrayList<>();
    static int waitturn = 0;

    static void backforth(RobotController rc) throws GameActionException {
        int a = moveTowards1(rc, closedList.get(pathIndex));
        rc.setIndicatorString( "pathIndex: " + pathIndex);
        if (a== 1)
        {
            if(moveforward == true){
                pathIndex++;
                if(pathIndex == closedList.size()){
                    moveforward = false;
                    pathIndex--;
                }
            }
            else{
                pathIndex--;
                if(pathIndex == -1){
                    moveforward = true;
                    pathIndex++;
                }
            }
        }
    }
    static void find(RobotController rc) throws GameActionException{

        // System.out.println("Open List:"+openList);
        // cleans up open list to ensure memory issue dont occur
        if(openList.size() >= 10){
            // System.out.println("Open list too big, cleaning up");
            openList.subList(9, openList.size()).clear();
        }
        // System.out.println("Is rc action ready?" + rc.isActionReady());
        // if open list is empty, no point in move forward
        if(!openList.isEmpty() && rc.isActionReady()){
            movebest(rc);
        }
    }
    static void movebest(RobotController rc) throws GameActionException {
        // get current best move
        MapLocation current = openList.get(0);
        if(!current.isAdjacentTo(rc.getLocation())){
            // rc.setIndicatorString("Current best move is not adjacent to me, moving to it" + current);
            // if current best move is not adjacent to current location, move to it
            Direction mapInfo = rc.senseMapInfo(rc.getLocation()).getCurrentDirection();
            Direction dir = rc.getLocation().directionTo(current);
            if(mapInfo.opposite() == dir){
            }
            else{
                moveTowards1(rc, current);
            }
            return;
        }
        Direction dir = rc.getLocation().directionTo(current);

        // System.out.println("A* Debugg: "+ openList.size()+ "Current loc:" + rc.getLocation()+ "Want to move to:" + current);

        if(rc.canMove(dir)) {
            // System.out.println("Moving to: " + current);
            rc.move(dir);
            openList.remove(current);
            closedList.add(current);
            moved = current;
        }
        else{
            // System.out.println("Cannot move to: " + current);
            MapLocation a = moveRandom1(rc);
            openList.remove(current);
            closedList.add(a);
            moved = a;
            // System.out.println("Waiting for turn");
        }
        current = moved;
        if(current.equals(end) || current.isAdjacentTo(end)){
            // found the path
            // System.out.println("Found the path");
            openList.clear();
            isPathSet = true;
            findMode = false;
            // System.out.println("Closed list: " + closedList);
            // System.out.println("Current Loc: " + rc.getLocation());
            if(rc.getLocation() == closedList.get(closedList.size()-1)){
                pathIndex = closedList.size()-2;
            }
            else if(rc.getLocation() == closedList.get(0)){
                pathIndex = 1;
            }
            else{
                pathIndex = closedList.indexOf(rc.getLocation());
            }
            // System.out.println("Path Index: " + pathIndex);
            return;
        }
        MapInfo[] mapInfos = rc.senseNearbyMapInfos(current, 2);
        for(MapInfo mapInfo: mapInfos){
            if(mapInfo.isPassable() && mapInfo.getMapLocation() != current && rc.sensePassability(mapInfo.getMapLocation())){
                MapLocation newLoc = mapInfo.getMapLocation();
                // System.out.println("MapInfo: " + mapInfo.getMapLocation());
                openList.add(newLoc);
            }
        }

        // System.out.println("Open List:"+openList);
        // calculate f value n reorder the array
        fcalc(rc);
    }
    static void fcalc(RobotController rc) throws GameActionException {
        List<Integer> f = new ArrayList<>();
        Map<MapLocation, Integer> fMap = new HashMap<>();
        for (MapLocation loc : openList) {
            int p = 0;
            // System.out.println("Calculating f for: " + loc);
            if(rc.canSenseLocation(loc)==false ){
                fMap.put(loc, -1000);
            }
            else {
                MapInfo mapInfo = rc.senseMapInfo(loc);
                if (mapInfo.isPassable() && rc.canMove(rc.getLocation().directionTo(loc))) {
                    p -= loc.distanceSquaredTo(end);
                    // if (rc.getLocation().distanceSquaredTo(end) > loc.distanceSquaredTo(end)) p += 10;
                    if (mapInfo.hasCloud()) p -= 2;
                    if(mapInfo.getCurrentDirection()!=Direction.CENTER) p-=100;
                    // if (rc.senseRobotAtLocation(mapInfo.getMapLocation()).getTeam() != rc.getTeam()) p -= 5;
                } else {
                    p -= 1000;
                }
                fMap.put(loc, p);
            }
        }
        Collections.sort(openList, new Comparator<MapLocation>(){
            @Override
            public int compare(MapLocation o1, MapLocation o2) {
                return Integer.compare(fMap.get(o2), fMap.get(o1));
            }
        });
        // System.out.println("After sort:"+ openList);
    }

    static MapLocation moveRandom1(RobotController rc) throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if(rc.canMove(dir)){
            rc.move(dir);
        }
        return rc.getLocation();
    }

    static int moveTowards1(RobotController rc, MapLocation target) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(target);
        if (rc.canMove(dir) && rc.senseMapInfo(rc.adjacentLocation(dir)).getCurrentDirection() != dir.opposite()) {
            rc.move(dir);
            return 1;
        }
        else {
            moveRandom1(rc);
            return 0;
        }
    }

    static void update(MapLocation start1, MapLocation end1) throws GameActionException{
        // adds paths to pathMaps
        ArrayList<MapLocation> newList= new ArrayList<>();
        for(MapLocation p : closedList) {
            newList.add(new MapLocation(p.x, p.y));
        }
        if(!pathMaps.contains(newList) && !newList.isEmpty()){
            pathMaps.add(newList);
        }
        //
        if(!pathMaps.isEmpty()) {
            // checks if a good old path exists!!
            for (ArrayList<MapLocation> list : pathMaps) {
                if (list.get(0).isWithinDistanceSquared(start1, 2) && list.get(list.size() - 1).isWithinDistanceSquared(end1, 2) || list.get(list.size() - 1).isWithinDistanceSquared(start1, 2) && list.get(0).isWithinDistanceSquared(end1, 2)) {
                    start = list.get(0);
                    end = list.get(list.size() - 1);
                    isPathSet = true;
                    findMode = false;
                    pathIndex = 0;
                    openList.clear();
                    closedList.clear();
                    closedList = list;
                    moveforward = true;
                    // System.out.println("Path found and setup:" + closedList);
                    return;
                }
            }
        }
        else {
            // System.out.println("No path found");
            // if no path exists, then find a new one
            start = start1;
            end = end1;
            isPathSet = false;
            findMode = true;
            openList.clear();
            closedList.clear();
            openList.add(start);
            pathIndex = 0;
            moveforward = true;
        }
    }
}
