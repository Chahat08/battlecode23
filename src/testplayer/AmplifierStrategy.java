package testplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import static testplayer.RobotPlayer.directions;
import static testplayer.RobotPlayer.rng;

public class AmplifierStrategy {

    static void runAmplifier(RobotController rc) throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
