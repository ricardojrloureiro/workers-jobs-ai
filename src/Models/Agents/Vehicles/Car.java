package Models.Agents.Vehicles;

import Models.Tool;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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


    }

    protected void takeDown() {
        // retira registo no DF
        try {
            DFService.deregister(this);
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }
}
