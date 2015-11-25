package Models.Agents.Vehicles;

import Models.Jobs.FixedPriceJob;
import Models.Jobs.Job;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;


public class VehicleBehaviour extends ContractNetResponder {


    public VehicleBehaviour(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
        try {
            System.out.println("Agent "+ getAgent().getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContentObject());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        int proposal = ((Vehicle) getAgent()).evaluateAction();
        if (proposal > 2) {
            // We provide a proposal
            System.out.println("Agent "+getAgent().getLocalName()+": Proposing "+proposal);
            ACLMessage propose = cfp.createReply();
            propose.setPerformative(ACLMessage.PROPOSE);
            propose.setContent(String.valueOf(proposal));
            return propose;
        }
        else {
            // We refuse to provide a proposal
            System.out.println("Agent "+getAgent().getLocalName()+": Refuse");
            throw new RefuseException("evaluation-failed");
        }
    }

    protected ACLMessage prepareResultNotification(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
        System.out.println("Agent "+getAgent().getLocalName()+": Proposal accepted");
        if (((Vehicle) getAgent()).performAction()) {
            System.out.println("Agent "+getAgent().getLocalName()+": Action successfully performed");
            ACLMessage inform = accept.createReply();
            inform.setPerformative(ACLMessage.INFORM);
            return inform;
        }
        else {
            System.out.println("Agent "+getAgent().getLocalName()+": Action execution failed");
            throw new FailureException("unexpected-error");
        }
    }

    protected void handleRejectProposal(ACLMessage reject) {
        System.out.println("Agent "+getAgent().getLocalName()+": Proposal rejected");
    }
}
