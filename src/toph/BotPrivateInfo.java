package toph;

import battlecode.common.*;

import java.util.HashSet;
import java.util.Set;

import static toph.CarrierStrategy.*;
import static toph.LauncherStrategy.*;
import static toph.MovementStrategy.backforthMode;
import static toph.MovementStrategy.update;
import static toph.RobotPlayer.*;
import static toph.SharedArrayWork.*;

public class BotPrivateInfo {
    static Set<Integer> islandIdxs = new HashSet<>();
    static Set<MapLocation> neutralIslandLocs = new HashSet<>();
    static Set<MapLocation> ourIslandLocs = new HashSet<>();
    static Set<MapLocation> enemyIslandLocs = new HashSet<>();


    static Set<MapLocation> AdawellLocs = new HashSet<>();
    static Set<MapLocation> ManawellLocs = new HashSet<>();

    static Set<MapLocation> CurrentLocs = new HashSet<>();

    static Set<MapLocation> CloudLocs = new HashSet<>();

    static Direction wallColiderDir = directions[rng.nextInt(8)];

    static void record(RobotController rc) throws GameActionException {
        // design a alert system
        try {
            MapInfo[] newLocs = rc.senseNearbyMapInfos();
            for(MapInfo loc: newLocs){
                int id = rc.senseIsland(loc.getMapLocation());
                if(id != -1){
                    Team team = rc.senseTeamOccupyingIsland(id);
                    MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                    Team ourTeam = rc.getTeam();
                    Team enemyTeam = ourTeam.opponent();
                    if(!islandIdxs.contains(id)){
                        System.out.println("Island Noticed: " + id + " " + locs[0]);
//                        if(rc.getID() == 11608){
//                            System.out.println("Island Noticed: " + id + " " + locs[0]);
//                        }
                        islandAlert = true;
                        islandIdxs.add(id);
                    }
                    if(team == Team.NEUTRAL){
                        for(MapLocation l: locs){
                            neutralIslandLocs.add(l);
                        }
                    }
                    else if(team == ourTeam){
                        for(MapLocation l: locs){
                            ourIslandLocs.add(l);
                        }
                    }
                    else if(team == enemyTeam){
                        for(MapLocation l: locs){
                            enemyIslandLocs.add(l);
                        }
                    }
                }

                WellInfo well = rc.senseWell(loc.getMapLocation());

                if(well != null){
                    if(well.getResourceType() == ResourceType.ADAMANTIUM){
                        AdawellLocs.add(loc.getMapLocation());
                    }
                    else if(well.getResourceType() == ResourceType.MANA){
                        ManawellLocs.add(loc.getMapLocation());
                    }
                }

                if(loc.hasCloud()) {
                    CloudLocs.add(loc.getMapLocation());
                }
                if(loc.getCurrentDirection()!= Direction.CENTER) {
                    CurrentLocs.add(loc.getMapLocation());
                }
            }
        }
        catch(Exception e) {
            //  Block of code to handle errors
            System.out.println("Exception: " + e);
        }
    }

    static void reportOffline(RobotController rc) throws GameActionException {
        for(MapLocation loc: neutralIslandLocs){
            writeIslandLocation(rc, loc);
        }
        for(MapLocation loc: ourIslandLocs){
            writeIslandLocation(rc, loc);
        }
        for(MapLocation loc: enemyIslandLocs){
            writeIslandLocation(rc, loc);
        }
        for(MapLocation loc: AdawellLocs){
            writeWellLocation(rc, loc, ResourceType.ADAMANTIUM);
        }
        for(MapLocation loc: ManawellLocs){
            writeWellLocation(rc, loc, ResourceType.MANA);
        }
        for(MapLocation loc: CurrentLocs){
            writeData(rc, loc, 1);
        }
        for(MapLocation loc: CloudLocs){
            writeData(rc, loc,2);
        }
//        System.out.println("Reached End!!");
    }

    static void recordingsystem(RobotController rc) throws GameActionException {
        try {
            boolean offline = rc.canWriteSharedArray(60, 1);
            if(offline == false) {
                rc.setIndicatorString("Recording Offline");
                record(rc);
            }
            else{
                int radius = rc.getType().actionRadiusSquared;
                if(hqLoc != null && rc.getLocation().distanceSquaredTo(hqLoc) <= radius){            if(rc.getLocation().isWithinDistanceSquared(hqLoc, radius)) {
                    rc.setIndicatorString("Reporting offline");

                    reportOffline(rc);
//                    if(rc.getID()== 11608){
//                        System.out.println("Reached End!!");
//                    }
//                    System.out.println("Reported Data End!!");
                    islandAlert = false;
                }

                }
            }

        }
        catch(Exception e1){
                System.out.println("Error in recording system1");
            }
    }

    static void report(RobotController rc) throws GameActionException {
        WellInfo[] adawelllocs = rc.senseNearbyWells(ResourceType.ADAMANTIUM);
        for(WellInfo loc:adawelllocs){
            writeWellLocation(rc, loc.getMapLocation(), ResourceType.ADAMANTIUM); ;
        }

        WellInfo[]  manawelllocs = rc.senseNearbyWells(ResourceType.MANA);
        for(WellInfo loc:manawelllocs){
            writeWellLocation(rc, loc.getMapLocation(), ResourceType.MANA); ;
        }

        int[] islandlocs = rc.senseNearbyIslands();
        for(int loc:islandlocs){
            MapLocation[] locs = rc.senseNearbyIslandLocations(loc);
            for(MapLocation loc2:locs){
                writeIslandLocation(rc, loc2);
            }

        }
        if(rc.getType() == RobotType.HEADQUARTERS){
            writeOurHQLocation(rc, rc.getLocation());
        }
        RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for(RobotInfo robot:robots){
            if(robot.getType() == RobotType.HEADQUARTERS){
                writeEnemyHQLocation(rc, robot.getLocation());
            }
        }
    }

    static void writeReport(RobotController rc) throws GameActionException {
        if(rc.getType() == RobotType.HEADQUARTERS){
            System.out.println("HEADQUARTERS report:");
            MapLocation[] hqlocs = readOurHQLocations(rc);
            System.out.println("My HQs: ");
            for(MapLocation loc: hqlocs){
                System.out.println("HQ: "+loc);
            }
            System.out.println("Enemies: ");
            MapLocation[] enemyHQLocs = readEnemyHQLocations(rc);
            for(MapLocation loc: enemyHQLocs){
                System.out.println("ENEMY HQ: "+loc);
            }
            MapLocation[] wells = readWellLocations(rc);
            System.out.println("Wells: ");
            for(MapLocation well : wells){
                System.out.println(well);
            }
            MapLocation[] islands = readIslandLocations(rc);
            System.out.println("Islands: ");
            for(MapLocation island : islands){
                System.out.println(island);
            }
        }
    }
}
