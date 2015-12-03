package Models;


import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable{
    private float weight;
    private String name;
    private int id;
    private int price;
    private ArrayList<Tool> toolsRequiredtoProduce;

    /**
     * Time to produce the product in milliseconds
     */
    private float timeRequiredToProduce;



    public Product(String name, float weight, ArrayList<Tool>  tools, int price, int id)
    {
        this.name = name;
        this.weight = weight;
        this.toolsRequiredtoProduce = tools;
        this.price = price;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }
}
