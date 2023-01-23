package toph;

import battlecode.common.*;

import java.util.HashSet;
import java.util.Set;

import static toph.CarrierStrategies.islandLoc;
import static toph.CarrierStrategies.wellLoc;
import static toph.SharedArrayWork.*;

public class BotPrivateInfo {
    // islands locs
    static Set<Integer> islandIdxs = new HashSet<>();
    static Set<MapLocation> neutralIslandLocs = new HashSet<>();
    static Set<MapLocation> ourIslandLocs = new HashSet<>();
    static Set<MapLocation> enemyIslandLocs = new HashSet<>();

    static Set<MapLocation> enemyHQs = new HashSet<>();
    static Set<MapLocation> ourHQs = new HashSet<>();
    // mine locs
    static Set<MapLocation> AdawellLocs = new HashSet<>();
    static Set<MapLocation> ManawellLocs = new HashSet<>();
    // locations of currents and clouds
    static Set<MapLocation> CurrentLocs = new HashSet<>();
    static Set<MapLocation> CloudLocs = new HashSet<>();
    static int lastupdate = 0;
    static void record(RobotController rc) throws GameActionException {
        // design a alert system
        try {
            MapInfo[] newLocs = rc.senseNearbyMapInfos();
            for(MapInfo loc: newLocs){
                int id = rc.senseIsland(loc.getMapLocation());
                if(id != -1){
                    lastupdate = rc.getRoundNum();
                    Team team = rc.senseTeamOccupyingIsland(id);
                    MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                    Team ourTeam = rc.getTeam();
                    Team enemyTeam = ourTeam.opponent();
                    islandIdxs.add(id);
                    if(team == Team.NEUTRAL){
                        for(MapLocation l: locs){
                            neutralIslandLocs.add(l);
                            enemyIslandLocs.remove(l);
                            ourIslandLocs.remove(l);
                        }
                    }
                    else if(team == ourTeam){
                        for(MapLocation l: locs){
                            ourIslandLocs.add(l);
                            neutralIslandLocs.remove(l);
                            enemyIslandLocs.remove(l);
                        }
                    }
                    else if(team == enemyTeam){
                        for(MapLocation l: locs){
                            enemyIslandLocs.add(l);
                            ourIslandLocs.remove(l);
                            neutralIslandLocs.remove(l);
                        }
                    }
                    continue;
                }
                // add well locations
                WellInfo well = rc.senseWell(loc.getMapLocation());
                if(well != null){
                    if(well.getResourceType() == ResourceType.ADAMANTIUM){
                        AdawellLocs.add(loc.getMapLocation());
                    }
                    else if(well.getResourceType() == ResourceType.MANA){
                        ManawellLocs.add(loc.getMapLocation());
                    }

                    /// alert: uncoherent behavior
                    if(rc.canCollectResource(loc.getMapLocation(), -1)) rc.collectResource(loc.getMapLocation(), -1);


                    continue;
                }

                // add to cloud locs
                if(loc.hasCloud()) {
                    CloudLocs.add(loc.getMapLocation());
                    continue;
                }

                // add to current locs
                if(loc.getCurrentDirection()!= Direction.CENTER) {
                    CurrentLocs.add(loc.getMapLocation());
                }

                if(rc.canSenseRobotAtLocation(loc.getMapLocation())){
                    RobotInfo robot = rc.senseRobotAtLocation(loc.getMapLocation());
                    if(robot.getType() == RobotType.HEADQUARTERS){
                        if(robot.getTeam() == rc.getTeam()){
                            ourHQs.add(loc.getMapLocation());
                        }
                        else{
                            lastupdate = rc.getRoundNum();
                            enemyHQs.add(loc.getMapLocation());
                        }
                    }
                }

            }
        }
        catch(Exception e) {
            //  Block of code to handle errors
            System.out.println("Error recording private info");
        }
    }

    static void report(RobotController rc) throws GameActionException {
        try {
            for (MapLocation loc : neutralIslandLocs) {
                writeIslandLocation(rc, loc);
            }
            for (MapLocation loc : ourIslandLocs) {
                writeIslandLocation(rc, loc);
            }
            for (MapLocation loc : enemyIslandLocs) {
                writeIslandLocation(rc, loc);
            }
            for (MapLocation loc : AdawellLocs) {
                writeWellLocation(rc, loc, ResourceType.ADAMANTIUM);
            }
            for (MapLocation loc : ManawellLocs) {
                writeWellLocation(rc, loc, ResourceType.MANA);
            }
            for (MapLocation loc : CurrentLocs) {
                writeData(rc, loc, 1);
            }
            for (MapLocation loc : CloudLocs) {
                writeData(rc, loc, 2);
            }
            for (MapLocation loc : enemyHQs) {
                writeEnemyHQLocation(rc, loc);
            }
            for (MapLocation loc : ourHQs) {
                writeOurHQLocation(rc, loc);
            }
        }
        catch(Exception e) {
            //  Block of code to handle errors
            System.out.println("Error reporting offline");
        }
    }

    static void recordingsystem(RobotController rc) throws GameActionException {
        try {
            boolean offline = rc.canWriteSharedArray(60, 1);
            if(offline == false) {
                record(rc);
            }
            else{
                report(rc);
                rc.setIndicatorString("Reporting");
            }
        }
        catch(Exception e1){
            System.out.println("Error in recording system");
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


    static int getTotalResources(RobotController rc) {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM)
                + rc.getResourceAmount(ResourceType.MANA)
                + rc.getResourceAmount(ResourceType.ELIXIR);
    }
}