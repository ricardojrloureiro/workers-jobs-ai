package Models.Jobs;

public class AuctionJob extends Job {

    private float currentBid;

    public AuctionJob(int timeToComplete) {
        super(timeToComplete);
    }
}
