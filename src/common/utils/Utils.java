package common.utils;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;



public class Utils {

    public static class BitIndex{
        int arrayIndex=0;
        int bitPosition=0;

        public int getArrayIndex() {
            return arrayIndex;
        }
        public int getBitPosition(){
            return bitPosition;
        }

        public void setArrayIndex(int arrayIndex) {
            this.arrayIndex = arrayIndex;
        }

        public void setBitPosition(int bitPosition) {
            this.bitPosition = bitPosition;
        }

        public BitIndex(int arrayIndex, int bitPosition){
            this.arrayIndex = arrayIndex;
            this.bitPosition = bitPosition;
        }
        public BitIndex(){
        }
    }

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

}
