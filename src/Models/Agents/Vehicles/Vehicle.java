package Models.Agents.Vehicles;

import Models.Agents.Locations.BatteryStation;
import Models.Agents.Locations.Location;
import Models.Jobs.Job;
import Models.Map;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Vehicle extends Agent {

    public static int AIR = 1;
    public static int STREET = 2;

    public boolean available = true;

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

    public ArrayList<Location> getBestPathToJob(Job job)
    {

        ArrayList<Location> path = new ArrayList<>();

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

       // System.out.println("Distance Job - BS_Job: " + distanceJobToBatteryStation);
       // System.out.println("Distance Agent - BS_Agent: " + distanceCurrentLocationToBatteryStation);
       // System.out.println("Distance BS_Agent - Job: " + distanceNearestBSCurrentPositionToJob);
       // System.out.println("Distance Agent - Job: " + distanceAgentToJob);

        float distanceJobBS = distanceAgentToJob + distanceJobToBatteryStation;
        float distanceBSJob = distanceCurrentLocationToBatteryStation + distanceNearestBSCurrentPositionToJob;


        //System.out.println("Distance Job -> BS: " + distanceJobBS);
        //System.out.println("Distance BS -> Job: " + distanceBSJob);

        float totalTimeJobBS = -1;
        float totalTimeBSJob = -1;
        if(distanceBSJob < this.getBateryCharge())
        {

            float bsVehicleCharge = this.getBateryCharge() - distanceCurrentLocationToBatteryStation;
            float chargeLeftWhenOnBS = this.mBateryCapacity - bsVehicleCharge;
            float timeToCharge = chargeLeftWhenOnBS /nearestBatteryStationCurrentLocation.getChargePerMinute();
            //System.out.println("Time to charge: " + timeToCharge);

            totalTimeBSJob = timeToTravelDistance(distanceBSJob) + timeToCharge;
        }
        if(distanceJobBS < this.getBateryCharge())
        {
            totalTimeJobBS = timeToTravelDistance(distanceAgentToJob);
        }

        if(totalTimeJobBS == -1)
            totalTimeJobBS = totalTimeBSJob+1;
        if(totalTimeBSJob == -1)
            totalTimeBSJob = totalTimeJobBS+1;

        path.add(currentLocation);
        if(totalTimeBSJob > totalTimeJobBS)
        {
            path.add(nearestBatteryStationCurrentLocation);
            path.add(jobLocation);
        }else{
            path.add(jobLocation);
        }

        return path;
    }

    /**
     *
     * @param job that will be analysed
     * @return an integer that represents the value of the current job
     */
    public int evaluateAction(Job job) {

        if(!available)
            return 0;

        available = false;

        int toolsMissing = 0;

        for (int i : job.getRequiredTools()) {
            if (!mTools.contains(i))
                toolsMissing++;
        }

        ArrayList<Location> path = getBestPathToJob(job);

        float totalDistance = 0;

        for(int i = 0; i < path.size()-1; i++)
        {

            totalDistance += this.mMap.getLocationsDistance(
                    path.get(i),
                    path.get(i+1)
            );
        }

        float totalTime = timeToTravelDistance(totalDistance);

        return Math.round(totalTime + toolsMissing);
    }

    /**
     *
     * @param job
     * @return
     */
    public Boolean performAction(Job job) {

        new WorkThread(this,job).start();

        return true;
    }

    /**
     *
     * @param finalLocation final location
     * @return true or false, depending if had enough gas to perform the transition
     */
    public boolean moveToLocation(Location finalLocation) {

        //System.out.println("mover para a localizacao " + finalLocation.getLocationName());

        Location currentLocation = this.mMap.getLocationIdFromPosition(this.mCurrentPosition);
        float distance = this.mMap.getLocationsDistance(
                currentLocation,
                finalLocation
        );

        if(mBateryCharge - distance  < 0) {
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

        System.out.println("chegou a localizacao " + finalLocation.getLocationName());
        return true;
    }

}
