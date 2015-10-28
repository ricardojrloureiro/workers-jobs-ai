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

public class Drone extends Vehicle {


    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.f1);

        return tools;
    }

    public Drone() {
        super(5, 250, 100, Vehicle.AIR, new Pair<>(0.0f, 0.0f), Drone.generateToolsArray());
    }


    private class PingPongBehaviour extends SimpleBehaviour {
        private int n = 0;

        // construtor do behaviour
        public PingPongBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = blockingReceive();
            if (msg.getPerformative() == ACLMessage.INFORM) {
                System.out.println(++n + " " + getLocalName() + ": recebi " + msg.getContent());
                // cria resposta
                ACLMessage reply = msg.createReply();
                // preenche conteúdo da mensagem
                if (msg.getContent().equals("ping"))
                    reply.setContent("pong");
                else reply.setContent("ping");
                // envia mensagem
                send(reply);
            }

        }

        // método done
        public boolean done() {
            return n==10;
        }

    }


    // método setup
    protected void setup() {
        String tipo = "";
        // obtém argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            tipo = (String) args[0];
        } else {
            System.out.println("Não especificou o tipo");
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
