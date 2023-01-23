package toph;

import battlecode.common.*;

import java.util.HashMap;
import java.util.Random;
import java.util.Map;

public class Constants {
    // num of turns robot has been alive for
    static MapLocation myHqLoc;
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

    static final HashMap<Integer, MapSymmetry.SymmetryType> symmetries = new HashMap<Integer, MapSymmetry.SymmetryType>() {{
        put(0, MapSymmetry.SymmetryType.ROTATIONAL);
        put(1, MapSymmetry.SymmetryType.HORIZONTAL);
        put(2, MapSymmetry.SymmetryType.VERTICAL);
        //put(3, SymmetryType.ROTATIONAL);
    }};

    static Direction wallColiderDir = directions[rng.nextInt(8)];
    static void scanHQ(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots) {
            if(robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.HEADQUARTERS) {
                myHqLoc = robot.getLocation();
                break;
            }
        }
    }
}
