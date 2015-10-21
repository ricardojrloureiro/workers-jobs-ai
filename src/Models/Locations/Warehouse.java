package Models.Locations;


public class Warehouse extends Location {
    private float maxWeight;

    public Warehouse(String name, float maxWeight)
    {
        super(name);
        this.maxWeight = maxWeight;
    }
}
