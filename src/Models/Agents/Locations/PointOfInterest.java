package Models.Agents.Locations;


import javafx.util.Pair;

public class PointOfInterest extends Location {
    private String name;

    public PointOfInterest() {
        super();
    }

    public PointOfInterest(int id, String name)
    {
        super(id, new Pair<>(0.0f,0.0f));
        this.name = name;
    }


    public PointOfInterest(int id, String name, Pair<Float, Float> mPosition)
    {
        super(id, mPosition);
        this.name = name;
    }


    public String getPOIName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
