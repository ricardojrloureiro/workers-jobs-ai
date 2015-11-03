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

public class Car extends Vehicle {

    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.f1);
        tools.add(Tool.f2);

        return tools;
    }
    /*
    public Car() {
        super(3, 500, 550, Vehicle.STREET, new Pair<>(0.0f, 0.0f), Car.generateToolsArray());
    }
    */
    private class CarBehaviour extends SimpleBehaviour {
        private int n = 0;

        // construtor do behaviour
        public CarBehaviour(Agent a) {
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
        this.setSpeed(3);
        this.setBatteryCharge(500);
        this.setLoadCapacity(550);
        this.setMovementType(Vehicle.STREET);
        this.setCurrentPosition(new Pair<>(0.0f, 0.0f));
        this.setTools(Car.generateToolsArray());


        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Car");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // cria behaviour
        CarBehaviour b = new CarBehaviour(this);
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
