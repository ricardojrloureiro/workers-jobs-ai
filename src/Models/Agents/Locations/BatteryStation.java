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

    private class PingPongBehaviour extends SimpleBehaviour {
        private int n = 0;

        // construtor do behaviour
        public PingPongBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            System.out.println("AQUI");
            ACLMessage msg = blockingReceive();
            if (msg.getPerformative() == ACLMessage.INFORM) {
                BatteryStation b = (BatteryStation) this.getAgent();
                System.out.println("YO SOU A BOMBA " + b.getPOIName() + " " + b.getChargePerMinute());

                /*
                // cria resposta
                ACLMessage reply = msg.createReply();
                // preenche conte�do da mensagem
                if (msg.getContent().equals("ping"))
                    reply.setContent("pong");
                else reply.setContent("ping");
                // envia mensagem
                send(reply);
                */
            }

        }

        // m�todo done
        public boolean done() {
            return n==10;
        }

    }


    // m�todo setup
    protected void setup() {
        System.out.println("A CRIAR BATTERY STATION");

        String tipo = "";
        // obt�m argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            this.setId((Integer) args[0]);
            this.setChargePerMinute((Float) args[1]);
            this.setName((String) args[2]);
            this.setPosition((Pair<Float, Float>) args[3]);
        } else {
            System.out.println("N�o especificou o tipo");
        }

        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Agente " + tipo);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // cria behaviour
        PingPongBehaviour b = new PingPongBehaviour(this);
        addBehaviour(b);
        /*
        // toma a iniciativa se for agente "pong"
        if(tipo.equals("pong")) {
            // pesquisa DF por agentes "ping"
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd1 = new ServiceDescription();
            sd1.setType("Agente ping");
            template.addServices(sd1);
            try {
                DFAgentDescription[] result = DFService.search(this, template);
                // envia mensagem "pong" inicial a todos os agentes "ping"
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                for(int i=0; i<result.length; ++i)
                    msg.addReceiver(result[i].getName());
                msg.setContent("pong");
                send(msg);
            } catch(FIPAException e) { e.printStackTrace(); }
        }
        */

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
