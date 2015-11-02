package Models.Agents.Vehicles;

import Models.Map;
import jade.core.Agent;
import javafx.util.Pair;

import java.util.ArrayList;

public class Vehicle extends Agent {

    public static int AIR = 1;
    public static int STREET = 2;

    private int mSpeed;
    private int mBateryCharge;
    private int mLoadCapacity;
    private int mMovementType;

    private Pair<Float, Float> mCurrentPosition;

    private ArrayList<Integer> mTools;

    private Map mMap;


    public Vehicle(int mSpeed, int mBateryCharge, int mLoadCapacity, int mMovementType, Pair<Float, Float> mCurrentPosition, ArrayList<Integer> mTools) {
        this.mSpeed = mSpeed;
        this.mBateryCharge = mBateryCharge;
        this.mLoadCapacity = mLoadCapacity;
        this.mMovementType = mMovementType;
        this.mCurrentPosition = mCurrentPosition;
        this.mTools = mTools;
        // Load the map
        this.mMap = Map.getMap();
    }

    public Map getmMap() {
        return this.mMap;
    }

}
