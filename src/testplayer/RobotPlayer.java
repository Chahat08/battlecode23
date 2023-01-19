package testplayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import java.util.Random;

import static testplayer.AmplifierStrategy.runAmplifier;
import static testplayer.CarrierStrategy.runCarrier;
import static testplayer.HeadquaterStrategy.runHeadquaters;
import static testplayer.LauncherStrategy.runLauncher;

public class RobotPlayer {
    static int turnCount = 0;

    static final Random rng = new Random(6147);

    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };


    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        System.out.println("TYPE: "+rc.getType()+", HEALTH: "+rc.getHealth());

        // game loop
        while(true){
            ++turnCount;
            try {

                switch (rc.getType()) {
                    case HEADQUARTERS:
                        runHeadquaters(rc); break;
                    case CARRIER:
                        runCarrier(rc); break;
                    case LAUNCHER:
                        runLauncher(rc); break;// do something
                    case BOOSTER:
                        //runLauncher(rc); break; // do something
                    case DESTABILIZER:
                        //runLauncher(rc); break; // do something
                    case AMPLIFIER:
                        runAmplifier(rc); break;
                }
            } catch(GameActionException e){
                e.printStackTrace();
            } catch(Exception e) {

            } finally {
                Clock.yield(); // make code wait until next turn, then cont
            }

        }

    }


}
