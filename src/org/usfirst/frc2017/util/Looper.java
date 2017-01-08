package org.usfirst.frc2017.util;

import java.util.ArrayList;
import java.util.List;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This code runs all of the robot's loops. Loop objects are stored in a List
 * object. They are started when the robot powers up and stopped after the
 * match.
 */
public class Looper {
    public double period = 0.1;

    private boolean running;

    private final Notifier notifier;
    private final List<Loop> loops;
    private final Object taskRunningLock = new Object();
    private double timestamp = 0;
    private double dt = 0;
    
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (taskRunningLock) {
                if (running) {
                    double start = Timer.getFPGATimestamp();
                    dt = start - timestamp;
                    timestamp = start;
                    for (Loop loop : loops) {
                        loop.onLoop();
                    }
                    double duration = Timer.getFPGATimestamp() - start;
                    timestamp = start;
                    
                    if (duration >= period) {
//                        FaultCode.write(FaultCode.Codes.SLOW_LOOPER, 
  //                              "expected <" + period + " had " + dt);
                    }
                }
            }
        }
    };

    public Looper(double period) {
        this.period = period;
        notifier = new Notifier(runnable);
        running = false;
        loops = new ArrayList<>();
    }

    public synchronized void register(Loop loop) {
        synchronized (taskRunningLock) {
            loops.add(loop);
        }
    }

    public synchronized void start() {
        if (!running) {
       //     Logger.info("Starting loops");
            synchronized (taskRunningLock) {
                timestamp = Timer.getFPGATimestamp();
                for (Loop loop : loops) {
                    loop.onStart();
                }
                
                // reset the timestamp, to minimize faults,
                // especially when our onStarts are slow
                timestamp = Timer.getFPGATimestamp();
                running = true;
            }
            notifier.startPeriodic(period);
        }
    }

    public synchronized void stop() {
        if (running) {
            Logger.info("Stopping loops");
            notifier.stop();
            synchronized (taskRunningLock) {
                running = false;
                for (Loop loop : loops) {
                    Logger.debug("Stopping " + loop);
                    loop.onStop();
                }
            }
        }
    }

    public void outputToSmartDashboard() {
        SmartDashboard.putNumber("looper_dt", dt);
    }
}
