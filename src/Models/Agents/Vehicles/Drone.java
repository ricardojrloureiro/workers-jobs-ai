package Models.Agents.Vehicles;

import Models.Agents.JobContractor;
import Models.Tool;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.util.Pair;

import java.util.ArrayList;

public class Drone extends Vehicle {

    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.f1);

        return tools;
    }

    //public Drone() {
    //    super(5, 250, 100, Vehicle.AIR, new Pair<>(0.0f, 0.0f), Drone.generateToolsArray());
    //}

    private class DroneBehaviour extends SimpleBehaviour {
        private int n = 0;

        // construtor do behaviour
        public DroneBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = blockingReceive();
            if (msg.getPerformative() == ACLMessage.INFORM) {
                //Drone d = (Drone) this.getAgent();
                //System.out.println(d.getmMap().shortestPath(1,3));
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
        this.setSpeed(5);
        this.setBatteryCharge(250);
        this.setLoadCapacity(100);
        this.setMovementType(Vehicle.AIR);
        this.setCurrentPosition(new Pair<>(-159f, 36f));
        this.setTools(Drone.generateToolsArray());
        this.setmBateryCapacity(250);
        this.setmMoney(500);

        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Vehicle");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // cria behaviour

        MessageTemplate template = MessageTemplate.and(
        MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
        MessageTemplate.MatchPerformative(ACLMessage.CFP) );
        VehicleBehaviour b = new VehicleBehaviour(this, template);
        addBehaviour(b);

        try {
            AgentController aController = getContainerController().createNewAgent("Carro fixe",Car.class.getName(),null);
            aController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        try {
            AgentController aController = getContainerController().createNewAgent("Contractor",JobContractor.class.getName(),null);
            aController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

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
