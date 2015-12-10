package Models.Jobs;

import Models.Agents.Locations.Location;
import Models.Product;

import java.io.Serializable;
import java.util.HashMap;

public class Job implements Serializable {

    private int mTimeToComplete; // time to finish the job
    private int mDeadLine; // time left to select the worker
    private int[] requiredTools;
    private int finalDestinationId;
    private HashMap<Integer, Integer> productsToMake;
    public static float PENALTY = 71;
    private float price;

    private boolean auction = false;

    public Job(float price, int mTimeToComplete, int finalDestinationId, HashMap<Integer, Integer> productsToMake) {
        this.mTimeToComplete = mTimeToComplete;
        this.mDeadLine = 10;
        this.finalDestinationId = finalDestinationId;
        this.productsToMake = productsToMake;
        this.price = price;
    }




    public void setPrice(float price) {
        this.price = price;
    }

    public int getTimeToComplete() {
        return mTimeToComplete;
    }

    public int[] getRequiredTools() {
        return requiredTools;
    }

    public int getDeadline() {
        return mDeadLine;
    }

    public int getFinalDestinationId() {
        return finalDestinationId;
    }

    public HashMap<Integer, Integer>  getProductsToMake() {
        return productsToMake;
    }


    public float getPrice() {
        return price;
    }

    public boolean isAuction() {
        return auction;
    }

    public void setAuction(boolean auction) {
        this.auction = auction;
    }
}
