package samwise;

import battlecode.common.*;

import java.util.Random;

import static samwise.RobotPlayer.directions;

public class LauncherStrategy {
    static final Random rng = new Random(6147);

    static void runLauncher(RobotController rc) throws GameActionException {
        int radius = rc.getType().actionRadiusSquared;
        rc.setIndicatorString(""+radius);
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);

        // attack if we can
        if (enemies.length >= 0) {
            MapLocation toAttack = enemies[0].location;

            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");
                rc.attack(toAttack);
            }
        }

        // move randomly
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

    }
}
