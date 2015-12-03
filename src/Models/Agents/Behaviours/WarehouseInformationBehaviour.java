package Models.Agents.Behaviours;


import Models.Agents.Locations.Store;
import Models.Agents.Locations.Warehouse;
import Models.Product;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.HashMap;

public class WarehouseInformationBehaviour extends SimpleBehaviour{

    public WarehouseInformationBehaviour(Agent a)
    {
        super(a);
    }


    @Override
    public void action() {
        ACLMessage msg = getAgent().blockingReceive();
        if (msg.getPerformative() == ACLMessage.QUERY_IF) {
            ACLMessage reply = msg.createReply();

            reply.setPerformative(ACLMessage.INFORM);
            if (msg.getContent().equals("products_list"))
            {
                try {

                    HashMap<Warehouse,HashMap<Product, Integer>> sim = new HashMap<>();
                    sim.put(((Warehouse)getAgent()), ((Warehouse)getAgent()).getProducts());
                    reply.setContentObject( sim );
                    reply.setInReplyTo("information_agent");
                    getAgent().send(reply);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
