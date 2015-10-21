package Models.Locations;

import javafx.util.Pair;

public class Location {

    private Pair<Float, Float> mPosition;
    private int mId;

    public Location(int id, Pair<Float,Float> position) {
        this.mId = id;
        this.mPosition = position;
    }

    public Pair<Float, Float> getPosition() {
        return mPosition;
    }

    public void setPosition(Pair<Float, Float> mPosition) {
        this.mPosition = mPosition;
    }


}
