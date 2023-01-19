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


// 1: BIT 0 write lock, BIT 1 index for write lock
// 2: our HQ locations [bits: 0 to 11, 12 to 23, 24 to 31...]
// 3: [...0 to 3, 4 to 15]

// 0:
// 1: our HQ location bits:[0 to 11, 12 to 23]
// 2: our HQ location bits:[0 to 11, 12 to 23]
// 3: enemy HQ location bits:[0 to 11, 12 to 23, 24 and 25-> num launchers on loc 1, 26 and 27-> num launchers on loc 2]
// 4: enemy HQ location bits:[0 to 11, 12 to 23, 24 and 25-> num launchers on loc 1, 26 and 27-> num launchers on loc 2]

public class Write {
    private static final BitwiseOperations bitwiseOperations = new BitwiseOperationsImpl();
    private static int hqLocNumber = 0;
    private static int enemyHQLocNumber = 0;

    // TODO: check uniqueness before writing
    private static void writeLocationAtBitIndex(MapLocation loc, RobotController rc, BitIndex bitIndex) throws GameActionException{
        int location = locationToInt(rc, loc); // 12 bits

        System.out.println("here");

        if(!rc.canWriteSharedArray(bitIndex.getArrayIndex(), location))
            return;

        System.out.println("here too");


        String bitStr = bitwiseOperations.getIntegerAs12BitString(location);
        System.out.println(bitStr);
        int val = bitwiseOperations.setBitStringInIntegerAtPositionK(
                rc.readSharedArray(bitIndex.getArrayIndex()),
                bitStr,
                bitIndex.getBitPosition()
                );
        System.out.println(val);
        rc.writeSharedArray(bitIndex.getArrayIndex(), val);
        System.out.println("reading now: ");
    }
    public static void addOurHQLocation(RobotController rc) throws GameActionException {
        MapLocation me = rc.getLocation();
        System.out.println("HERE I AM: "+Read.readLocationAtBitIndex(rc, new BitIndex(1, 0)));

        if(Read.readOurHQLocations(rc).containsValue(me)) return;

        if(Read.readLocationAtBitIndex(rc, new BitIndex(1, 0))==null){
            hqLocNumber++;
            System.out.println("FIRST HQ SET");
            writeLocationAtBitIndex(me, rc, new BitIndex(1, 0));
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(1, 12))==null){
            hqLocNumber++;
            System.out.println("SECOND HQ SET");
            writeLocationAtBitIndex(me, rc, new BitIndex(1, 12));
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(2, 0))==null){
            hqLocNumber++;
            System.out.println("THIRD HQ SET");
            writeLocationAtBitIndex(me, rc, new BitIndex(2, 0));
        }
        else if(Read.readLocationAtBitIndex(rc, new BitIndex(2, 12))==null){
            hqLocNumber++;
            System.out.println("FOURTH HQ SET");
            writeLocationAtBitIndex(me, rc, new BitIndex(2, 12));
        }

    }

    public static void addEnemyHQLocation(RobotController rc, MapLocation loc) throws GameActionException {
        if(Read.readOurHQLocations(rc).containsValue(loc)) return;

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
            default:
                System.out.println("This shouldnt be called either");
        }
    }

    public static void addToEnemyHQLauncherBotCount(RobotController rc, MapLocation loc) throws GameActionException {
        int currentValue = Read.readEnemyHQLauncherBotCount(rc, loc);
        BitIndex bitIndex = Read.getEnemyHQLauncherCountBitIndex(rc, loc);

        if(rc.canWriteSharedArray(bitIndex.getArrayIndex(), 1)){
            rc.writeSharedArray(bitIndex.getArrayIndex(), currentValue+1);
        }
    }
}
