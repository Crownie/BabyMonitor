package babymonitor.example.com.babymonitor;

import babymonitor.example.com.babymonitor.services.NotificationService;

import java.util.Observable;
import java.util.Observer;

/**
 * Responsible for monitoring a temperature record and getting notifications sent out if dangerous.
 */
public class TemperatureMonitor implements Observer {

    private final NotificationService notificationService;

    public TemperatureMonitor(NotificationService notificationService) {
        if (notificationService == null) {
            throw new NullPointerException();
        }
        this.notificationService = notificationService;
    }

    public void monitorTemperatureRecord(TemperatureRecord record) {
        record.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        TemperatureRecord record = (TemperatureRecord) observable;
        long mostRecentTemperature = record.getMostRecentTemperature();
        long runningDelta = record.getRunningDelta();
        this.checkTemperatureSafety(mostRecentTemperature, runningDelta);
        // also notify the UI of the temperature change
        this.notificationService.broadcastTemperature(mostRecentTemperature);
    }

    /**
     * Can potentially send out two notifications (if both runningDelta and mostRecentTemperature are dangerous)
     */
    private void checkTemperatureSafety(long mostRecentTemperature, long runningDelta) {

        if (mostRecentTemperature <= TemperatureRecord.TOO_LOW_TEMPERATURE) {
            this.notificationService.soundTemperatureAlarm("Too low: " + mostRecentTemperature);
        } else if (TemperatureRecord.TOO_HIGH_TEMPERATURE <= mostRecentTemperature) {
            this.notificationService.soundTemperatureAlarm("Too high: " + mostRecentTemperature);
        }

        if (TemperatureRecord.TOO_BIG_TEMPERATURE_DELTA <= Math.abs(runningDelta)) {
            this.notificationService.soundTemperatureAlarm("Too big a temperature change: " + runningDelta);
        }
    }

}
