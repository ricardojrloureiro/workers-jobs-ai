package Models.Locations;

public class BatteryStations extends PointOfInterest {

    private float chargePerMinute;

    public BatteryStations(int id, String name, float chargePerMinute)
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
