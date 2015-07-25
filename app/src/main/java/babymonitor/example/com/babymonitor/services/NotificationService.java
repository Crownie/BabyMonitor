package babymonitor.example.com.babymonitor.services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import babymonitor.example.com.babymonitor.*;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class NotificationService extends Service {
    public final static String ON_RECEIVE_DATA ="com.babymonitor.onreceivedata";
    private final static String FIREBASE_URL = "https://boiling-torch-7535.firebaseio.com";

    private TemperatureRecord record;
    private TemperatureChangeListener listener;
    private TemperatureMonitor monitor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.record = new TemperatureRecord();
        this.listener = new TemperatureChangeListener(this.record);
        this.monitor = new TemperatureMonitor(this);
        this.monitor.monitorTemperatureRecord(this.record);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setUpFirebase();
        return Service.START_STICKY;
    }

    public void setUpFirebase() {
        // get handle to firebase and authenticate
        Firebase.setAndroidContext(this);
        Firebase firebase = new Firebase(NotificationService.FIREBASE_URL);
        firebase.authWithPassword("eoogwe@gmail.com", "idiot", new Firebase.AuthResultHandler() {
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
        Firebase temperatureRef = firebase.child("temperature");
        if (temperatureRef == null) {
            throw new RuntimeException("No temperature key available from Firebase.");
        }
        temperatureRef.addValueEventListener(this.listener);
    }

    /**
     * Broadcast most recent temperature
     */
    public void broadcastTemperature(long temperature) {
        Intent intent = new Intent();
        intent.setAction(ON_RECEIVE_DATA);
        intent.putExtra("temperature", temperature);
        sendBroadcast(intent);
    }

    public void soundTemperatureAlarm(String message) {
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
}
