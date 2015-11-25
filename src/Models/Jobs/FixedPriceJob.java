package Models.Jobs;

public class FixedPriceJob extends Job {

    private float price;

    public FixedPriceJob(int price, int[] requiredTools, int timeToComplete, int finalDestinationId, int[] productsToMake) {
        super(timeToComplete,requiredTools,finalDestinationId,productsToMake);
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
}
