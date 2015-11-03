package Models.Agents.Vehicles;

import Models.Map;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import javafx.util.Pair;

import java.util.ArrayList;

public class Vehicle extends Agent {

    public static int AIR = 1;
    public static int STREET = 2;

    private int mSpeed;
    private int mBateryCharge;
    private int mLoadCapacity;
    private int mMovementType;

    private Pair<Float, Float> mCurrentPosition = new Pair<>(0.0f,0.0f);

    private ArrayList<Integer> mTools = new ArrayList<>();

    private Map mMap;


    //public Vehicle(int mSpeed, int mBateryCharge, int mLoadCapacity, int mMovementType, Pair<Float, Float> mCurrentPosition, ArrayList<Integer> mTools) {
    //    this.mSpeed = mSpeed;
    //    this.mBateryCharge = mBateryCharge;
    //    this.mLoadCapacity = mLoadCapacity;
    //    this.mMovementType = mMovementType;
    //    this.mCurrentPosition = mCurrentPosition;
    //    this.mTools = mTools;
    //}


    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setBatteryCharge(int batteryCharge) {
        this.mBateryCharge = batteryCharge;
    }

    public int getBateryCharge() {
        return mBateryCharge;
    }

    public void setLoadCapacity(int loadCapacity) {
        this.mLoadCapacity = loadCapacity;
    }

    public int getLoadCapacity() {
        return mLoadCapacity;
    }

    public void setMovementType(int movementType) {
        this.mMovementType = movementType;
    }

    public int getMovementType() {
        return mMovementType;
    }

    public void setCurrentPosition(Pair<Float, Float> position) {
        this.mCurrentPosition = position;
    }

    public Pair<Float,Float> getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setTools(ArrayList<Integer> tools) {
        this.mTools = tools;
    }

    public ArrayList<Integer> getTools() {
        return mTools;
    }

    public Map getmMap() {
        return this.mMap;
    }

    public void setMap(AgentContainer ac) {
        this.mMap = Map.getMap(ac);
    }

}
