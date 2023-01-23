package stitch;

import battlecode.common.*;
import stitch.MapSymmetry.SymmetryType;

import java.util.HashMap;
import java.util.Random;

import static stitch.AmplifierStrategy.runAmplifier;
import static stitch.BotPrivateInfo.*;
import static stitch.CarrierStrategy.runCarrier;
import static stitch.HeadquaterStrategy.runHeadquaters;
import static stitch.LauncherStrategy.runLauncher;

public class RobotPlayer {
    static int turnCount = 0; // num of turns robot has been alive for
    static final Random rng = new Random(1012); // random number generator
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

    static final HashMap<Integer, SymmetryType> symmetries = new HashMap<Integer, SymmetryType>() {{
        put(0, SymmetryType.ROTATIONAL);
        put(1, SymmetryType.HORIZONTAL);
        put(2, SymmetryType.VERTICAL);
        //put(3, SymmetryType.ROTATIONAL);
    }};

    static boolean islandAlert = false;
    static MapLocation hqLoc;
    static boolean debugOn = false;
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
//        System.out.println("TYPE: "+rc.getType()+", HEALTH: "+rc.getHealth());

        // game loop
        while(true){
            ++turnCount;


            recordingsystem(rc);
            report(rc);
            if(debugOn == true ) writeReport(rc);

            try {
                switch (rc.getType()) {
                    case HEADQUARTERS:
                        runHeadquaters(rc); break;
                    case CARRIER:
                        runCarrier(rc); break;
                    case LAUNCHER:
                        runLauncher(rc); break;
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
