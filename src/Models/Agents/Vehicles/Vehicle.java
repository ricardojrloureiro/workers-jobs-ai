package Models.Agents.Vehicles;

import Models.Agents.Locations.BatteryStation;
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


    public float timeToTravelDistance(float distance)
    {
        return distance/this.getSpeed();
    }

    /**
     *
     * @param job that will be analysed
     * @return an integer that represents the value of the current job
     */
    public int evaluateAction(FixedPriceJob job) {

        int toolsMissing = 0;

        for (int i : job.getRequiredTools()) {
            if (!mTools.contains(i))
                toolsMissing++;
        }

        Location currentLocation = this.mMap.getLocationIdFromPosition(this.mCurrentPosition);
        Location jobLocation = this.mMap.getLocationFromId(job.getFinalDestinationId());

        // nearest battery station from agent current location
        BatteryStation nearestBatteryStationCurrentLocation =  (BatteryStation) this.mMap.getNearestBatteryStation(currentLocation);

        // nearest battery station from job location
        BatteryStation nearestBatteryStationJobLocation = (BatteryStation) this.mMap.getNearestBatteryStation(jobLocation);

        // distance from jog location to battery station
        float distanceJobToBatteryStation = this.mMap.getLocationsDistance(
                jobLocation,
                nearestBatteryStationJobLocation
        );

        // distance from agent location to battery station
        float distanceCurrentLocationToBatteryStation = this.mMap.getLocationsDistance(
                currentLocation,
                nearestBatteryStationCurrentLocation
        );

        // distance from agent to job
        float distanceAgentToJob = this.mMap.getLocationsDistance(
                currentLocation,
                jobLocation
        );

        // distance from agent's nearest battery station to job
        float distanceNearestBSCurrentPositionToJob = this.mMap.getLocationsDistance(
                nearestBatteryStationCurrentLocation,
                jobLocation
        );

        System.out.println("Distance Job - BS_Job: " + distanceJobToBatteryStation);
        System.out.println("Distance Agent - BS_Agent: " + distanceCurrentLocationToBatteryStation);
        System.out.println("Distance BS_Agent - Job: " + distanceNearestBSCurrentPositionToJob);
        System.out.println("Distance Agent - Job: " + distanceAgentToJob);


        float distanceJobBS = distanceAgentToJob + distanceJobToBatteryStation;
        float distanceBSJob = distanceCurrentLocationToBatteryStation + distanceNearestBSCurrentPositionToJob;


        System.out.println("Distance Job -> BS: " + distanceJobBS);
        System.out.println("Distance BS -> Job: " + distanceBSJob);


        if(distanceJobBS > this.getBateryCharge())
        {
            return 0;
        }
        else if(distanceBSJob > this.getBateryCharge())
        {
            return 0;
        }

        float bsVehicleCharge = this.getBateryCharge() - distanceCurrentLocationToBatteryStation;
        float chargeLeftWhenOnBS = this.mBateryCapacity - bsVehicleCharge;
        float timeToCharge = chargeLeftWhenOnBS /nearestBatteryStationCurrentLocation.getChargePerMinute();

        System.out.println("Time to charge: " + timeToCharge);

        float totalTimeBSJob = timeToTravelDistance(distanceBSJob) + timeToCharge;
        float totalTimeJobBS = timeToTravelDistance(distanceAgentToJob);

        System.out.println("Total time BS -> Job: " + totalTimeBSJob);
        System.out.println("Total time Job -> BS: " + totalTimeJobBS);

        return (totalTimeBSJob < totalTimeJobBS) ? Math.round(totalTimeBSJob + toolsMissing) : Math.round(totalTimeJobBS + toolsMissing);
    }

    /**
     *
     * @param job
     * @return
     */
    public Boolean performAction(FixedPriceJob job) {

        System.out.println("DOING JOB");


        System.out.println("Price: " + job.getPrice());
        System.out.println("Products to make: " + Arrays.toString(job.getProductsToMake()));


        return true;
    }

    /**
     *
     * @param finalLocation final location
     * @return true or false, depending if had enough gas to perform the transition
     */
    public boolean moveToLocation(Location finalLocation) {
        Location currentLocation = this.mMap.getLocationIdFromPosition(this.mCurrentPosition);
        float distance = this.mMap.getLocationsDistance(
                currentLocation,
                finalLocation
        );

        if(distance - mBateryCharge < 0) {
            return false;
        } else {
            this.mBateryCharge -= distance;
        }

        long travelTime = (long) (distance/this.mSpeed * 1000);

        try {
            Thread.sleep(travelTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.mCurrentPosition = finalLocation.getPosition();

        return true;
    }

}
