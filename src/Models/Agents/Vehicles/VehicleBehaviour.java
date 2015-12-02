package Models.Agents.Vehicles;

import Models.Jobs.Job;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import jade.proto.SSContractNetResponder;

import java.util.Date;


public class VehicleBehaviour extends ContractNetResponder {


    public VehicleBehaviour(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
        try {
            System.out.println("Agent "+ getAgent().getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContentObject());

            int proposal = ((Vehicle) getAgent()).evaluateAction((Job) cfp.getContentObject());
            if (proposal > 0) {
                // We provide a proposal
                System.out.println("Agent "+getAgent().getLocalName()+": Proposing "+proposal + " to "+ cfp.getContentObject());
                ACLMessage propose = cfp.createReply();
                propose.setPerformative(ACLMessage.PROPOSE);
                propose.setContent(String.valueOf(proposal));
                return propose;
            }
            else {
                // We refuse to provide a proposal
                System.out.println("Agent "+getAgent().getLocalName()+": Refuse");
                ((Vehicle) getAgent()).available = true;
                throw new RefuseException("evaluation-failed");
            }

        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
        System.out.println("Agent "+getAgent().getLocalName()+": Proposal accepted");
        try {
            if (((Vehicle) getAgent()).performAction((Job) cfp.getContentObject())) {
                System.out.println("Agent "+getAgent().getLocalName()+": Action successfully performed");
                ACLMessage inform = accept.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                return inform;
            }
            else {
                System.out.println("Agent "+getAgent().getLocalName()+": Action execution failed");
                throw new FailureException("unexpected-error");
            }
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println("Agent "+getAgent().getLocalName()+": Proposal rejected");
        //((Vehicle) getAgent()).available = true;
    }
}
