package Models;


import java.util.ArrayList;

public class Product{
    private float weight;
    private String name;
    private ArrayList<Tool> toolsRequiredtoProduce;

    /**
     * Time to produce the product in milliseconds
     */
    private float timeRequiredToProduce;



    public Product(String name, float weight, ArrayList<Tool>  tools)
    {
        this.name = name;
        this.weight = weight;
        this.toolsRequiredtoProduce = tools;
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

    public ArrayList<Tool> getToolsRequiredtoProduce() {
        return toolsRequiredtoProduce;
    }

    public void setToolsRequiredtoProduce(ArrayList<Tool> toolsRequiredtoProduce) {
        this.toolsRequiredtoProduce = toolsRequiredtoProduce;
    }
}
