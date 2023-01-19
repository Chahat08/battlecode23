package common.communication;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import common.bitwisemanipulation.BitwiseOperations;
import common.bitwisemanipulation.BitwiseOperationsImpl;
import common.utils.Utils.BitIndex;

import static common.utils.Utils.intToLocation;

public class Read {

// 0:
// 1: our HQ location bits:[0 to 11, 12 to 23]
// 2: our HQ location bits:[0 to 11, 12 to 23]
// 3: enemy HQ location bits:[0 to 11, 12 to 23]
// 4: enemy HQ location bits:[0 to 11, 12 to 23]

    private static final BitwiseOperations bitwiseOperations = new BitwiseOperationsImpl();

    public static Set<MapLocation> readOurHQLocations(RobotController rc) throws GameActionException{
        String atIdx1 = bitwiseOperations.getIntegerAs32BitString(rc.readSharedArray(1));
        String atIdx2 = bitwiseOperations.getIntegerAs32BitString(rc.readSharedArray(2));


        String[] headquaters = new String[4];
        headquaters[0] = atIdx1.substring(0,12);
        headquaters[1] = atIdx1.substring(12,24);
        headquaters[2] = atIdx2.substring(0,12);
        headquaters[3] = atIdx2.substring(12,24);

        String ref = "000000000000";

        Set<MapLocation> locs = new HashSet<>();
        if(!headquaters[0].equals(ref))
            locs.add(
                    intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[0]))
            );

        if(!headquaters[1].equals(ref))
            locs.add(
                    intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[1]))
            );

        if(!headquaters[2].equals(ref))
            locs.add(
                    intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[2]))
            );

        if(!headquaters[3].equals(ref))
            locs.add(
                    intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[3]))
            );

        return locs;
    }

    public static Set<MapLocation> readEnemyHQLocations(RobotController rc) throws GameActionException{
        String atIdx3 = bitwiseOperations.getIntegerAs32BitString(rc.readSharedArray(2));
        String atIdx4 = bitwiseOperations.getIntegerAs32BitString(rc.readSharedArray(3));


        String[] headquaters = new String[4];
        headquaters[0] = atIdx3.substring(0,12);
        headquaters[1] = atIdx3.substring(12,24);
        headquaters[2] = atIdx4.substring(0,12);
        headquaters[3] = atIdx4.substring(12,24);

        String ref = "000000000000";

        Set<MapLocation> locs = new HashSet<>();
        if(!headquaters[0].equals(ref))
            locs.add(
                    intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[0]))
            );

        if(!headquaters[1].equals(ref))
            locs.add(
                    intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[1]))
            );

        if(!headquaters[2].equals(ref))
            locs.add(
                    intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[2]))
            );

        if(!headquaters[3].equals(ref))
            locs.add(
                    intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[3]))
            );

        return locs;
    }

}
