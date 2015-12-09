package Models.Agents.Behaviours;


import Models.Agents.Locations.Warehouse;
import Models.Jobs.Job;
import Models.Product;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AuctionJobBehaviour extends SimpleBehaviour{

    Job j ;
    public boolean done = false;
    public AuctionJobBehaviour(Agent a, Job job)
    {
        super(a);
        this.j = job;
    }

    private ArrayList<AID> getAgents(Agent agent)
    {
        ArrayList<AID> availableAgentsList = new ArrayList<>();
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sdVehicles = new ServiceDescription();
        sdVehicles.setType("Vehicle");
        template.addServices(sdVehicles);
        DFAgentDescription[] results = new DFAgentDescription[0];
        try {
            results = DFService.search(agent, template);

            for (DFAgentDescription result : results) {
                availableAgentsList.add(result.getName());
            }

        } catch (FIPAException e) {
            e.printStackTrace();
        }

        return availableAgentsList;
    }

    public ArrayList<AID> handleResponse(ArrayList<AID> agents)
    {
        int numResponses = 0;
        ArrayList<AID> accepts = new ArrayList<>();

        while(numResponses < agents.size())
        {
            System.out.println("a espera de resposta");
            ACLMessage reply = getAgent().blockingReceive(MessageTemplate.MatchReplyWith("reply_auction"), 1000);

            if(reply == null)
                return accepts;
            System.out.println("recebeu resposta " + reply.getPerformative());

            if(reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
            {
                accepts.add(reply.getSender());
            }

            if(accepts.size() > 1)
            {
                j.setPrice(j.getPrice() - 10);
            }
            numResponses++;

        }
        return accepts;
    }

    private void sendMessage(ArrayList<AID> agents)
    {
        for (AID id : agents)
        {
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(id);
            msg.setReplyWith("reply_auction");
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
            // We want to receive a reply depending in the job deadline
            msg.setReplyByDate(new Date(System.currentTimeMillis() + 2000));
            try {
                msg.setContentObject(j);
                getAgent().send(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void acceptProposer(AID aid)
    {
        ACLMessage acceptMessage = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        acceptMessage.setInReplyTo("reply_auction");
        try {
            acceptMessage.setContentObject(this.j);
        } catch (IOException e) {
            e.printStackTrace();
        }
        acceptMessage.addReceiver(aid);
    }

    @Override
    public void action() {
        ArrayList<AID> agents = getAgents(getAgent());

        ArrayList<AID> accepts = agents;

        while(accepts.size() > 1)
        {
            sendMessage(accepts);
            accepts = handleResponse(accepts);
        }

        if( accepts.size() == 0)
        {
            j.setPrice(j.getPrice()+10);

            sendMessage(agents);

            accepts = handleResponse(agents);
        }

        acceptProposer(accepts.get(0));

        done = true;
    }

    @Override
    public boolean done() {
        return done;
    }
}
