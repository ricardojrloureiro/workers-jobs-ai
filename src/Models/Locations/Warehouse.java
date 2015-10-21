package Models.Locations;


import Models.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class Warehouse extends PointOfInterest {
    private float maxWeight;
    private ArrayList< HashMap<Product, Integer> > products;

    public Warehouse(int id, String name, float maxWeight)
    {
        super(id, name);
        this.maxWeight = maxWeight;
    }

    public void addProduct(Product product, int quantity)
    {
        HashMap<Product, Integer> hm = new HashMap<>();
        hm.put(product, quantity);
        products.add(hm);
    }

    // TODO: check for needed products
}
