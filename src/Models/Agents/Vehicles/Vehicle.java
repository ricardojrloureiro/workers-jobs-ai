package Models.Agents.Vehicles;

import Models.*;
import Models.Agents.Locations.BatteryStation;
import Models.Agents.Locations.Location;
import Models.Agents.Locations.Store;
import Models.Agents.Locations.Warehouse;
import Models.Agents.Threads.WorkThread;
import Models.Jobs.Job;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class Vehicle extends Agent {

    protected GraphVisualisation graphVisualisation;
    public static int AIR = 1;
    public static int STREET = 2;

    public boolean available = true;
    public boolean working = false;

    public int mMoney = 0;
    private int mSpeed;
    private int mBateryCharge;
    protected int mBateryCapacity;
    private int mLoadCapacity;
    private int mMovementType;
    public Job currentJob = null;

    protected Pair<Float, Float> mCurrentPosition = new Pair<>(0.0f, 0.0f);

    private ArrayList<Integer> mTools = new ArrayList<>();

    protected Map mMap;

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

    protected boolean hasBatteryToArriveLocationAndBatterryStation(Location nextLocation)
    {
        Location currentLocation = this.mMap.getLocationIdFromPosition(this.mCurrentPosition);

        // nearest battery station from job location
        BatteryStation nearestBatteryStationJobLocation = (BatteryStation) this.mMap.getNearestBatteryStation(nextLocation);

        // distance from jog location to battery station
        float distanceJobToBatteryStation = this.mMap.getLocationsDistance(
                nextLocation,
                nearestBatteryStationJobLocation
        );

        // distance from agent to job
        float distanceAgentToJob = this.mMap.getLocationsDistance(
                currentLocation,
                nextLocation
        );

        float distanceJobBS = distanceAgentToJob + distanceJobToBatteryStation;

        if(distanceJobBS < this.getBateryCharge())
        {
            return true;
        }

        return false;
    }

    public ArrayList<Location> getBestPathToJob(Job job , ArrayList<Location> storesToVisit)
    {

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
                float pathLength = mMap.getLocationsDistance(currentLocation, this.mMap.getLocationIdFromPosition(s.getPosition()));
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

    public Location getCurrentLocation()
    {
        return mMap.getLocationIdFromPosition(getCurrentPosition());
    }

    // TODO: analisar preco trabalho/preco loja
    // TODO:

    private HashMap<Location,HashMap<Product, Integer>> askInformationTo(String locationType)
    {

        HashMap<Location,HashMap<Product, Integer>> resultFromQuery = new HashMap<>();

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType(locationType);
        template.addServices(sd1);

        try {
            DFAgentDescription[] result = DFService.search(this, template);

            ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
            for(int i=0; i<result.length; ++i) {
                msg.addReceiver(result[i].getName());
            }

            msg.setContent("products_list");
            send(msg);


            int counter = 0;

            while(counter < result.length)
            {
                ACLMessage reply = blockingReceive(MessageTemplate.MatchInReplyTo("information_agent"));
                if(reply.getPerformative() != ACLMessage.INFORM)
                    continue;

                HashMap<Location,HashMap<Product, Integer>> resultHashMap = (HashMap<Location,HashMap<Product, Integer>>)reply.getContentObject();
                Location resultStore = ((Location)resultHashMap.keySet().toArray()[0]);
                resultFromQuery.put(resultStore , resultHashMap.get(resultStore));
                counter++;
            }

        } catch(FIPAException e) { e.printStackTrace(); } catch (UnreadableException e) {
            e.printStackTrace();
        }

        return resultFromQuery;
    }

    private Location bestLocationsForProduct(Product p, int needed)
    {
        HashMap<Location,HashMap<Product, Integer>> storeInformation = askInformationTo("Store");
        HashMap<Location,HashMap<Product, Integer>> warehouseInformation = askInformationTo("Warehouse");

        HashMap<Location,HashMap<Product, Integer>> locationsInformation = new HashMap<Location,HashMap<Product, Integer>>();

        locationsInformation.putAll(storeInformation);
        locationsInformation.putAll(warehouseInformation);

        Location bestStore = null;
        float bestDistance = -1;

        for (Location store : locationsInformation.keySet()) {
            for (Product prod : locationsInformation.get(store).keySet()) {

                if(prod.getId() == p.getId()) {
                    float prodQuantity = locationsInformation.get(store).get(prod);

                    //System.out.println("Product/Quantity -> " +prod.getId() + "/" + prodQuantity + " ---    Prod Needed/Quantity --> " + p.getId() + "/" + needed);

                    Location currentLocation = mMap.getLocationIdFromPosition(getCurrentPosition());
                    Location storeLocation = mMap.getLocationIdFromPosition(store.getPosition());

                    if (bestDistance == -1 && needed < prodQuantity) {
                        bestDistance = mMap.getLocationsDistance(currentLocation, storeLocation);
                        bestStore = store;
                    } else if (needed < prodQuantity && mMap.getLocationsDistance(currentLocation, storeLocation) < bestDistance) {
                        bestStore = store;
                        bestDistance = mMap.getLocationsDistance(currentLocation, storeLocation);
                    }

                }
            }
        }

        return bestStore;
    }

    private boolean hasToolForProduct(Product p)
    {
        boolean hasTools = true;
        for(Tool toolId : p.getToolsRequiredtoProduce())
        {
            if(!getTools().contains(toolId.type))
            {
                hasTools = false;
            }
        }
        return hasTools;
    }


    public ArrayList<Location> locationsToVisit(Job job, PriceObject totalPrice)
    {
        ArrayList<Location> storeToVisit = new ArrayList<>();

        for(Integer idProduto : job.getProductsToMake().keySet())
        {
            Integer quantity = job.getProductsToMake().get(idProduto);
            Product product = mMap.getProductById(idProduto);
            if(product != null)
            {
                if(!hasToolForProduct(product))
                {
                    Location bestStore = bestLocationsForProduct(product, quantity);
                    if(bestStore == null)
                    {
                        return null;
                    }
                    storeToVisit.add(bestStore);
                    totalPrice.price += product.getPrice() * quantity;
                }
            }
        }

        return storeToVisit;
    }


    protected float getAllJobWeight( Job job)
    {
        float weight = 0.0f;

        for(Integer idProduto : job.getProductsToMake().keySet())
        {
            Integer quantity = job.getProductsToMake().get(idProduto);
            Product product = mMap.getProductById(idProduto);
            if(product != null)
            {
                weight += product.getWeight() * quantity;
            }
        }
        return weight;
    }

    /**
     *
     * @param job that will be analysed
     * @return an integer that represents the value of the current job
     */
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

        ArrayList<Location> path = getBestPathToJob(job, storeToVisit);
        //System.out.println(path.size() + " - Test path");

        if(path == null)
            return null;

        //System.out.println(this.getName() + " - Distance");
        float totalDistance = 0;
        for(int i = 0; i < path.size()-1; i++)
        {
            totalDistance += this.mMap.getLocationsDistance(
                    path.get(i),
                    path.get(i+1)
            );
        }

        //System.out.println("Total Distance = " + totalDistance);
        float totalTime = timeToTravelDistance(totalDistance);
        return new TimePricePair(Math.round(totalTime), Math.round(totalPrice.price));
    }

    /**
     *
     * @param job
     * @return
     */
    public Boolean performAction(Job job) {
        graphVisualisation.showMessage("Job - " + job.getPrice());
        this.currentJob = job;
        new WorkThread(this,job).start();

        return true;
    }

    /**
     *
     * @param finalLocation final location
     * @return true or false, depending if had enough gas to perform the transition
     */
    public boolean moveToLocation(Vehicle v, Location finalLocation) {

        Location currentLocation = this.mMap.getLocationIdFromPosition(this.mCurrentPosition);
        float distance = this.mMap.getLocationsDistance(
                currentLocation,
                finalLocation
        );

        /** Charge vehicle */
        if(currentLocation instanceof BatteryStation)
        {
            try {
                Thread.sleep( Math.round((v.mBateryCapacity-v.mBateryCharge) / ((BatteryStation)currentLocation).getChargePerMinute() ));

                v.mBateryCharge = v.mBateryCapacity;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

        return true;
    }

}
