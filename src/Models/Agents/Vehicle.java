package Models.Agents;

import jade.core.Agent;

public class Vehicle extends Agent {

    public static int AIR = 1;
    public static int STREET = 2;

    private int mSpeed;
    private int mBateryCharge;
    private int mLoadCapacity;
    private int mMovementType;

    public Vehicle(int mSpeed, int mBateryCharge, int mLoadCapacity, int mMovementType) {
        this.mSpeed = mSpeed;
        this.mBateryCharge = mBateryCharge;
        this.mLoadCapacity = mLoadCapacity;
        this.mMovementType = mMovementType;
    }
}
