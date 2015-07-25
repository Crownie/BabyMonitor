package babymonitor.example.com.babymonitor.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class NotificationService extends Service implements ValueEventListener{
    public final static String ON_RECEIVE_DATA ="com.babymonitor.onreceivedata";
    private final static String FIREBASE_URL = "https://boiling-torch-7535.firebaseio.com";

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setUpFirebase();
        return Service.START_STICKY;
    }

    public void setUpFirebase(){
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase(NotificationService.FIREBASE_URL);

        myFirebaseRef.authWithPassword("eoogwe@gmail.com", "idiot", new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
            }
        });

        Firebase temperatureRef = myFirebaseRef.child("temperature");

        if (temperatureRef == null) {
            // TODO proper handled exception
            throw new RuntimeException("Could not fetch temperature.");
        }
        temperatureRef.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        // NB: this gets called once when the service is instantiated
        if (snapshot.getKey().equals("temperature")) {
            long mostRecentTemperature = (Long) snapshot.getValue();
            this.updateTemperature(mostRecentTemperature);
        }

    }

    private void updateTemperature(long mostRecentTemperature) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        System.out.println("Firebase Error:"+ firebaseError.toString());
    }
}
