package Models.Locations;

import Models.Locations.Location;
import Models.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class Store extends Location {

    private ArrayList< HashMap<Product, Integer> > products;

    public Store(String name)
    {
        super(name);
    }


    public void addProduct(Product product, int quantity)
    {
        HashMap<Product, Integer> hm = new HashMap<>();
        hm.put(product, quantity);
        products.add(hm);
    }

    // TODO: check for needed products

}
