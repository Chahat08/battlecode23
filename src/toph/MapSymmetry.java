package toph;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * There's always symmetry in the maps, it can be either
 * Rotational, or, Horizontal, or, Vertical
 * If we detect it, we don't have to store all that much info the array,
 * and our moves can be smarter and more deterministic instead of randomly hobbling about in initial game
 */
public class MapSymmetry {
    // what type of symmetry does the map have?
    public static enum SymmetryType{
        ROTATIONAL,
        HORIZONTAL,
        VERTICAL
    }

    public static MapLocation getSymmetricalMapLocation(RobotController rc, MapLocation location, SymmetryType symmetryType){
        if(symmetryType==SymmetryType.ROTATIONAL)
            return getRotationalSymmetricalMapLocation(rc, location);

        else if(symmetryType==SymmetryType.HORIZONTAL)
            return getHorizontalSymmetricalMapLocation(rc, location);

        else
            return getVerticalSymmetricalMapLocation(rc, location);
    }


    private static MapLocation getRotationalSymmetricalMapLocation(RobotController rc, MapLocation location) {
        int MAP_WIDTH=rc.getMapWidth(); int MAP_HEIGHT=rc.getMapHeight();
        MapLocation loc = new MapLocation(MAP_WIDTH- location.x-1, MAP_HEIGHT-location.y-1);
//        if(loc.x<0 || loc.y<0 || loc.x>=MAP_WIDTH || loc.y<=MAP_HEIGHT) return null;
        return loc;
    }

    private static MapLocation getHorizontalSymmetricalMapLocation(RobotController rc, MapLocation location) {
        int MAP_HEIGHT=rc.getMapHeight(); int MAP_WIDTH=rc.getMapWidth();
        MapLocation loc = new MapLocation(location.x, MAP_HEIGHT- location.y-1);
//        if(loc.x<0 || loc.y<0 || loc.x>=MAP_WIDTH || loc.y<=MAP_HEIGHT) return null;
        return loc;
    }

    private static MapLocation getVerticalSymmetricalMapLocation(RobotController rc, MapLocation location) {
        int MAP_WIDTH=rc.getMapWidth();  int MAP_HEIGHT=rc.getMapHeight();
        MapLocation loc = new MapLocation(MAP_WIDTH- location.x-1, location.y);
//        if(loc.x<0 || loc.y<0 || loc.x>=MAP_WIDTH || loc.y<=MAP_HEIGHT) return null;
        return loc;
    }

}
