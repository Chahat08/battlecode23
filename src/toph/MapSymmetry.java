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

    // all classes can look up the type from here
    public static SymmetryType MAP_SYMMETRY_TYPE = null;

    private static int MAP_WIDTH=30;
    private static int MAP_HEIGHT=30;

    public static void setMapDimensions(int height, int width) {
        MAP_HEIGHT = height;
        MAP_WIDTH = width;
    }

    // to detect symmetry, except some cases where things lie on axes,
    // we can pretty much confirm what type of symmetry we have soon as we find an enemy HQ
    public static void detectMapSymmetry(RobotController rc){

    }

    public static MapLocation getSymmetricalMapLocation(RobotController rc, MapLocation location, SymmetryType symmetryType){
        if(symmetryType==SymmetryType.ROTATIONAL)
            return getRotationalSymmetricalMapLocation(rc, location);

        else if(symmetryType==SymmetryType.HORIZONTAL)
            return getRotationalSymmetricalMapLocation(rc, location);

        else
            return getVerticalSymmetricalMapLocation(rc, location);
    }


    private static MapLocation getRotationalSymmetricalMapLocation(RobotController rc, MapLocation location) {
        MapLocation loc = new MapLocation(MAP_WIDTH- location.x-1, MAP_HEIGHT-location.y-1);
        return loc;
    }

    private static MapLocation getHorizontalSymmetricalMapLocation(RobotController rc, MapLocation location) {
        MapLocation loc = new MapLocation(location.x, MAP_HEIGHT- location.y-1);
        return loc;
    }

    private static MapLocation getVerticalSymmetricalMapLocation(RobotController rc, MapLocation location) {
        MapLocation loc = new MapLocation(MAP_WIDTH- location.x-1, location.y);
        return loc;
    }

}
