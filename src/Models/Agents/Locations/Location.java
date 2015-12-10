package Models.Agents.Locations;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;

public class Location extends Agent {

    private Pair<Float, Float> mPosition;
    private int mId;
    private String name;

    public Location() {

    }

    public Location(int id, Pair<Float,Float> position) {
        this.mId = id;
        this.mPosition = position;
    }

    public Pair<Float, Float> getPosition() {
        return mPosition;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public void setPosition(Pair<Float, Float> mPosition) {
        this.mPosition = mPosition;
    }

    public String getLocationName() { return this.name; }

    public void setName(String name) { this.name = name; }

    @Override public String toString() {
        return "" + mId;
    }


}
