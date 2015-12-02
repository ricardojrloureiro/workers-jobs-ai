package Models.Agents.Locations;


import javafx.util.Pair;

public class PointOfInterest extends Location {

    public PointOfInterest() {
        super();
    }

    public PointOfInterest(int id, String name)
    {
        super(id, new Pair<>(0.0f,0.0f));
        this.setName(name);
    }


    public PointOfInterest(int id, String name, Pair<Float, Float> mPosition)
    {
        super(id, mPosition);
        this.setName(name);
    }
}
