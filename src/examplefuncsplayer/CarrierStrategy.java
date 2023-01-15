package examplefuncsplayer;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static examplefuncsplayer.RobotPlayer.*;

public class CarrierStrategy {

    static MapLocation headquarterLoc;
    static MapLocation wellLoc ;
    static MapLocation enemyHQ;

    /**
     * Run a single turn for a Carrier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runCarrier(RobotController rc) throws GameActionException {
        // Try to attack someone
        if(headquarterLoc == null) scanHQ(rc);
        if(wellLoc == null) scanWell(rc);
        if(enemyHQ == null) scanEnemyHQ(rc);
        rc.setIndicatorString("Turn Count:"+Integer.toString(turnCount)+rc.getTeam());

        if (rc.getAnchor() != null) {
            // If I have an anchor singularly focus on getting it to the first island I see
            int[] islands = rc.senseNearbyIslands();
            Set<MapLocation> islandLocs = new HashSet<>();
            for (int id : islands) {
                MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
                islandLocs.addAll(Arrays.asList(thisIslandLocs));
            }
            if (islandLocs.size() > 0) {
                MapLocation islandLocation = islandLocs.iterator().next();
                rc.setIndicatorString("Moving my anchor towards " + islandLocation);
                while (!rc.getLocation().equals(islandLocation)) {
                    Direction dir = rc.getLocation().directionTo(islandLocation);
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                    }
                }
                if (rc.canPlaceAnchor()) {
                    rc.setIndicatorString("Huzzah, placed anchor!");
                    rc.placeAnchor();
                }
            }
        }
        // Try to gather from squares around us.
        MapLocation me = rc.getLocation();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation wellLocation = new MapLocation(me.x + dx, me.y + dy);
                if (rc.canCollectResource(wellLocation, -1)) {
//                    if (rng.nextBoolean()) {
                    rc.collectResource(wellLocation, -1);
//                    rc.setIndicatorString("Collecting, now have, AD:" +
//                            rc.getResourceAmount(ResourceType.ADAMANTIUM) +
//                            " MN: " + rc.getResourceAmount(ResourceType.MANA) +
//                            " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
//                    }
                }
            }
        }
        // Occasionally try out the carriers attack
        if (rng.nextInt(20) == 1) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            if (enemyRobots.length > 0) {
                if (rc.canAttack(enemyRobots[0].location)) {
                    rc.attack(enemyRobots[0].location);
                }
            }
        }

        // If we can see a well, move towards it
        WellInfo[] wells = rc.senseNearbyWells();
        if (wells.length > 1 && rng.nextInt(3) == 1) {
            WellInfo well_one = wells[1];
            Direction dir = me.directionTo(well_one.getMapLocation());
            if (rc.canMove(dir))
                rc.move(dir);
        }
        // Also try to move randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    static void scanHQ(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam());
        for (RobotInfo r : robots) {
            if (r.type == RobotType.HEADQUARTERS) {
                headquarterLoc = r.location;
                break;
            }
        }

    }

    static void scanEnemyHQ(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (RobotInfo r : robots) {
            if (r.type == RobotType.HEADQUARTERS) {
                enemyHQ = r.location;
                break;
            }
        }

    }

    static void scanWell(RobotController rc) throws GameActionException {
        WellInfo[] wells = rc.senseNearbyWells();
        if(wells.length > 0) {
            wellLoc = wells[0].getMapLocation();
        }
    }
}
