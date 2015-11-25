package Models.Jobs;

public class FixedPriceJob extends Job {

    private float price;

    public FixedPriceJob(int price, int[] requiredTools, int timeToComplete) {
        super(timeToComplete,requiredTools);
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
}
