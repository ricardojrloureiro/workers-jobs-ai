package Models.Agents.Vehicles;

import Models.Agents.Behaviours.AuctionVehicleBehaviour;
import Models.Agents.Behaviours.VehicleBehaviour;
import Models.Agents.JobContractor;
import Models.Agents.Locations.BatteryStation;
import Models.Agents.Locations.Location;
import Models.GraphVisualisation;
import Models.Jobs.Job;
import Models.PriceObject;
import Models.TimePricePair;
import Models.Tool;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.util.Pair;

import javax.swing.*;
import java.util.ArrayList;



public class Drone extends Vehicle {

    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.f1);
        return tools;
    }

    //public Drone() {
    //    super(5, 250, 100, Vehicle.AIR, new Pair<>(0.0f, 0.0f), Drone.generateToolsArray());
    //}

    private class DroneBehaviour extends SimpleBehaviour {
        private int n = 0;

        // construtor do behaviour
        public DroneBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = blockingReceive();
            if (msg.getPerformative() == ACLMessage.INFORM) {
                //Drone d = (Drone) this.getAgent();
                //System.out.println(d.getmMap().shortestPath(1,3));
            }

        }

        // método done
        public boolean done() {
            return n==10;
        }

    }

    // método setup
    protected void setup() {
        this.setMap(getContainerController());
        this.setSpeed(5);
        this.setBatteryCharge(250);
        this.setLoadCapacity(100);
        this.setMovementType(Vehicle.AIR);
        this.setCurrentPosition(new Pair<>(12f, 10f));
        this.setTools(Drone.generateToolsArray());
        this.setmBateryCapacity(250);
        this.setmMoney(500);

        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Vehicle");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // cria behaviour

        MessageTemplate template = MessageTemplate.and(
        MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
        MessageTemplate.MatchPerformative(ACLMessage.CFP) );
        VehicleBehaviour b = new VehicleBehaviour(this, template);
        AuctionVehicleBehaviour auctionB = new AuctionVehicleBehaviour(this);
        addBehaviour(b);
        addBehaviour(auctionB);

        graphVisualisation = new GraphVisualisation(this.getName(), this.getmMap(), this);
    }

    // método takeDown
    protected void takeDown() {
        // retira registo no DF
        try {
            DFService.deregister(this);
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Location> getBestPathToJob(Job job, ArrayList<Location> storesToVisit) {
        ArrayList<Location> path = new ArrayList<>();

        Location currentLocation = getCurrentLocation();
        Location jobLocation = this.mMap.getLocationFromId(job.getFinalDestinationId());
        path.add(currentLocation);

        int storesVisited = 0;
        while(storesVisited < storesToVisit.size()) {
            Location nextStore = null;
            float shortestPath = -1;
            boolean nextStoreFound = false;
            for (Location s : storesToVisit) {
                float pathLength = getDistanceBeetwenLocations(currentLocation, this.mMap.getLocationIdFromPosition(s.getPosition()));
                //System.out.println("Distance to store : " + pathLength);
                if (shortestPath == -1 && hasBatteryToArriveLocationAndBatterryStation(s)) {
                    shortestPath = pathLength;
                    nextStore = s;
                    nextStoreFound = true;
                    //System.out.println("tem gasosa e e o 1");
                } else if (shortestPath > pathLength && hasBatteryToArriveLocationAndBatterryStation(s)) {
                    shortestPath = pathLength;
                    nextStore = s;
                    nextStoreFound = true;
                    //System.out.println("tem gasosa e e o 2");
                }
            }
            if (nextStoreFound){
                path.add(nextStore);
                storesToVisit.remove(nextStore);
                storesVisited++;
            }
            else if(getBateryCharge() == mBateryCapacity){
                return null;
            }else{
                // nearest battery station from agent current location
                BatteryStation nearestBatteryStationCurrentLocation =  (BatteryStation) this.mMap.getNearestBatteryStation(currentLocation);
                path.add(nearestBatteryStationCurrentLocation);
                currentLocation = nearestBatteryStationCurrentLocation;
            }
        }

        if(hasBatteryToArriveLocationAndBatterryStation(jobLocation))
        {
            path.add(jobLocation);
        }else{
            BatteryStation nearestBatteryStationCurrentLocation =  (BatteryStation) this.mMap.getNearestBatteryStation(currentLocation);
            path.add(nearestBatteryStationCurrentLocation);
            path.add(jobLocation);
        }

        return path;
    }

    private float getDistanceBeetwenLocations(Location l1, Location l2) {
        return (float)Math.sqrt(
                Math.pow(l1.getPosition().getKey() - l2.getPosition().getKey(),2)
                        + Math.pow(l1.getPosition().getValue() - l2.getPosition().getValue(),2)
        );
    }

    @Override
    protected boolean hasBatteryToArriveLocationAndBatterryStation(Location nextLocation) {
        Location currentLocation = this.mMap.getLocationIdFromPosition(this.mCurrentPosition);

        // nearest battery station from job location
        BatteryStation nearestBatteryStationJobLocation = (BatteryStation) this.mMap.getNearestBatteryStation(nextLocation);

        // distance from jog location to battery station
        float distanceJobToBatteryStation = getDistanceBeetwenLocations(
                nextLocation,
                nearestBatteryStationJobLocation
        );

        // distance from agent to job
        float distanceAgentToJob = this.mMap.getLocationsDistance(
                currentLocation,
                nextLocation
        );


        float distanceJobBS = distanceAgentToJob + distanceJobToBatteryStation;

        return distanceJobBS < this.getBateryCharge();

    }

    @Override
    public TimePricePair evaluateAction(Job job) {

        //System.out.println(this.getName() + " - Availability");
        if (!available)
            return null;

        available = false;

        PriceObject totalPrice = new PriceObject();

        if(getAllJobWeight(job) > getLoadCapacity())
            return null;

        ArrayList<Location> storeToVisit = locationsToVisit(job, totalPrice);

        //System.out.println(storeToVisit.size() + " - Stores to visit");
        if (storeToVisit == null)
            return null;

        //System.out.println(totalPrice.price + " - Total Price");
        if(totalPrice.price > job.getPrice())
        {
            return null;
        }

        //System.out.println(this.getName() + " - Test path");
        ArrayList<Location> path = getBestPathToJob(job, storeToVisit);
        //System.out.println(path.size() + " - Test path");

        if(path == null)
            return null;

        //System.out.println(this.getName() + " - Distance");
        float totalDistance = 0;
        for(int i = 0; i < path.size()-1; i++)
        {
            totalDistance += getDistanceBeetwenLocations(
                    path.get(i),
                    path.get(i+1)
            );
        }

        //System.out.println("Total Distance = " + totalDistance);
        float totalTime = timeToTravelDistance(totalDistance);
        return new TimePricePair(Math.round(totalTime), Math.round(totalPrice.price));
    }
}
