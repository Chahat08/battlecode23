package toph.SharedArray;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.ResourceType;
import battlecode.common.RobotController;
import toph.MapSymmetry;

public interface SharedArrayAccess {
    MapSymmetry.SymmetryType readMapSymmetry(RobotController rc) throws GameActionException;
    void writeMapSymmetry(RobotController rc, MapSymmetry.SymmetryType symmetryType) throws GameActionException;
    MapLocation[] readOurHQLocations(RobotController rc) throws GameActionException;
    void writeOurHQLocation(RobotController rc, MapLocation location) throws GameActionException;
    MapLocation[] readWellLocations(RobotController rc) throws GameActionException;
    MapLocation[] readWellLocationsOfType(RobotController rc, ResourceType resourceType) throws GameActionException;
    void writeWellLocation(RobotController rc, MapLocation location, ResourceType resourceType) throws GameActionException;
    MapLocation[] readIslandLocations(RobotController rc) throws GameActionException;
    void writeIslandLocation(RobotController rc, MapLocation location) throws GameActionException;
}
