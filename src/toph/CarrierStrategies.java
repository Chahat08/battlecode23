package toph;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.ResourceType;
import battlecode.common.RobotController;

import static toph.Constants.myHqLoc;
import static toph.Constants.rng;
import static toph.MovementStrategy.*;

public class CarrierStrategies {
    static int carrierMode = 0; // 0: well work, 1: island work, 2: wall colliders

    static ResourceType welltype;
    static int wellMode;

    static MapLocation wellLoc;

    static boolean changeWell = false; // changewell and wellMode both need to called when
    static boolean assignWell = false; //
    static MapLocation islandLoc;

    static void wellassignment(RobotController rc, ResourceType type, int wellMode) throws GameActionException{
        if(assignWell || changeWell){
            welltype = type;
            int res;
            if(wellMode == 0){
                res = pickAnyWell(rc);
                if(res == -1){ moveRandom(rc); return;}
                else{
                    assignWell = false;
                    changeWell = false;
                }
            }
            else if(wellMode == 1){
                res = pickWellOfType(rc, type);
                if(res == -1){ moveRandom(rc); return;}
                else{
                    assignWell = false;
                    changeWell = false;
                }
            }
            else if(wellMode == 2){
                res = pickClosestWell(rc);
                if(res == -1){ moveRandom(rc); return;}
                else{
                    assignWell = false;
                    changeWell = false;
                    // move forward
                }
            }
            findMode = true;
            backforthMode = true;
            update(rc, myHqLoc, wellLoc);
        }
        move(rc);
    }

    static int pickAnyWell(RobotController rc) throws GameActionException {
        MapLocation[] wells = SharedArrayWork.readWellLocations(rc);
        if(wells.length>0){
            wellLoc= wells[rng.nextInt(wells.length)];
            return 0;
        }
        else{
            return -1;
        }
    }
    static int pickClosestWell(RobotController rc) throws GameActionException {
        MapLocation[] wells = SharedArrayWork.readWellLocations(rc);
        if(wells.length>0){
            MapLocation closest = wells[0];
            for(MapLocation well: wells){
                if (well.distanceSquaredTo(myHqLoc) < closest.distanceSquaredTo(myHqLoc)){
                    closest = well;
                }
            }
            wellLoc= closest;
            return 0;
        }
        else{
            return -1;
        }
    }
    static int pickWellOfType(RobotController rc, ResourceType type) throws GameActionException {
        MapLocation[] wells = SharedArrayWork.readWellLocationsOfType(rc, type);
        if(wells.length>0){
            MapLocation closest = wells[0];
            for(MapLocation well: wells){
                if (well.distanceSquaredTo(myHqLoc) < closest.distanceSquaredTo(myHqLoc)){
                    closest = well;
                }
            }
            wellLoc= closest;
            return 0;
        }
        else{
            return -1;
        }
    }

    static void move(RobotController rc) throws GameActionException{
        if(start != null && end != null){
            if(findMode){
                find(rc);
            }
            else if (backforthMode){
                backforth(rc);
            }
        }
        else{
            moveRandomNoRet(rc);
        }
    }
    static void depositResource(RobotController rc, ResourceType type) throws GameActionException {
        int amount = rc.getResourceAmount(type);
        if(amount > 0) {
            if(rc.canTransferResource(myHqLoc, type, amount)) rc.transferResource(myHqLoc, type, amount);
        }
    }

    static void carrierbehavior(RobotController rc) throws GameActionException{
                //Collect from well if close and inventory not full
        if(wellLoc != null && rc.canCollectResource(wellLoc, -1)) rc.collectResource(wellLoc, -1);

        //Transfer resource to headquarters
        depositResource(rc, ResourceType.ADAMANTIUM);
        depositResource(rc, ResourceType.MANA);
    }
}
