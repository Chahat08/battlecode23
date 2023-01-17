package lazarus;

import battlecode.common.*;

import static lazarus.RobotPlayer.directions;
import static lazarus.RobotPlayer.rng;
public class LauncherStrategy {
    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runLauncher(RobotController rc) throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
             MapLocation toAttack = enemies[0].location;
//            MapLocation toAttack = rc.getLocation().add(Direction.EAST)

            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");
                rc.attack(toAttack);
            }
        }

        RobotInfo[] visiblemax = rc.senseNearbyRobots(-1, opponent);
        for (RobotInfo r : visiblemax) {
            if (r.type != RobotType.HEADQUARTERS) {
                MapLocation enemyLoc = r.getLocation();
                MapLocation myLoc = rc.getLocation();
                Direction dir = myLoc.directionTo(enemyLoc);
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    break;
                }

            }
        }
        // In case no enemy near by move randomly
        // or move make n forth specific well path or anchored areas

        // Also try to move randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
