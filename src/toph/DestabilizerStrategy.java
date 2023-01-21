package toph;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import static toph.RobotPlayer.directions;

public class DestabilizerStrategy {

    static public void runDestabilizer(RobotController rc) throws GameActionException{
        MapLocation loc = rc.getLocation().add(directions[0]);
        if(rc.canDestabilize(loc))
            rc.destabilize(loc);
    }
}
