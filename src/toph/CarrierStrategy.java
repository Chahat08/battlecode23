package toph;


import battlecode.common.*;


import java.util.*;

import static common.utils.Utils.intToLocation;
import static toph.BotPrivateInfo.*;
import static toph.CarrierStrategies.*;
import static toph.Constants.*;
import static toph.MovementStrategy.*;
import static toph.RobotPlayer.*;
import static toph.SharedArrayWork.*;
public class CarrierStrategy {

    static boolean adaormana = false;

    static int Mode = 2; // 0: well work, 1: island work, 2: wall colliders
    static boolean islandAlert = false;
    static int starveLoc = 0;
    static Direction wallColiderDir = directions[rng.nextInt(8)];
    static ResourceType wellType = rng.nextBoolean() ? ResourceType.ADAMANTIUM : ResourceType.MANA;
    static int wellMode = 0;
    // carry resources if near hq, well
    // attack-> throws resources at enemies
    // used to put anchors on sky islands
    // get slower with amount carried
    static void runCarrier(RobotController rc) throws GameActionException {
        Mode = rng.nextInt(2);
        if(Mode == 0  && turnCount == 1){ assignWell = true;}
        if(Mode == 0){
            rc.setIndicatorString("Mode: Well Work" + " " + wellType + " " + wellLoc);
            wellassignment(rc, wellType, wellMode);
        }
        else if(Mode == 1){
            if(rc.canPlaceAnchor()) {
                rc.placeAnchor();
            }
            if(rc.canTakeAnchor(myHqLoc, Anchor.STANDARD) && rc.getNumAnchors(Anchor.STANDARD) == 0) {
                rc.takeAnchor(myHqLoc, Anchor.STANDARD);
            }
            if(!islandAlert) {
                rc.setIndicatorString(("WallColliderDir: " + wallColiderDir));
                wallcollider(rc);
                if(rc.getRoundNum() - lastupdate == 5) islandAlert = true;
                return;
            }
            if(islandAlert){
                rc.setIndicatorString("Rushing Home!");
                moveTowards(rc, myHqLoc);
                int radius = RobotType.CARRIER.actionRadiusSquared;
                if(rc.getLocation().isWithinDistanceSquared(myHqLoc, radius)){
                    depositResource(rc, ResourceType.ADAMANTIUM);
                    depositResource(rc, ResourceType.MANA);
                    islandAlert = false;
                    wallColiderDir = directions[rng.nextInt(8)];
                }
            }

        }
        else {
            System.out.println("Crazy Confused Mode");
            moveRandom(rc);
        }
    }
}