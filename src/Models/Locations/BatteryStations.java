package Models.Locations;

public class BatteryStations extends Location {

    private float chargePerMinute;

    public BatteryStations(String name, float chargePerMinute)
    {
        super(name);
        this.chargePerMinute = chargePerMinute;
    }


    public float getChargePerMinute() {
        return chargePerMinute;
    }

    public void setChargePerMinute(float chargePerMinute) {
        this.chargePerMinute = chargePerMinute;
    }
}
