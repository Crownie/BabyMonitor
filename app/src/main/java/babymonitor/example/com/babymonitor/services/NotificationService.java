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
        Firebase myFirebaseRef = new Firebase("http://boiling-torch-7535.firebaseio.com");

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

        myFirebaseRef.child("temperature").addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {

        if(snapshot.getKey().equals("temperature")){
            System.out.println("Randomaskmakmskkldksdkls  "+snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
        }

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        System.out.println("Firebase Error:"+ firebaseError.toString());
    }
}
