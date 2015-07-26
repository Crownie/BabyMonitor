package babymonitor.example.com.babymonitor;

/**
 * Instances of this class represent settings for a temperature monitor.
 */
public class TemperatureMonitorSettings {

    private long tooLowTemperature;
    private long tooHighTemperature;
    private long tooBigDelta;

    public long getTooBigDelta() {
        return tooBigDelta;
    }

    public void setTooBigDelta(long tooBigDelta) {
        this.tooBigDelta = tooBigDelta;
    }

    public long getTooHighTemperature() {
        return tooHighTemperature;
    }

    public void setTooHighTemperature(long tooHighTemperature) throws Exception {
        if (this.tooLowTemperature < tooHighTemperature) {
            this.tooHighTemperature = tooHighTemperature;
        } else {
            throw new Exception("tooHighTemperature must be greater than " + this.tooLowTemperature);
        }
    }

    public long getTooLowTemperature() {
        return tooLowTemperature;
    }

    public void setTooLowTemperature(long tooLowTemperature) throws Exception {
        this.tooLowTemperature = tooLowTemperature;
        if (tooLowTemperature < this.tooHighTemperature) {
            this.tooLowTemperature = tooLowTemperature;
        } else {
            throw new Exception("tooLowTemperature must be less than " + this.tooHighTemperature);
        }
    }

    public TemperatureMonitorSettings(long tooLowTemperature, long tooHighTemperature, long tooBigDelta) {
        if (tooHighTemperature < tooLowTemperature) {
            tooHighTemperature = tooLowTemperature;
        }
        this.tooLowTemperature = tooLowTemperature;
        this.tooHighTemperature = tooHighTemperature;
        this.tooBigDelta = tooBigDelta;
    }
}
