package common.communication;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import common.bitwisemanipulation.BitwiseOperations;
import common.bitwisemanipulation.BitwiseOperationsImpl;

import common.utils.Utils;

import static common.utils.Utils.locationToInt;

class BitIndex{
    int arrayIndex;
    int bitPosition;

    public BitIndex(int a, int b){
        this.arrayIndex = a;
        this.bitPosition = b;
    }
}
// 1: BIT 0 write lock, BIT 1 index for write lock
// 2: our HQ locations [bits: 0 to 11, 12 to 23, 24 to 31...]
// 3: [...0 to 3, 4 to 15]

// 0:
// 1: our HQ location bits:[0 to 11, 12 to 23]
// 2: our HQ location bits:[0 to 11, 12 to 23]
// 3: enemy HQ location bits:[0 to 11, 12 to 23]
// 4: enemy HQ location bits:[0 to 11, 12 to 23]

public class Write {
    private static final BitwiseOperations bitwiseOperations = new BitwiseOperationsImpl();
    private static int hqLocNumber = 0;
    private static int enemyHQLocNumber = 0;

    // TODO: check uniqueness before writing
    private static void writeLocationAtBitIndex(MapLocation loc, RobotController rc, BitIndex bitIndex) throws GameActionException{
        int location = locationToInt(rc, loc); // 12 bits
        String bitStr = bitwiseOperations.getIntegerAs12BitString(location);
        int val = bitwiseOperations.setBitStringInIntegerAtPositionK(
                rc.readSharedArray(bitIndex.arrayIndex),
                bitStr,
                bitIndex.bitPosition
                );
        rc.writeSharedArray(val, bitIndex.arrayIndex);
    }
    public static void addOurHQLocation(RobotController rc) throws GameActionException {
        MapLocation me = rc.getLocation();
        if(Read.readOurHQLocations(rc).contains(locationToInt(rc, me))) return;
        switch (hqLocNumber){
            case 1:
                hqLocNumber++;
                writeLocationAtBitIndex(me, rc, new BitIndex(1, 0));
                break;
            case 2:
                hqLocNumber++;
                writeLocationAtBitIndex(me, rc, new BitIndex(1, 12));
                break;
            case 3:
                hqLocNumber++;
                writeLocationAtBitIndex(me, rc, new BitIndex(2, 0));
                break;
            case 4:
                hqLocNumber++;
                writeLocationAtBitIndex(me, rc, new BitIndex(2, 12));
                break;
        }
    }

    public static void addEnemyHQLocation(RobotController rc, MapLocation loc) throws GameActionException {
        if(Read.readOurHQLocations(rc).contains(locationToInt(rc, loc))) return;

        switch (enemyHQLocNumber){
            case 1:
                enemyHQLocNumber++;
                writeLocationAtBitIndex(loc, rc, new BitIndex(3, 0));
                break;
            case 2:
                enemyHQLocNumber++;
                writeLocationAtBitIndex(loc, rc, new BitIndex(3, 12));
                break;
            case 3:
                enemyHQLocNumber++;
                writeLocationAtBitIndex(loc, rc, new BitIndex(4, 0));
                break;
            case 4:
                enemyHQLocNumber++;
                writeLocationAtBitIndex(loc, rc, new BitIndex(4, 12));
                break;
        }
    }
}
