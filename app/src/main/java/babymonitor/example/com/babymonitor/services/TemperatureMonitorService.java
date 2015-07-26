package babymonitor.example.com.babymonitor.services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import babymonitor.example.com.babymonitor.MainActivity;
import babymonitor.example.com.babymonitor.R;
import babymonitor.example.com.babymonitor.TemperatureMonitor;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class TemperatureMonitorService extends Service {

    public final static String ON_RECEIVE_DATA ="com.babymonitor.onreceivedata";
    private final static String FIREBASE_URL = "https://boiling-torch-7535.firebaseio.com";

    private Firebase firebase;

    @Override
    public void onCreate() {
        // The service is being created (only called once)
        Firebase.setAndroidContext(this);
        this.firebase = new Firebase(TemperatureMonitorService.FIREBASE_URL);
        this.firebase.authAnonymously(new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        // Called when startService() is called
        Firebase temperaturesRef = this.firebase.child("temperatures");
        if (temperaturesRef == null) {
            throw new RuntimeException("Couldn't get a reference to the 'temperatures' keystore");
        } else {
            if (!temperaturesRef.getKey().equals("temperatures")) throw new AssertionError();
            temperaturesRef.addChildEventListener(new TemperatureMonitor(this));
        }
        return Service.START_STICKY;
    }

    public void broadcastTemperature(long temperature) {
        Intent intent = new Intent(ON_RECEIVE_DATA);
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

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return null;
    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        // All clients have unbound with unbindService()
//        return true;
//    }
//
//    @Override
//    public void onRebind(Intent intent) {
//        // A client is binding to the service with bindService(),
//        // after onUnbind() has already been called
//    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }

}
