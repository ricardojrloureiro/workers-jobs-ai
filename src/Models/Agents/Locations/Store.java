package Models.Agents.Locations;

import Models.Product;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class Store extends PointOfInterest {
    public static final String STORE_TYPE = "store";


    private HashMap<Product,Integer> products = new HashMap<>();

    public Store() {
        super();
    }

    public Store(int id, String name)
    {
        super(id, name);
    }


    public void addProduct(Product product, int quantity)
    {
        products.put(product,quantity);
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    // TODO: check for needed products


    /**
     * Agent functions
     */

    private class StoreBehaviour extends SimpleBehaviour {
        private int n = 0;

        // construtor do behaviour
        public StoreBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = blockingReceive();
            if (msg.getPerformative() == ACLMessage.INFORM) {
                Store s = (Store) this.getAgent();
                System.out.println("SOU A Store " + s.getPOIName() + " " + s.getPosition());

                for (java.util.Map.Entry<Product, Integer> entry : s.getProducts().entrySet()) {
                    System.out.println("Produto: " + entry.getKey().getName());
                    System.out.println("Quantidade: " + entry.getValue());
                }

            }

        }

        // método done
        public boolean done() {
            return n==10;
        }

    }


    // método setup
    protected void setup() {

        // obtém argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            this.setId((Integer) args[0]);
            this.setName((String) args[1]);
            this.setPosition((Pair<Float, Float>) args[2]);

            HashMap<Product,Integer> productsToAdd = (HashMap<Product,Integer>) args[3];

            if(productsToAdd.size() != 0) {
                for (java.util.Map.Entry<Product, Integer> entry : productsToAdd.entrySet()) {
                    this.addProduct(entry.getKey(), entry.getValue());
                }
            }


        } else {
            System.out.println("Não especificou o tipo");
        }

        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Store");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // cria behaviour
        StoreBehaviour b = new StoreBehaviour(this);
        addBehaviour(b);

    }

    // método takeDown
    protected void takeDown() {
        // retira registo no DF
        try {
            DFService.deregister(this);
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

}
