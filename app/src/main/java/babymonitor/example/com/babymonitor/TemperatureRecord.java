package babymonitor.example.com.babymonitor;

import java.util.Date;
import java.util.Observable;

/**
 * Responsible for maintaining temperature related values.
 */
public class TemperatureRecord extends Observable {

    // TODO: maybe make these values configurable by the user?
    public static long TOO_BIG_TEMPERATURE_DELTA = 20;
    public static long TOO_HIGH_TEMPERATURE = 400;
    public static long TOO_LOW_TEMPERATURE = 360;

    // most recent temperature recorded
    private long mostRecentTemperature;
    // time at which most recent temperature was recorded (null if no temperature ever recorded)
    private Date timeRecorded;
    /**
     * Maintains a running change in temperature
     * e.g. initially mostRecentTemperature = 380, runningDelta = 0
     * -> next instant, mostRecentTemperature = 383, runningDelta += 3 and runningDelta = 3
     * -> next instant, mostRecentTemperature = 385, runningDelta += 2 and runningDelta = 5
     * -> next instant, mostRecentTemperature = 379, runningDelta += -6 and runningDelta = -1
     * -> next instant, mostRecentTemperature = 358, runningDelta += -21 and runningDelta = -22
     * now |runningDelta| >= 20 so send a notification that there has been too big a change in temperature
     */
    private long runningDelta;


    public TemperatureRecord() {
        this.mostRecentTemperature = 0;
        this.timeRecorded = null;
        this.runningDelta = 0;
    }

    public long getMostRecentTemperature() {
        return mostRecentTemperature;
    }

    public void setMostRecentTemperature(long newTemperature, Date timeRecorded) {
        if (this.timeRecorded != null && this.timeRecorded.after(timeRecorded)) {
            throw new RuntimeException("New temperature was recorded before the current most recent temperature.");
        }

        // update running delta
        long oldTemperature = this.mostRecentTemperature;
        this.runningDelta += newTemperature - oldTemperature;
        this.mostRecentTemperature = newTemperature;

        // update time recorded
        this.timeRecorded = timeRecorded;

        this.setChanged();
        this.notifyObservers();
    }

    public Date getTimeRecorded() {
        return timeRecorded;
    }

    public long getRunningDelta() {
        return runningDelta;
    }
}
