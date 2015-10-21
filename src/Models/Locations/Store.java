package Models.Locations;

import Models.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class Store extends PointOfInterest {

    private ArrayList< HashMap<Product, Integer> > products;

    public Store(int id, String name)
    {
        super(id, name);
    }


    public void addProduct(Product product, int quantity)
    {
        HashMap<Product, Integer> hm = new HashMap<>();
        hm.put(product, quantity);
        products.add(hm);
    }

    // TODO: check for needed products

}
