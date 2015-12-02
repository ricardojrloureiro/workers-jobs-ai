package Models.Agents.Locations;


import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import javafx.util.Pair;

public class BatteryStation extends PointOfInterest {

    public static final String BATTERYSTATION_TYPE = "battery_station";
    private float chargePerMinute;

    public BatteryStation(int id, String name, float chargePerMinute)
    {
        super(id, name);
        this.chargePerMinute = chargePerMinute;
    }

    public BatteryStation() {
        super();
    }


    public float getChargePerMinute() {
        return chargePerMinute;
    }

    public void setChargePerMinute(float chargePerMinute) {
        this.chargePerMinute = chargePerMinute;
    }


    /**
     * Agent functions
     */

    private class BatteryStationBehaviour extends SimpleBehaviour {
        private int n = 0;

        // construtor do behaviour
        public BatteryStationBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = blockingReceive();
            if (msg.getPerformative() == ACLMessage.INFORM) {
                BatteryStation b = (BatteryStation) this.getAgent();
                System.out.println("SOU A BOMBA " + b.getLocationName() + " " + b.getChargePerMinute() + " " + b.getPosition());
            }

        }

        // m�todo done
        public boolean done() {
            return n==10;
        }

    }


    // m�todo setup
    protected void setup() {

        // obt�m argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            this.setId((Integer) args[0]);
            this.setChargePerMinute((Float) args[1]);
            this.setName((String) args[2]);
            this.setPosition((Pair<Float, Float>) args[3]);
        } else {
        }

        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("BatteryStation");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // cria behaviour
        BatteryStationBehaviour b = new BatteryStationBehaviour(this);
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
