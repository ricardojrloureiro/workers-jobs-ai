package Models.Agents.Vehicles;

import Models.Agents.Behaviours.AuctionVehicleBehaviour;
import Models.Agents.Behaviours.VehicleBehaviour;
import Models.GraphVisualisation;
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

public class Motorcycle extends Vehicle {

    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.tool1);
        tools.add(Tool.f3);

        return tools;
    }

    protected void setup() {

        Object[] args = getArguments();
        String xCoord = args[0].toString();
        String yCoord = args[1].toString();


        this.setMap(getContainerController());
        this.setSpeed(4);
        this.setBatteryCharge(350);
        this.setLoadCapacity(300);
        this.setMovementType(Vehicle.STREET);
        this.setCurrentPosition(new Pair<>(Float.parseFloat(xCoord), Float.parseFloat(yCoord)));
        this.setTools(Motorcycle.generateToolsArray());
        this.setmBateryCapacity(350);
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
        AuctionVehicleBehaviour auctionB = new AuctionVehicleBehaviour(this);
        addBehaviour(b);
        addBehaviour(auctionB);


        graphVisualisation = new GraphVisualisation(this.getName(), this.getmMap(), this);
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
