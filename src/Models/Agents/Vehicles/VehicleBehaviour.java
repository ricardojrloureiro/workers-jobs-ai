package Models.Agents.Vehicles;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;


public class VehicleBehaviour extends SimpleBehaviour {
    private int n = 0;

    public VehicleBehaviour(Agent a) {
        super(a);
    }

    public void action() {
        ACLMessage msg = getAgent().blockingReceive();
        if (msg.getPerformative() == ACLMessage.INFORM) {
            System.out.println(getAgent().getName() + " Recebi um inform");
        }

    }

    public boolean done() {
        return n==10;
    }
}
