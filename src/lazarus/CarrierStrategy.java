package lazarus;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static lazarus.RobotPlayer.rng;
import static lazarus.RobotPlayer.turnCount;

public class CarrierStrategy {

    static MapLocation headquarterLoc;
    static MapLocation wellLoc ;
    static MapLocation enemyHQ;
    boolean anchorMode = false;
    /**
     * Run a single turn for a Carrier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runCarrier(RobotController rc) throws GameActionException {
        // Try to attack someone
        if(headquarterLoc == null) scanHQ(rc);
        if(wellLoc == null) scanWell(rc);
        if(enemyHQ == null) scanEnemyHQ(rc);

        rc.setIndicatorString("Turn Count:"+Integer.toString(turnCount));
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

        // Collect from well if close and inventory not full
        if( wellLoc != null && rc.canCollectResource(wellLoc, -1)){
            rc.collectResource(wellLoc, -1);
        }

        depositResource(rc, ResourceType.ADAMANTIUM);
        depositResource(rc, ResourceType.MANA);

        // Occasionally try out the carriers attack
        if (rng.nextInt(20) == 1) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            if (enemyRobots.length > 0) {
                if (rc.canAttack(enemyRobots[0].location)) {
                    rc.attack(enemyRobots[0].location);
                }
            }
        }
//        RobotPlayer.moveRandom(rc);
        int total = getTotatlResources(rc);
        if(total == 0){
            // go to well if no resources
            if(wellLoc != null){
                MapLocation myLoc = rc.getLocation();
                Direction dir = myLoc.directionTo(wellLoc);
                if(!myLoc.isAdjacentTo(wellLoc)){
                    RobotPlayer.moveTowards(rc, wellLoc);
                }
            }
            else{
                // move in random direction
                RobotPlayer.moveRandom(rc);
            }
        }
        if(total == GameConstants.CARRIER_CAPACITY){
            // move to HQ if resources
            RobotPlayer.moveTowards(rc, headquarterLoc);
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

    static void depositResource(RobotController rc, ResourceType type) throws GameActionException {
        int amount = rc.getResourceAmount(type);
        if(amount > 0){
            if(rc.canTransferResource(headquarterLoc, type, amount)){
                rc.transferResource(headquarterLoc, type, amount);
            }
        }
    }

    static int  getTotatlResources(RobotController rc) throws GameActionException {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR);
    }

}
