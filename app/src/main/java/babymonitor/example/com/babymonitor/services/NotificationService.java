package babymonitor.example.com.babymonitor.services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;

import babymonitor.example.com.babymonitor.MainActivity;
import babymonitor.example.com.babymonitor.R;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;

public class NotificationService extends Service implements ValueEventListener{
    public final static String ON_RECEIVE_DATA ="com.babymonitor.onreceivedata";
    private final static String FIREBASE_URL = "https://boiling-torch-7535.firebaseio.com";
    private Firebase firebase = null;

    private static long TOO_BIG_TEMPERATURE_DELTA = 20;
    private static long TOO_HIGH_TEMPERATURE = 400;
    private static long TOO_LOW_TEMPERATURE = 360;


    private long mostRecentTemperature;
    private Date timeMostRecentTemperatureWasRecordedAt;

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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setUpFirebase();
        return Service.START_STICKY;
    }

    public void setUpFirebase() {
        // get handle to firebase and authenticate
        Firebase.setAndroidContext(this);
        this.firebase = new Firebase(NotificationService.FIREBASE_URL);
        this.firebase.authWithPassword("eoogwe@gmail.com", "idiot", new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
            }
        });

        // start listening to temperature changes
        Firebase temperatureRef = this.firebase.child("temperature");
        if (temperatureRef == null) {
            throw new RuntimeException("No temperature key available ");
        }
        temperatureRef.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        // NB: this gets called once when the service is instantiated
        if (snapshot.getKey().equals("temperature")) {
            long newTemperature = (Long) snapshot.getValue();
            this.updateTemperature(newTemperature);
            this.broadcastTemperature();
            this.checkTemperatureSafety();
        }

    }

    private void updateTemperature(long newTemperature) {
        // update local fields
        long oldTemperature = this.mostRecentTemperature;
        this.runningDelta += newTemperature - oldTemperature;
        this.mostRecentTemperature = newTemperature;
        this.timeMostRecentTemperatureWasRecordedAt = new Date();
    }

    private void broadcastTemperature() {
        Intent intent = new Intent();
        intent.setAction(ON_RECEIVE_DATA);
        intent.putExtra("temperature", mostRecentTemperature);
        sendBroadcast(intent);
    }

    private void checkTemperatureSafety() {
        if (this.mostRecentTemperature <= NotificationService.TOO_LOW_TEMPERATURE) {
            this.soundTemperatureAlarm("Too low: " + this.mostRecentTemperature);
        } else if (NotificationService.TOO_HIGH_TEMPERATURE <= this.mostRecentTemperature) {
            this.soundTemperatureAlarm("Too high: " + this.mostRecentTemperature);
        }

        if (NotificationService.TOO_BIG_TEMPERATURE_DELTA <= Math.abs(this.runningDelta)) {
            this.soundTemperatureAlarm("Too big a temperature change: " + this.runningDelta);
        }
    }

    private void soundTemperatureAlarm(String message) {
        Notification.Builder nb = new Notification.Builder(this)
                .setContentTitle("TEMPERATURE ALERT")
                .setContentText(message)
                .setLights(1000, 200, 100)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setSmallIcon(R.drawable.abc_btn_check_material);

        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder tsb = TaskStackBuilder.create(this);
        tsb.addParentStack(MainActivity.class);
        tsb.addNextIntent(intent);

        PendingIntent pendingIntent = tsb.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        nb.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(100, nb.build());
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        System.out.println("Firebase Error:"+ firebaseError.toString());
    }
}
