package babymonitor.example.com.babymonitor;

import babymonitor.example.com.babymonitor.services.TemperatureMonitorService;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class TemperatureMonitor implements ChildEventListener {

    private final TemperatureMonitorService service;
    private final TemperatureMonitorSettings settings;

    private long mostRecentTemperature = 0L;
    private long mostRecentTimestamp = 0L;
    private long babyTemperature = 0L;

    public TemperatureMonitor(TemperatureMonitorService service) {
        if (service == null) {
            throw new NullPointerException();
        }
        this.service = service;
        this.settings = new TemperatureMonitorSettings(20, 50, 100);
    }

    public void setBabyTemperature(long babyTemperature) {
        this.babyTemperature = babyTemperature;
    }

    public TemperatureMonitorSettings getSettings() {
        return settings;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        // initially called at instantiation for every child in chronological order
        // if still more children, don't sound alarm!!
        long oldTimestamp = this.mostRecentTimestamp;
        long oldTemperature = this.mostRecentTemperature;

        long newTimestamp = Long.parseLong(dataSnapshot.getKey());
        long newTemperature = (Long) dataSnapshot.getValue();

        if (newTimestamp < oldTimestamp) {
            System.out.println("WARNING: Temperature data point from the past published.");
        }

        // update instance fields
        this.mostRecentTemperature = newTemperature;
        this.mostRecentTimestamp = newTimestamp;

        // broadcast change in temperature
        this.service.broadcastTemperature(this.mostRecentTemperature);

        /**
         * Temperature safety checks
         * Can potentially send out two notifications (if both runningDelta and mostRecentTemperature are dangerous)
         */
        if (this.mostRecentTemperature <= this.settings.getTooLowTemperature()) {
            this.service.soundTemperatureAlarm("Too low: " + this.mostRecentTemperature);
        } else if (this.settings.getTooHighTemperature() <= this.mostRecentTemperature) {
            this.service.soundTemperatureAlarm("Too high: " + this.mostRecentTemperature);
        }

        long delta = this.mostRecentTemperature - this.babyTemperature;
        if (this.settings.getTooBigDelta() <= Math.abs(delta)) {
            this.service.soundTemperatureAlarm("Too big a temperature change: " + delta);
        }
    }

    /**
     * Timestamps are immutable and shouldn't change once added.
     */
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        System.out.println("WARNING: Temperature data point altered.");
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
