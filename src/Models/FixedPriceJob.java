package Models;

public class FixedPriceJob extends Job{

    private float price;

    public FixedPriceJob(int timeToComplete) {
        super(timeToComplete);
    }
}
