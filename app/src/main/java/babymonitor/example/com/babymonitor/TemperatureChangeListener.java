package babymonitor.example.com.babymonitor;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;

/**
 * Responsible for listening for changes in temperature from Firebase and
 * updating the associated TemperatureRecord.
 */
public class TemperatureChangeListener implements ValueEventListener {

    private final TemperatureRecord record;
    
    /**
     *
     * @param record the temperature record to be updated
     */
    public TemperatureChangeListener(TemperatureRecord record) {
        if (record == null) {
            throw new NullPointerException();
        }
        this.record = record;
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        // NB: this gets called once when the service is instantiated
        if (snapshot == null) {
            throw new NullPointerException();
        }
        if (snapshot.getKey() == null || !snapshot.getKey().equals("temperature")) {
            throw new RuntimeException("This TemperatureChangeListener should listen for a change in the value of key 'temperature', not " + snapshot.getKey());
        }

        Object value = snapshot.getValue();
        if (value == null) {
            throw new RuntimeException("No value for key 'temperature'");
        }

        long newTemperature = (Long) value;
        Date now = new Date();

        this.record.setMostRecentTemperature(newTemperature, now);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        System.out.println("Firebase Error:" + firebaseError.toString());
    }
}
