package toph;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.ResourceType;
import battlecode.common.RobotController;
import toph.MapSymmetry;

import java.util.ArrayList;

public class SharedArrayWork {
    static int MAP_SYMMETRY_INDEX = 0;

    // our hq locations
    static int OUR_HQ_LOCATIONS_FIRST_INDEX = 1;
    static int OUR_HQ_LOCATIONS_LAST_INDEX = 4;

    static int ENEMY_HQ_LOCATIONS_FIRST_INDEX = 5;
    static int ENEMY_HQ_LOCATIONS_LAST_INDEX = 8;
    // adamantium wells
    static int ADA_WELL_LOCATIONS_FIRST_INDEX = 21;
    static int ADA_WELL_LOCATIONS_LAST_INDEX = 25;

    // mana wells
    static int MANA_WELL_LOCATIONS_FIRST_INDEX = 26;
    static int MANA_WELL_LOCATIONS_LAST_INDEX = 30;

    // island locations
    static int ISLAND_LOCATIONS_FIRST_INDEX = 32;
    static int ISLAND_LOCATIONS_LAST_INDEX = 40;

    static int ENEMY_ISLAND_LOCATIONS_FIRST_INDEX = 41;
    static int ENEMY_ISLAND_LOCATIONS_LAST_INDEX = 45;

    static int NEUTRAL_ISLAND_LOCATIONS_FIRST_INDEX = 46;
    static int NEUTRAL_ISLAND_LOCATIONS_LAST_INDEX = 50;

    static int CURRENT_LOCATIONS_FIRST_INDEX = 51;
    static int CURRENT_LOCATIONS_LAST_INDEX = 55;
    static int CLOUD_LOCATIONS_FIRST_INDEX = 56;
    static int CLOUD_LOCATIONS_LAST_INDEX = 60;


    public static int locationToInt(RobotController rc, MapLocation m) {
        if (m == null) {
            return 0;
        }
        return 1 + m.x + m.y * rc.getMapWidth();
        // 1 + 60 +(60*60) = 3661
    }

    public static MapLocation intToLocation(RobotController rc, int m) {
        if (m == 0) {
            return null;
        }
        m--;
        return new MapLocation(m % rc.getMapWidth(), m / rc.getMapWidth());
    }

    public MapSymmetry.SymmetryType readMapSymmetry(RobotController rc) throws GameActionException{
        int symm = rc.readSharedArray(MAP_SYMMETRY_INDEX);
        if(symm!=0){
            if(symm==1) return MapSymmetry.SymmetryType.ROTATIONAL;
            if(symm==2) return MapSymmetry.SymmetryType.HORIZONTAL;
            if(symm==3) return MapSymmetry.SymmetryType.VERTICAL;
        }
        return null;
    }
    public void writeMapSymmetry(RobotController rc, MapSymmetry.SymmetryType symmetryType) throws GameActionException{
        if(!rc.canWriteSharedArray(0, 1)) return;
        if(symmetryType== MapSymmetry.SymmetryType.ROTATIONAL) rc.writeSharedArray(MAP_SYMMETRY_INDEX, 1);
        if(symmetryType==MapSymmetry.SymmetryType.HORIZONTAL) rc.writeSharedArray(MAP_SYMMETRY_INDEX, 2);
        if(symmetryType==MapSymmetry.SymmetryType.VERTICAL) rc.writeSharedArray(MAP_SYMMETRY_INDEX, 3);
    }

    public static MapLocation[] readOurHQLocations(RobotController rc) throws GameActionException {
        ArrayList<MapLocation> locations = new ArrayList<>();
        for(int i=OUR_HQ_LOCATIONS_FIRST_INDEX; i<=OUR_HQ_LOCATIONS_LAST_INDEX; ++i){
            int val = rc.readSharedArray(i);
            if(val!=0) locations.add(intToLocation(rc, val));
        }
        return locations.toArray(new MapLocation[locations.size()]);
    }

    public static MapLocation[] readEnemyHQLocations(RobotController rc) throws GameActionException {
        ArrayList<MapLocation> locations = new ArrayList<>();
        for(int i=ENEMY_HQ_LOCATIONS_FIRST_INDEX; i<=ENEMY_HQ_LOCATIONS_LAST_INDEX; ++i){
            int val = rc.readSharedArray(i);
            if(val!=0) locations.add(intToLocation(rc, val));
        }
        return locations.toArray(new MapLocation[locations.size()]);
    }

    public static void writeOurHQLocation(RobotController rc, MapLocation location) throws GameActionException{
        if(!rc.canWriteSharedArray(OUR_HQ_LOCATIONS_FIRST_INDEX, 1)) return;
        int val = locationToInt(rc, location);
        for(int i=OUR_HQ_LOCATIONS_FIRST_INDEX; i<=OUR_HQ_LOCATIONS_LAST_INDEX; ++i){
            if(rc.readSharedArray(i) == val) return;
            if(rc.readSharedArray(i)==0) {
                rc.writeSharedArray(i, val);
                break;
            }
        }
    }

    public static void writeEnemyHQLocation(RobotController rc, MapLocation location) throws GameActionException{
        if(!rc.canWriteSharedArray(ENEMY_HQ_LOCATIONS_FIRST_INDEX, 1)) return;
        int val = locationToInt(rc, location);
        for(int i=ENEMY_HQ_LOCATIONS_FIRST_INDEX; i<=ENEMY_HQ_LOCATIONS_LAST_INDEX; ++i){
            if(rc.readSharedArray(i) == val) return;
            if(rc.readSharedArray(i)==0) {
                rc.writeSharedArray(i, val);
                break;
            }
        }
    }
    public static MapLocation[] readWellLocations(RobotController rc) throws GameActionException {
        ArrayList<MapLocation> locations = new ArrayList<>();
        for(int i=ADA_WELL_LOCATIONS_FIRST_INDEX; i<=MANA_WELL_LOCATIONS_LAST_INDEX; ++i){
            int val = rc.readSharedArray(i);
            if(val!=0) locations.add(intToLocation(rc, val));
        }
        return locations.toArray(new MapLocation[locations.size()]);
    }

