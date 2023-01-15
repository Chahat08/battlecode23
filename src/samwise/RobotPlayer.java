package samwise;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class RobotPlayer {
    static int turnCount = 0;
    static final Random rng = new Random(6147);

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


    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        System.out.println("TYPE: "+rc.getType()+", HEALTH: "+rc.getHealth());

        // game loop
        while(true){
            ++turnCount;

            try {

                switch (rc.getType()) {
                    case HEADQUARTERS:
                        runHeadquaters(rc); break;
                    case CARRIER:
                        runCarrier(rc); break;//runCarrier(rc); break;
                    case LAUNCHER:
                        runLauncher(rc); break;// do something
                    case BOOSTER:
                        //runLauncher(rc); break; // do something
                    case DESTABILIZER:
                        //runLauncher(rc); break; // do something
                    case AMPLIFIER:
                        //runLauncher(rc); break; // do something
                }
            } catch(GameActionException e){
                e.printStackTrace();
            } catch(Exception e) {

            } finally {
                Clock.yield(); // make code wait until next turn, then cont
            }

        }

    }

    // HQ will build resources so build them if poss
    static void runHeadquaters(RobotController rc) throws GameActionException{

        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        if(rc.canBuildAnchor(Anchor.STANDARD)) {
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("BUILDING ANCHOR: "+rc.getAnchor());
        }


        if(rng.nextBoolean()) {
            rc.setIndicatorString("Trying to build a carrier");
            if(rc.canBuildRobot(RobotType.CARRIER, newLoc)){
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }
        } else {
            rc.setIndicatorString("Trying to build a launcher");
            if(rc.canBuildRobot(RobotType.LAUNCHER, newLoc)){
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }
        }

    }

    // carry resources if near hq, well
    // attack-> throws resources at enemies
    // used to put anchors on sky islands
    // get slower with amount carried
    static void runCarrier(RobotController rc) throws GameActionException{

        Team myTeam = rc.getTeam();
        RobotInfo[] robots = rc.senseNearbyRobots(1, myTeam);

        Set<MapLocation> hqLocations = new HashSet<>();

        // pick anchor if hq with anchor is near
        for(RobotInfo robot: robots){
            if(robot.getType().equals(RobotType.HEADQUARTERS)){
                hqLocations.add(robot.getLocation());
            }
        }
        for(MapLocation loc:hqLocations){
            if(rc.canTakeAnchor(loc, Anchor.STANDARD)) {
                rc.takeAnchor(loc, Anchor.STANDARD);
                rc.setIndicatorString("picked anchor!");
                break;
            }
        }

        if (rc.getAnchor() != null) {
            // If I have an anchor singularly focus on getting it to the first island I see
            int[] islands = rc.senseNearbyIslands();
            Set<MapLocation> islandLocs = new HashSet<>();
            for (int id : islands) {
                MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
                islandLocs.addAll(Arrays.asList(thisIslandLocs));
            }
            // TODO: go towards nearest island instead
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
                    if (rng.nextBoolean()) {
                        rc.collectResource(wellLocation, -1);
                        rc.setIndicatorString("Collecting, now have, AD:" +
                                rc.getResourceAmount(ResourceType.ADAMANTIUM) +
                                " MN: " + rc.getResourceAmount(ResourceType.MANA) +
                                " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                    }
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
