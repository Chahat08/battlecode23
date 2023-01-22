package toph;

import battlecode.common.*;
import toph.MapSymmetry.SymmetryType;
import toph.SharedArray.SharedArrayAccess;
import toph.SharedArray.SharedArrayAccessImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static javax.swing.UIManager.put;
import static toph.CarrierStrategy.runCarrier;
import static toph.HeadquaterStrategy.runHeadquaters;
import static toph.LauncherStrategy.runLauncher;
import static toph.SharedArrayWork.*;

public class RobotPlayer {
    static int turnCount = 0; // num of turns robot has been alive for
    static final Random rng = new Random(1012); // random number generator
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    static final HashMap<Integer, SymmetryType> symmetries = new HashMap<Integer, SymmetryType>() {{
        put(0, SymmetryType.ROTATIONAL);
        put(1, SymmetryType.HORIZONTAL);
        put(2, SymmetryType.VERTICAL);
        //put(3, SymmetryType.ROTATIONAL);
    }};

    static final SharedArrayAccess sharedArray = new SharedArrayAccessImpl();

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
//        System.out.println("TYPE: "+rc.getType()+", HEALTH: "+rc.getHealth());

        // game loop
        while(true){
            ++turnCount;
            report(rc);
//            if(turnCount==1) report(rc);
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

            try {
                switch (rc.getType()) {
                    case HEADQUARTERS:
                        runHeadquaters(rc); break;
                    case CARRIER:
                        runCarrier(rc); break;
                    case LAUNCHER:
                        runLauncher(rc); break;
                    case BOOSTER:
                        //runLauncher(rc); break; // do something
                    case DESTABILIZER:
                        //runLauncher(rc); break; // do something
                    case AMPLIFIER:
                        //runAmplifier(rc); break;
                }
            } catch(GameActionException e){
                e.printStackTrace();
            } catch(Exception e) {

            } finally {
                Clock.yield(); // make code wait until next turn, then cont
            }
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
}
