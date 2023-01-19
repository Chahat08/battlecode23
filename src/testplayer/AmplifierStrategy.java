package testplayer;

import battlecode.common.*;
import common.communication.Read;
import common.communication.Write;

import java.util.HashMap;

import static testplayer.LauncherStrategy.MAX_LAUNCHER_BOT_COUNT_PER_HQ;
import static testplayer.RobotPlayer.directions;
import static testplayer.RobotPlayer.rng;

public class AmplifierStrategy {
    static boolean isAtEnemyHQ = false;

    static void runAmplifier(RobotController rc) throws GameActionException {

        // lets go park amplifiers by enemy hq

        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();

        // try to sense enemy headquaters and write if found
        MapLocation hqLocation = null;
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length >= 0) { // enemies found
            for (RobotInfo enemy : enemies) {
                if (enemy.getType() == RobotType.HEADQUARTERS) { // headquater type found
                    hqLocation = enemy.getLocation(); // write locally to move into it
                    Write.addEnemyHQLocation(rc, enemy.getLocation()); // add to shared info
                }
            }
        }

        HashMap<Integer, MapLocation> enemyHQs = Read.readEnemyHQLocations(rc);

        if(!enemyHQs.isEmpty()){
            rc.setIndicatorString("enemy hq found!");
            for(MapLocation enemyHQ :enemyHQs.values()){
                if(Read.readEnemyHQLauncherBotCount(rc, enemyHQ)<=MAX_LAUNCHER_BOT_COUNT_PER_HQ) {
                    hqLocation = enemyHQ;
                    break;
                }
            }
        }

        if(hqLocation!=null){
            // if enemy hq found, go there
            if(rc.canMove(rc.getLocation().directionTo(hqLocation))){
                rc.move(rc.getLocation().directionTo(hqLocation));
            }
            if(rc.canActLocation(hqLocation)){
                if(rc.canWriteSharedArray(5, 1)) {
                    Write.addToEnemyHQLauncherBotCount(rc, hqLocation);
                }
                isAtEnemyHQ = true;
            }
        } else { // else move randomly
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        }
    }
}
