package babymonitor.example.com.babymonitor;

import babymonitor.example.com.babymonitor.services.TemperatureMonitorService;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class TemperatureChangeRecorder implements ChildEventListener {

    public static long TOO_BIG_TEMPERATURE_DELTA = 20;
    public static long TOO_HIGH_TEMPERATURE = 400;
    public static long TOO_LOW_TEMPERATURE = 360;

    private final TemperatureMonitorService service;
    private long mostRecentTemperature = 0L;
    private long mostRecentTimestamp = 0L;
    private long runningDelta = 0L;

    public TemperatureChangeRecorder(TemperatureMonitorService service) {
        if (service == null) {
            throw new NullPointerException();
        }
        this.service = service;
    }

    public long getMostRecentTemperature() {
        return mostRecentTemperature;
    }

    public long getMostRecentTimestamp() {
        return mostRecentTimestamp;
    }

    public long getRunningDelta() {
        return runningDelta;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        System.out.println("Snapshot received: " + dataSnapshot);
        long oldTimestamp = this.mostRecentTimestamp;
        long oldTemperature = this.mostRecentTemperature;

        long newTimestamp = Long.parseLong(dataSnapshot.getKey());
        long newTemperature = (Long) dataSnapshot.getValue();

        if (newTimestamp < oldTimestamp) {
            throw new RuntimeException("Temperature data point from the past published.");
        }

        // update running delta
        this.runningDelta += newTemperature - oldTemperature;

        // update instance fields
        this.mostRecentTemperature = newTemperature;
        this.mostRecentTimestamp = newTimestamp;

        // broadcast change in temperature
        this.service.broadcastTemperature(this.mostRecentTemperature);

        /**
         * Temperature safety checks
         * Can potentially send out two notifications (if both runningDelta and mostRecentTemperature are dangerous)
         */
        if (this.mostRecentTemperature <= TOO_LOW_TEMPERATURE) {
            this.service.soundTemperatureAlarm("Too low: " + this.mostRecentTemperature);
        } else if (TOO_HIGH_TEMPERATURE <= this.mostRecentTemperature) {
            this.service.soundTemperatureAlarm("Too high: " + this.mostRecentTemperature);
        }

        if (TOO_BIG_TEMPERATURE_DELTA <= Math.abs(this.runningDelta)) {
            this.service.soundTemperatureAlarm("Too big a temperature change: " + this.runningDelta);
        }
    }

    /**
     * Timestamps are immutable and shouldn't change once added so if this
     * method is called, an exception will be thrown.
     * @param dataSnapshot
     * @param previousChildName
     */
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        throw new RuntimeException("Temperature data point altered.");
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        System.out.println(firebaseError);
    }
}
