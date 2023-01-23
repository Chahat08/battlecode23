package toph;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import static toph.AmplifierStrategy.runAmplifier;
import static toph.BotPrivateInfo.*;
import static toph.CarrierStrategy.runCarrier;
import static toph.Constants.scanHQ;
import static toph.HeadquaterStrategy.runHeadquaters;
import static toph.LauncherStrategy.runLauncher;

public class RobotPlayer {
    static int turnCount = 0;
    static boolean debugOn = false;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        System.out.println("TYPE: "+rc.getType()+", HEALTH: "+rc.getHealth());
        // game loop
        while(true){
            // something to one move one is every robot has a home
            // someone to go back to today!!
            if(turnCount==0) {
                scanHQ(rc);
            }
            ++turnCount;

            // record every thing that happens in this turn
            recordingsystem(rc);

            // report to shared array if possible
            report(rc);

            // HQ write a report if asked for
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
                e.printStackTrace();
            } finally {
                Clock.yield(); // make code wait until next turn, then cont
            }
        }
    }


}