    public static MapLocation[] readWellLocationsOfType(RobotController rc, ResourceType resourceType) throws GameActionException{
        ArrayList<MapLocation> locations = new ArrayList<>();
        if(resourceType==ResourceType.ADAMANTIUM){
            for(int i=ADA_WELL_LOCATIONS_FIRST_INDEX; i<=ADA_WELL_LOCATIONS_LAST_INDEX; ++i){
                int val = rc.readSharedArray(i);
                if(val!=0) locations.add(intToLocation(rc, val));
            }
        }
        else if(resourceType==ResourceType.MANA){
            for(int i=MANA_WELL_LOCATIONS_FIRST_INDEX; i<=MANA_WELL_LOCATIONS_LAST_INDEX; ++i){
                int val = rc.readSharedArray(i);
                if(val!=0) locations.add(intToLocation(rc, val));
            }
        } // TODO: ELIXIR STUFF
        return locations.toArray(new MapLocation[locations.size()]);
    }

    public static void writeWellLocation(RobotController rc, MapLocation location, ResourceType resourceType) throws GameActionException{

        int val = locationToInt(rc, location);
        if(resourceType==ResourceType.ADAMANTIUM){
            for(int i=ADA_WELL_LOCATIONS_FIRST_INDEX; i<=ADA_WELL_LOCATIONS_LAST_INDEX; ++i){
                if(rc.readSharedArray(i) == val)
                {
//                    System.out.println("Already have this well location");
                    return;
                }

                if(rc.readSharedArray(i)==0){
                    if(rc.canWriteSharedArray(i, val))
                        rc.writeSharedArray(i, val);
                    return;
                }
            }
        }
        else if(resourceType==ResourceType.MANA){
            for(int i=MANA_WELL_LOCATIONS_FIRST_INDEX; i<=MANA_WELL_LOCATIONS_LAST_INDEX; ++i){
                if(rc.readSharedArray(i) == val)
                {
//                    System.out.println("Already have this well location MANA");
                    return;
                }
                if(rc.readSharedArray(i)==0){
                    if(rc.canWriteSharedArray(i, val)) {
                        rc.writeSharedArray(i, val);
                    }
                    return;
                }
            }
        }
        // TODO: ELIXIR STUFF
    }

    public static MapLocation[] readIslandLocations(RobotController rc) throws GameActionException{
        ArrayList<MapLocation> locations = new ArrayList<>();
        for(int i=ISLAND_LOCATIONS_FIRST_INDEX; i<=ISLAND_LOCATIONS_LAST_INDEX; ++i){
            int val = rc.readSharedArray(i);
            if(val!=0) locations.add(intToLocation(rc, val));
        }
        return locations.toArray(new MapLocation[locations.size()]);
    }

    public static void writeIslandLocation(RobotController rc, MapLocation location) throws GameActionException{
        for(int i=ISLAND_LOCATIONS_FIRST_INDEX; i<=ISLAND_LOCATIONS_LAST_INDEX; ++i){
            int val = locationToInt(rc, location);
            if (rc.readSharedArray(i) == val) return;
            if(rc.readSharedArray(i)==0){
                if(rc.canWriteSharedArray(i, val))
                    rc.writeSharedArray(i, val);
                return;
            }
        }
    }

    // both current and cloud read in 1
    public static MapLocation[] readData(RobotController rc, int TYPE) throws GameActionException {
        ArrayList<MapLocation> locations = new ArrayList<>();
        int start, end;
        if(TYPE == 1){
            start = CLOUD_LOCATIONS_FIRST_INDEX;
            end = CLOUD_LOCATIONS_LAST_INDEX;
            for(int i=start; i<=end; ++i){
                int val = rc.readSharedArray(i);
                if(val!=0) locations.add(intToLocation(rc, val));
            }
            return locations.toArray(new MapLocation[locations.size()]);
        }
        else if(TYPE == 2){
            start = CURRENT_LOCATIONS_FIRST_INDEX;
            end = CURRENT_LOCATIONS_LAST_INDEX;
            for(int i=start; i<=end; ++i){
                int val = rc.readSharedArray(i);
                if(val!=0) locations.add(intToLocation(rc, val));
            }
            return locations.toArray(new MapLocation[locations.size()]);
        }
        else{
            System.out.println("Invalid TYPE");
            return null;
        }



    }

    public static void writeData(RobotController rc, MapLocation location, int TYPE) throws GameActionException{
        int start, end;
        if(TYPE == 1){
            start = CLOUD_LOCATIONS_FIRST_INDEX;
            end = CLOUD_LOCATIONS_LAST_INDEX;
            for(int i=start; i<=end; ++i){
                int val = locationToInt(rc, location);
                if (rc.readSharedArray(i) == val) return;
                if(rc.readSharedArray(i)==0){
                    if(rc.canWriteSharedArray(i, val))
                        rc.writeSharedArray(i, val);
                    return;
                }
            }
        }
        else if(TYPE == 2){
            start = CURRENT_LOCATIONS_FIRST_INDEX;
            end = CURRENT_LOCATIONS_LAST_INDEX;
            for(int i=start; i<=end; ++i){
                int val = locationToInt(rc, location);
                if (rc.readSharedArray(i) == val) return;
                if(rc.readSharedArray(i)==0){
                    if(rc.canWriteSharedArray(i, val))
                        rc.writeSharedArray(i, val);
                    return;
                }
            }
        }
    }



}
