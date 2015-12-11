package Models.Agents.Threads;

import Models.Agents.Locations.Location;
import Models.Agents.Vehicles.Vehicle;
import Models.GraphVisualisation;
import Models.Jobs.Job;
import Models.Map;
import Models.PriceObject;

import java.util.ArrayList;
import java.util.Arrays;

public class WorkThread extends Thread {

    private Vehicle vehicle;
    private Job job;

    public WorkThread(Vehicle vehicle,Job job) {
        this.job = job;
        this.vehicle = vehicle;
    }

    public void run() {
        vehicle.available = false;
        vehicle.working = true;
        System.out.println("STARTING JOB --- " + job.getFinalDestinationId() + " --- BY : " + vehicle.getLocalName());

        PriceObject price = new PriceObject();

        ArrayList<Location> storesToVisit = vehicle.locationsToVisit(job, price);
        ArrayList<Location> path = vehicle.getBestPathToJob(job, storesToVisit);

        System.out.println(path.size());


        for(Location loc : path)
        {
            vehicle.moveToLocation(vehicle, loc);
        }

        System.out.println("ENDING JOB --- " + job.getFinalDestinationId() + " --- BY : " + vehicle.getLocalName());

        vehicle.mMoney += (job.getPrice()-price.price);
        vehicle.available = true;
        vehicle.working = false;
    }

}
