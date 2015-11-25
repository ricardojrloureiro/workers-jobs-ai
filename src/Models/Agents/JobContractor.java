package Models.Agents;

import Models.Jobs.FixedPriceJob;
import Models.Jobs.Job;
import Models.Tool;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;


public class JobContractor extends Agent {

    private ArrayList<Job> mJobList = new ArrayList<>();

    // Default constructor
    public JobContractor() {
        mJobList.add(new FixedPriceJob(10,new int[]{Tool.f1,Tool.f2},100,8,new int[]{4}));
        //mJobList.add(new FixedPriceJob(5,new int[]{1,2},100));
        //mJobList.add(new FixedPriceJob(20,new int[]{1,2},100));
    }

    private class JobContractorBehaviour extends ContractNetInitiator {

        public JobContractorBehaviour(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected void handlePropose(ACLMessage propose, Vector v) {
            System.out.println("Agent "+propose.getSender().getName()+" proposed "+propose.getContent());
        }

        protected void handleRefuse(ACLMessage refuse) {
            System.out.println("Agent "+refuse.getSender().getName()+" refused");
        }

        protected void handleFailure(ACLMessage failure) {
            if (failure.getSender().equals(myAgent.getAMS())) {
                // FAILURE notification from the JADE runtime: the receiver
                // does not exist
                System.out.println("Responder does not exist");
            }
            else {
                System.out.println("Agent "+failure.getSender().getName()+" failed");
            }
            // Immediate failure --> we will not receive a response from this agent

        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {
            // Evaluate proposals.
            int bestProposal = -1;
            AID bestProposer = null;
            ACLMessage accept = null;
            Enumeration e = responses.elements();
            while (e.hasMoreElements()) {
                ACLMessage msg = (ACLMessage) e.nextElement();
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.addElement(reply);
                    int proposal = Integer.parseInt(msg.getContent());
                    if (proposal > bestProposal) {
                        bestProposal = proposal;
                        bestProposer = msg.getSender();
                        accept = reply;
                    }
                }
            }
            // Accept the proposal of the best proposer
            if (accept != null) {
                System.out.println("Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            }
        }

        protected void handleInform(ACLMessage inform) {
            System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action");
        }


    }

    protected void setup() {
        // regista agente no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getName());
        sd.setType("Contractor");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }


        // cria behaviour
        for(Job j: mJobList) {

            try {

                // Fill the CFP message
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);

                //Search template - searches for all vehicles
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sdVehicles = new ServiceDescription();
                sdVehicles.setType("Vehicle");
                template.addServices(sdVehicles);
                DFAgentDescription[] results = DFService.search(this, template);

                for (DFAgentDescription result : results){
                    msg.addReceiver(result.getName());
                }

                msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                // We want to receive a reply in 10 secs
                msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
                msg.setContentObject(j);

                JobContractorBehaviour b = new JobContractorBehaviour(this, msg);
                addBehaviour(b);

            } catch(FIPAException | IOException e) {
                e.printStackTrace();
            }


        }



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
