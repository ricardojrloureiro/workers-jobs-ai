package Models.Agents.Vehicles;

import Models.Tool;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;

import java.util.ArrayList;

public class Motorcycle extends Vehicle {

    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.tool1);
        tools.add(Tool.f3);

        return tools;
    }
/*
    public Motorcycle() {
        super(4, 350, 300, Vehicle.STREET,new Pair<>(0.0f, 0.0f), Motorcycle.generateToolsArray());
    }
    */

    private class MotorcycleBehaviour extends SimpleBehaviour {
        private int n = 0;

        // construtor do behaviour
        public MotorcycleBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = blockingReceive();
            if (msg.getPerformative() == ACLMessage.INFORM) {

            }

        }

        // método done
        public boolean done() {
            return n==10;
        }

    }


    // método setup
    protected void setup() {
        this.setMap(getContainerController());
        this.setSpeed(4);
        this.setBatteryCharge(350);
        this.setLoadCapacity(300);
        this.setMovementType(Vehicle.STREET);
        this.setCurrentPosition(new Pair<>(0.0f, 0.0f));
        this.setTools(Motorcycle.generateToolsArray());


        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Motorcycle");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // cria behaviour
        MotorcycleBehaviour b = new MotorcycleBehaviour(this);
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
