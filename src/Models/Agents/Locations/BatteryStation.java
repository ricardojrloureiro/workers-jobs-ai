package Models.Agents.Locations;

public class BatteryStation extends PointOfInterest {

    public static final String BATTERYSTATION_TYPE = "battery_station";
    private float chargePerMinute;

    public BatteryStation(int id, String name, float chargePerMinute)
    {
        super(id, name);
        this.chargePerMinute = chargePerMinute;
    }


    public float getChargePerMinute() {
        return chargePerMinute;
    }

    public void setChargePerMinute(float chargePerMinute) {
        this.chargePerMinute = chargePerMinute;
    }
}
