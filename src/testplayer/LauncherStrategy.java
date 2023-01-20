package testplayer;

import battlecode.common.*;
import common.communication.Read;
import common.communication.Write;

import java.util.*;

import static common.utils.Utils.intToLocation;
import static testplayer.MovementStrategy.*;
import static testplayer.MovementStrategy.backforth;
import static testplayer.RobotPlayer.directions;
import static testplayer.RobotPlayer.turnCount;


public class LauncherStrategy {
    static final Random rng = new Random(6147);
    static boolean isAtEnemyHQ = false;
    static boolean didIncreaseCount = false;

    static final int MAX_LAUNCHER_BOT_COUNT_PER_HQ = 5;

    static void runLauncher(RobotController rc) throws GameActionException {
        if(turnCount == 10) {
            System.out.println(turnCount+ "_Update requested");
            update(intToLocation(rc, rc.readSharedArray(43)), intToLocation(rc, rc.readSharedArray(44)));
        }
        if(start != null && end != null){
            if(findMode == true){
                find(rc);
            }
            else{
                backforth(rc);
            }
        }
        else{
            System.out.println("No instructions");
        }

        // STEP 1: check for enemy HQs

//        int radius = rc.getType().actionRadiusSquared;
//        Team opponent = rc.getTeam().opponent();
//
//        // TODO: think of better strategy to engage with the enemies, diff strats to engage w diff enemies
//        // 1:2 attacking vs exploratory launchers in initial game
//        MapLocation hqLocation = null;
//        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
//        if (enemies.length >= 0) { // enemies found
//            for (RobotInfo enemy : enemies) {
//                if (enemy.getType() == RobotType.HEADQUARTERS) { // headquater type found
//                    hqLocation = enemy.getLocation(); // write locally to move into it
//                    Write.addEnemyHQLocation(rc, enemy.getLocation()); // add to shared info
//                }
//                else if(isAtEnemyHQ){
//                    // attack any other enemy bots detected
//                    if(rc.canAttack(enemy.getLocation())){
//                        rc.attack(enemy.getLocation());
//                    }
//                }
//            }
//        }
//
//        HashMap<Integer, MapLocation> enemyHQs = Read.readEnemyHQLocations(rc);
//        // let's read some enemy hq infos
//
//        if(!enemyHQs.isEmpty()){
//            rc.setIndicatorString("enemy hq found!");
//            for(MapLocation enemyHQ :enemyHQs.values()){
//                if(Read.readEnemyHQLauncherBotCount(rc, enemyHQ)<=MAX_LAUNCHER_BOT_COUNT_PER_HQ) {
//                    hqLocation = enemyHQ;
//                    break;
//                }
//            }
//        }
//
//
//        if(hqLocation!=null){
//            // if enemy hq found, go there
//              if(rc.canMove(rc.getLocation().directionTo(hqLocation))){
//                  rc.move(rc.getLocation().directionTo(hqLocation));
//              }
//              if(rc.canActLocation(hqLocation)){
//                  if(rc.canWriteSharedArray(5, 1)) {
//                      Write.addToEnemyHQLauncherBotCount(rc, hqLocation);
//                      didIncreaseCount=true;
//                  }
//                  isAtEnemyHQ = true;
//              }
//            if(isAtEnemyHQ && !didIncreaseCount){
//                if(rc.canWriteSharedArray(5, 1)){
//                    Write.addToEnemyHQLauncherBotCount(rc, hqLocation);
//                }
//            }
//        } else { // else move randomly
//            Direction dir = directions[rng.nextInt(directions.length)];
//            if (rc.canMove(dir)) {
//                rc.move(dir);
//            }
//        }

    }
}
