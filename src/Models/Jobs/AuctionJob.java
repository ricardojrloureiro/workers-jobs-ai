package Models.Jobs;

public class AuctionJob extends Job {

    private float currentBid;

    public AuctionJob(int timeToComplete, int[] requiredTools) {
        super(timeToComplete, requiredTools);
    }
}
