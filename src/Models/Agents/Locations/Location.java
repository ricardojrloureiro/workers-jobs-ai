package Models.Agents.Locations;

import jade.core.Agent;
import javafx.util.Pair;

public class Location extends Agent {

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
