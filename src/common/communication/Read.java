package common.communication;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.*;

import common.bitwisemanipulation.BitwiseOperations;
import common.bitwisemanipulation.BitwiseOperationsImpl;
import common.utils.Utils.BitIndex;

import static common.utils.Utils.intToLocation;

public class Read {

// 0:
// 1: our HQ location bits:[0 to 11, 12 to 23]
// 2: our HQ location bits:[0 to 11, 12 to 23]
// 3: enemy HQ location bits:[0 to 11, 12 to 23] 24, 25, 26 -> num launchers on loc 1; 27,28,29-> num launchers on loc 2
// 4: enemy HQ location bits:[0 to 11, 12 to 23] 24, 25, 26-> num launchers on loc 1, 27,28,29-> num launchers on loc 2]

    private static final BitwiseOperations bitwiseOperations = new BitwiseOperationsImpl();

    public static MapLocation readLocationAtBitIndex(RobotController rc, BitIndex bitIndex) throws GameActionException{
        // location is stored as 12 bit int

        int valAtIndex = rc.readSharedArray(bitIndex.getArrayIndex());
        System.out.println("val: "+valAtIndex);
        int loc = bitwiseOperations.getBitStringAsInteger(
                bitwiseOperations.getIntegerAs32BitString(valAtIndex)
                .substring(bitIndex.getBitPosition(), bitIndex.getBitPosition()+12)
        );

        System.out.println("loc as int: "+loc);

        return intToLocation(rc, loc);
    }

    public static HashMap<Integer, MapLocation> readOurHQLocations(RobotController rc) throws GameActionException{
        String atIdx1 = bitwiseOperations.getIntegerAs32BitString(rc.readSharedArray(1));
        String atIdx2 = bitwiseOperations.getIntegerAs32BitString(rc.readSharedArray(2));


        String[] headquaters = new String[4];
        headquaters[0] = atIdx1.substring(0,12);
        headquaters[1] = atIdx1.substring(12,24);
        headquaters[2] = atIdx2.substring(0,12);
        headquaters[3] = atIdx2.substring(12,24);

        String ref = "000000000000";

        HashMap<Integer, MapLocation> locationHashMap = new HashMap<Integer, MapLocation>();

        if(!headquaters[0].equals(ref))
            locationHashMap.put(1, intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[0])));


        if(!headquaters[1].equals(ref))
            locationHashMap.put(2, intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[1])));


        if(!headquaters[2].equals(ref))
            locationHashMap.put(3, intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[2])));


        if(!headquaters[3].equals(ref))
            locationHashMap.put(4, intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[3])));

        return locationHashMap;
    }

    public static HashMap<Integer, MapLocation> readEnemyHQLocations(RobotController rc) throws GameActionException{
        String atIdx3 = bitwiseOperations.getIntegerAs32BitString(rc.readSharedArray(2));
        String atIdx4 = bitwiseOperations.getIntegerAs32BitString(rc.readSharedArray(3));


        String[] headquaters = new String[4];
        headquaters[0] = atIdx3.substring(0,12);
        headquaters[1] = atIdx3.substring(12,24);
        headquaters[2] = atIdx4.substring(0,12);
        headquaters[3] = atIdx4.substring(12,24);

        String ref = "000000000000";

        HashMap<Integer, MapLocation> locationHashMap = new HashMap<Integer, MapLocation>();

        if(!headquaters[0].equals(ref))
            locationHashMap.put(1, intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[0])));


        if(!headquaters[1].equals(ref))
            locationHashMap.put(2, intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[1])));


        if(!headquaters[2].equals(ref))
            locationHashMap.put(3, intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[2])));


        if(!headquaters[3].equals(ref))
            locationHashMap.put(4, intToLocation(rc, bitwiseOperations.getBitStringAsInteger(headquaters[3])));

        return locationHashMap;
    }

    public static BitIndex getEnemyHQLauncherCountBitIndex(RobotController rc, MapLocation loc) throws GameActionException{
        HashMap<Integer, MapLocation> mapLocationHashMap = readEnemyHQLocations(rc);

        BitIndex bitIndex = new BitIndex();

        for (Map.Entry<Integer, MapLocation> entry : mapLocationHashMap.entrySet()) {
            if (entry.getValue().equals(loc)) {
                bitIndex.setArrayIndex(entry.getKey() == 1 || entry.getKey() == 2 ? 3 : 4);
                bitIndex.setBitPosition(entry.getKey() == 1 || entry.getKey() == 3 ? 24 : 27);
                break;
            }
        }
        return bitIndex;
    }

    public static int readEnemyHQLauncherBotCount(RobotController rc, MapLocation loc) throws GameActionException {

        HashMap<Integer, MapLocation> mapLocationHashMap = readEnemyHQLocations(rc);
        String currentCountString = "0";

        BitIndex bitIndex = getEnemyHQLauncherCountBitIndex(rc, loc);
        currentCountString = bitwiseOperations.readIthToJthBitOfIntegerAsBitString(
                rc.readSharedArray(bitIndex.getArrayIndex()),
                bitIndex.getBitPosition(),
                bitIndex.getBitPosition()+3
        );

        return bitwiseOperations.getBitStringAsInteger(currentCountString);
    }

}
