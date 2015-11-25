package Models.Agents.Vehicles;

import Models.Agents.Locations.Location;
import Models.Jobs.FixedPriceJob;
import Models.Map;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class Vehicle extends Agent {

    public static int AIR = 1;
    public static int STREET = 2;

    private int mMoney;
    private int mSpeed;
    private int mBateryCharge;
    private int mBateryCapacity;
    private int mLoadCapacity;
    private int mMovementType;

    private Pair<Float, Float> mCurrentPosition = new Pair<>(0.0f, 0.0f);

    private ArrayList<Integer> mTools = new ArrayList<>();

    private Map mMap;

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public void setmBateryCapacity(int mBateryCapacity) {
        this.mBateryCapacity = mBateryCapacity;
    }

    public void setmMoney(int mMoney) {
        this.mMoney = mMoney;
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

    public Pair<Float, Float> getCurrentPosition() {
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

    /**
     * Verifica se tem todas as tools
     * se não:
     * - Verifica quanto terá de gastar em sub trabalhos
     * - Verifica distância mais preço nas lojas
     * <p>
     * Verifica quanto terá de gastar em gasolina
     */
    public int evaluateAction(FixedPriceJob job) {

        boolean hasAllTools = true;
        int hasAllToolsEval = 5;
        int compensationValue = 0;
        int costOfWork=0;

        for (int i : job.getRequiredTools()) {
            if (!mTools.contains(i))
                hasAllTools = false;
        }

        // evaluates tools
        if (hasAllTools) {
            compensationValue += hasAllToolsEval;
        } else {
            // check how much it will require to finish the job
            // buying from houses or sub jobs
            //TODO acrescentar métodos para criacao de subtrabalhos

            costOfWork=1000;
        }

        Location currentLocation = this.mMap.getLocationIdFromPosition(this.mCurrentPosition);
        Location nearestBatteryStationLocation = this.mMap.getNearestBatteryStation(
                (this.mMap.getLocationIdFromPosition(this.mCurrentPosition)));
        Location jobLocation = this.mMap.getLocationFromId(job.getFinalDestinationId());

        float distanceToNearestBatteryStation = this.mMap.getLocationsDistance(currentLocation, nearestBatteryStationLocation);

        //evaluates distances
        float distance = this.mMap.getLocationsDistance(
                currentLocation,
                jobLocation
        );

        // evaluates the distance necessary if a recharge is required
        if (distance + distanceToNearestBatteryStation >= this.mBateryCharge) {
            distance = this.mMap.getDistancePassingLocation(
                    currentLocation,
                    nearestBatteryStationLocation,
                    jobLocation
            );
        }


        compensationValue = Math.round(job.getPrice() - (costOfWork + distance));


        System.out.println("compensation value is: " + compensationValue);
        return compensationValue;

    }

    public Boolean performAction(FixedPriceJob job) {

        System.out.println("DOING JOB");


        System.out.println("Price: " + job.getPrice());
        System.out.println("Products to make: " + Arrays.toString(job.getProductsToMake()));


        return true;
    }

}
