package Models.Agents;

import Models.Jobs.FixedPriceJob;
import Models.Jobs.Job;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.ArrayList;


public class JobContractor extends Agent {

    private ArrayList<Job> mJobList = new ArrayList<>();

    // Default constructor
    public JobContractor() {
        mJobList.add(new FixedPriceJob(10,new int[]{1,2},100));
        mJobList.add(new FixedPriceJob(5,new int[]{1,2},100));
        mJobList.add(new FixedPriceJob(20,new int[]{1,2},100));
    }

    private class JobContractorBehaviour extends SimpleBehaviour {
        private int n = 0;

        public JobContractorBehaviour(jade.core.Agent a) {
            super(a);
        }

        public void action() {

            //mandar propostas para os trabalhos todos

            //Search template - searches for all vehicles
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sdVehicles = new ServiceDescription();
            sdVehicles.setType("Vehicle");
            template.addServices(sdVehicles);

            try {
                DFAgentDescription[] results = DFService.search(this.getAgent(), template);

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                for (DFAgentDescription result : results){
                    msg.addReceiver(result.getName());
                }

                msg.setContentObject(mJobList.get(0));
                send(msg);

            } catch(FIPAException | IOException e) {
                e.printStackTrace();
            }

            mJobList.remove(0);

        }

        public boolean done() {
            return mJobList.isEmpty();
        }

    }

    protected void setup() {

        //ler jobs

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
        JobContractorBehaviour b = new JobContractorBehaviour(this);
        addBehaviour(b);


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
