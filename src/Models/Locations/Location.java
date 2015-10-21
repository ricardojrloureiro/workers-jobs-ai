package Models.Locations;


import javafx.util.Pair;

public class Location {
    private Pair<Float, Float> mPosition;
    private String name;


    public Location(String name)
    {
        this.name = name;
        this.mPosition = new Pair<>(0.0f,0.0f);
    }


    public Location(String name, Pair<Float, Float> mPosition)
    {
        this.name = name;
        this.mPosition = mPosition;
    }

    public Pair<Float, Float> getPosition() {
        return mPosition;
    }

    public void setPosition(Pair<Float, Float> mPosition) {
        this.mPosition = mPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
