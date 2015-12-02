package Models.Agents.Threads;

import Models.Agents.Locations.Location;
import Models.Agents.Vehicles.Vehicle;
import Models.Jobs.Job;

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
        System.out.println("STARTING JOB --- " + job.getFinalDestinationId() + " --- BY : " + vehicle.getLocalName());

        ArrayList<Location> path = vehicle.getBestPathToJob(job);

        for(Location loc : path)
        {
            vehicle.moveToLocation(loc);
        }

        System.out.println("ENDING JOB --- " + job.getFinalDestinationId() + " --- BY : " + vehicle.getLocalName());

        vehicle.available = true;
    }

}
