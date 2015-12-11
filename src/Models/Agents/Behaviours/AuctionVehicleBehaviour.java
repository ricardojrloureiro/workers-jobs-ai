package Models.Agents.Behaviours;


import Models.Agents.Vehicles.Vehicle;
import Models.Jobs.Job;
import Models.TimePricePair;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuctionVehicleBehaviour extends SimpleBehaviour {
    public boolean done = false;

    public AuctionVehicleBehaviour(Agent a)
    {
        super(a);
    }


    @Override
    public void action() {

        ACLMessage proposal = getAgent().receive(MessageTemplate.MatchReplyWith("reply_auction"));
        if(proposal == null)
            return;

        switch ( proposal.getPerformative() )
        {
            case ACLMessage.PROPOSE:
                try {
                    TimePricePair evaluateAction = ((Vehicle) getAgent()).evaluateAction((Job) proposal.getContentObject());

                    if(evaluateAction != null)
                    {
                        ACLMessage proposeTerms = proposal.createReply();
                        proposeTerms.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        proposeTerms.setContent(Integer.toString(evaluateAction.time));
                        proposeTerms.setReplyWith("reply_auction");
                        getAgent().send(proposeTerms);
                        ((Vehicle) getAgent()).available = true;
                    }else{
                        ACLMessage proposeTerms = proposal.createReply();
                        proposeTerms.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        proposeTerms.setReplyWith("reply_auction");
                        getAgent().send(proposeTerms);
                        ((Vehicle) getAgent()).available = true;
                    }
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                break;

            case ACLMessage.ACCEPT_PROPOSAL:
                try {
                    ((Vehicle) getAgent()).performAction((Job) proposal.getContentObject());
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public boolean done() {
        return done;
    }
}
