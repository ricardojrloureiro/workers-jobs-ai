package Models.Agents.Locations;


import Models.Agents.Behaviours.WarehouseInformationBehaviour;
import Models.Product;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Warehouse extends PointOfInterest  implements Serializable {

    public static final String WAREHOUSE_TYPE = "warehouse";

    private float maxWeight;
    private HashMap<Product,Integer> products = new HashMap<>();

    public Warehouse() {
        super();
    }

    public Warehouse(int id, String name, float maxWeight)
    {
        super(id, name);
        this.maxWeight = maxWeight;
    }

    public void addProduct(Product product, int quantity)
    {
        products.put(product, quantity);
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(float maxWeight) {
        this.maxWeight = maxWeight;
    }

    // TODO: check for needed products


    // m�todo setup
    protected void setup() {

        // obt�m argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            this.setId((Integer) args[0]);
            this.setName((String) args[1]);
            this.setMaxWeight((Float) args[2]);
            this.setPosition((Pair<Float, Float>) args[3]);

            HashMap<Product,Integer> productsToAdd = (HashMap<Product,Integer>) args[4];

            if(productsToAdd.size() != 0) {
                for (java.util.Map.Entry<Product, Integer> entry : productsToAdd.entrySet()) {
                    this.addProduct(entry.getKey(), entry.getValue());
                }
            }

        } else {
        }

        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Warehouse");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // cria behaviour
        WarehouseInformationBehaviour b = new WarehouseInformationBehaviour(this);
        addBehaviour(b);

    }

    // m�todo takeDown
    protected void takeDown() {
        // retira registo no DF
        try {
            DFService.deregister(this);
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }
}
