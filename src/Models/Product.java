package Models;


public class Product{
    private float weight;
    private String name;

    /**
     * Time to produce the product in milliseconds
     */
    private float timeRequiredToProduce;



    public Product(String name, float weight)
    {
        this.name = name;
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTimeRequiredToProduce() {
        return timeRequiredToProduce;
    }

    public void setTimeRequiredToProduce(float timeRequiredToProduce) {
        this.timeRequiredToProduce = timeRequiredToProduce;
    }
}
