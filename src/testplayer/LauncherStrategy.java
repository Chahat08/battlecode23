package testplayer;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static testplayer.RobotPlayer.directions;


public class LauncherStrategy {
    static final Random rng = new Random(6147);

    static void runLauncher(RobotController rc) throws GameActionException {

        /*
        TODO:
        EXPLORATION
        beginning game: find enemy headquaters, find wells and such, write to shared array
         */

        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();

        // TODO: think of better strategy to engage with the enemies, diff strats to engage w diff enemies
        // 1:2 attacking vs exploratory launchers in initial game
        MapLocation hqLocation = null;
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length >= 0) {
            for(RobotInfo enemy:enemies){
                if(enemy.getType()==RobotType.HEADQUARTERS){
                    System.out.println("ENEMY HQ FOUND");
                    System.out.println(enemy.getType());
                    System.out.println(enemy.getLocation());
                    hqLocation = enemy.getLocation();
                    System.out.println(hqLocation);
                }
            }
            // MapLocation toAttack = enemies[0].location;
//            MapLocation toAttack = rc.getLocation().add(Direction.EAST);
//
//            if (rc.canAttack(toAttack)) {
//                rc.setIndicatorString("Attacking");
//                rc.attack(toAttack);
//            }
        }

        // all nearby mapinfos
        //TODO: UTILISE MAPINFOS HERE
        //MapInfo[] mapInfos = rc.senseNearbyMapInfos(-1);


        // TODO: write to shared about this wellinfo, to send in carriers
        WellInfo[] wellInfos = rc.senseNearbyWells(-1);
        for(WellInfo wellInfo:wellInfos){
            System.out.println("WELL LOCATED!!!");
            System.out.println(wellInfo.getMapLocation());
            System.out.println(wellInfo.getResourceType());

            // if resource amount too low/0, convert to elixir well
            System.out.println(wellInfo.getResource(wellInfo.getResourceType()));
        }

        // TODO: write to shared array about this islandinfo, to send in carriers
        int[] islands = rc.senseNearbyIslands();
        Set<MapLocation> islandLocs = new HashSet<>();
        for (int id : islands) {
            MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
            islandLocs.addAll(Arrays.asList(thisIslandLocs));
        }
        if(islandLocs.size()>0){
            System.out.println("ISLAND FOUND!!!");
            for(MapLocation islandLoc : islandLocs){
                System.out.println(islandLoc);
            }
        }

        // move randomly
        // TODO: move in nearest/furtherst found hqLocation
        if(hqLocation!=null){
            System.out.println("Moving to HQ Location");
              if(rc.canMove(rc.getLocation().directionTo(hqLocation))){
                  rc.move(rc.getLocation().directionTo(hqLocation));
              }
        } else { // else move randomly
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        }

    }
}
