package common.communication;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import common.bitwisemanipulation.BitwiseOperations;
import common.bitwisemanipulation.BitwiseOperationsImpl;

import common.utils.Utils.BitIndex;

import java.util.HashMap;
import java.util.Map;

import static common.utils.Utils.locationToInt;

// 0:

// 1: our HQ location bits:[0 to 11]
// 2: our HQ location bits:[0 to 11]
// 3: our HQ location bits:[0 to 11]
// 4: our HQ location bits:[0 to 11]

// 5: enemy HQ location bits:[0 to 11], 12, 13 and 14-> num launchers
// 6: enemy HQ location bits:[0 to 11], 12, 13 and 14-> num launchers
// 7: enemy HQ location bits:[0 to 11], 12, 13 and 14-> num launchers
// 8: enemy HQ location bits:[0 to 11], 12, 13 and 14-> num launchers
public class Write {
    private static final BitwiseOperations bitwiseOperations = new BitwiseOperationsImpl();

    // this works
    private static void writeLocationAtBitIndex(MapLocation loc, RobotController rc, BitIndex bitIndex) throws GameActionException{
        int location = locationToInt(rc, loc); // 12 bits
        if(!rc.canWriteSharedArray(bitIndex.getArrayIndex(), location))
            return;


        String bitStr = bitwiseOperations.getIntegerAs12BitString(location);
        String currValStr = bitwiseOperations.getIntegerAs16BitString(rc.readSharedArray(bitIndex.getArrayIndex()));

        int val = bitwiseOperations.setBitStringInBitStringAtPositionK(
                currValStr,
                bitStr,
                bitIndex.getBitPosition()
                );

        rc.writeSharedArray(bitIndex.getArrayIndex(), val);

    }
    public static void addOurHQLocation(RobotController rc) throws GameActionException {
        MapLocation me = rc.getLocation();
        if(Read.readOurHQLocations(rc).containsValue(me)) return;
        if(!rc.canWriteSharedArray(1, locationToInt(rc, me))) return;

        if(Read.readLocationAtBitIndex(rc, new BitIndex(1, 0))==null){
            writeLocationAtBitIndex(me, rc, new BitIndex(1, 0));
            System.out.println("FIRST HQ SET at "+me);
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(2, 0))==null){
            writeLocationAtBitIndex(me, rc, new BitIndex(2, 0));
            System.out.println("SECOND HQ SET at "+me);
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(3, 0))==null){
            writeLocationAtBitIndex(me, rc, new BitIndex(3, 0));
            System.out.println("SECOND HQ SET at "+me);
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(4, 0))==null){
            writeLocationAtBitIndex(me, rc, new BitIndex(4, 0));
            System.out.println("SECOND HQ SET at "+me);
        }

    }

    public static void addEnemyHQLocation(RobotController rc, MapLocation loc) throws GameActionException {

        if(Read.readEnemyHQLocations(rc).containsValue(loc)) return;
        if(!rc.canWriteSharedArray(5,locationToInt(rc, loc))) return;

        if(Read.readLocationAtBitIndex(rc, new BitIndex(5, 0))==null){
            writeLocationAtBitIndex(loc, rc, new BitIndex(5, 0));
            System.out.println("FIRST ENEMY HQ SET at " + loc);
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(6, 0))==null){
            writeLocationAtBitIndex(loc, rc, new BitIndex(6, 0));
            System.out.println("SECOND ENEMY HQ SET at " + loc);
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(7, 0))==null){
            writeLocationAtBitIndex(loc, rc, new BitIndex(7, 0));
            System.out.println("THIRD ENEMY HQ SET at " + loc);
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(8, 0))==null){
            writeLocationAtBitIndex(loc, rc, new BitIndex(8, 0));
            System.out.println("FOURTH ENEMY HQ SET at " + loc);
        }
    }

    // TODO: SEE WHY NOT WORKING
    public static void addToEnemyHQLauncherBotCount(RobotController rc, MapLocation loc) throws GameActionException {
        int currentValue = Read.readEnemyHQLauncherBotCount(rc, loc);
        BitIndex bitIndex = Read.getEnemyHQLauncherCountBitIndex(rc, loc);
        int valAtIndex = rc.readSharedArray(bitIndex.getArrayIndex());

        int intToPut = bitwiseOperations.setBitStringInBitStringAtPositionK(
                bitwiseOperations.getIntegerAs16BitString(valAtIndex),
                bitwiseOperations.getIntegerAs3BitString(currentValue+1),
                bitIndex.getBitPosition()
        );
        if(rc.canWriteSharedArray(bitIndex.getArrayIndex(), 1)){
            rc.writeSharedArray(bitIndex.getArrayIndex(), intToPut);
        }
    }
}
