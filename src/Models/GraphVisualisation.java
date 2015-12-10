package Models;

import javax.swing.*;

import Models.Agents.JobContractor;
import Models.Agents.Locations.*;
import Models.Agents.Vehicles.Car;
import Models.Agents.Vehicles.Drone;
import Models.Agents.Vehicles.Vehicle;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.util.Pair;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphVisualisation extends JFrame
{
    public static final int MUL = 10;

    private DrawPane panel;
    public GraphVisualisation(String agentName, Map map, Vehicle vehicle){
        super(agentName);
        panel = new DrawPane(map, vehicle);
        setContentPane(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(100*MUL, 100*MUL);

        setVisible(true);
    }

    public void closeWindow()
    {
        setVisible(false); //you can't see me!
        dispose(); //Destroy the JFrame object
    }

    public void showMessage(String message)
    {
        panel.msg = message;
        repaint();
        panel.timer.restart();
    }

    //create a component that you can actually draw on.
    class DrawPane extends JPanel implements ActionListener {
        private Map m;
        Vehicle vehicle;
        Timer timer=new Timer(500, this);
        public String msg = null;

        public DrawPane(Map map, Vehicle vehicle)
        {
            this.m = map;this.vehicle = vehicle;
            timer.start();
        }

        public void paintComponent(Graphics g){
            for(float i = 0; i < 100; i++)
            {
                for(float j = 0; j < 100; j++)
                {
                    Location l = m.getLocationIdFromPosition(new Pair<Float, Float>(j, i));
                    if(l != null)
                    {
                        if(l instanceof BatteryStation)
                        {
                            g.setColor(Color.GREEN);
                            g.drawString("BS", Math.round(j)* MUL, Math.round(i)* MUL);
                        }
                        else if(l instanceof Warehouse)
                        {
                            g.setColor(Color.BLUE);
                            g.drawString("WH", Math.round(j)* MUL, Math.round(i)* MUL);
                        }else if(l instanceof Store)
                        {
                            g.setColor(Color.RED);
                            g.drawString("ST", Math.round(j)* MUL, Math.round(i)* MUL);
                        }
                        else{
                            g.setColor(Color.LIGHT_GRAY);
                            g.drawString("PI", Math.round(j)* MUL, Math.round(i)* MUL);
                        }
                    }
                }
            }
            g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g.setColor(Color.BLACK);

            g.drawString("V", Math.round(vehicle.getCurrentLocation().getPosition().getKey()) *  MUL, Math.round(vehicle.getCurrentLocation().getPosition().getValue()) *  MUL);


            for(int k = 0; k < m.getAllConnections().size(); k++)
            {
                Pair<Location, Location> connection = m.getAllConnections().get(k);

                Pair<Float, Float> coord1 = connection.getKey().getPosition();
                Pair<Float, Float> coord2 = connection.getValue().getPosition();
                g.drawLine(Math.round(coord1.getKey()) *  MUL, Math.round(coord1.getValue()) *  MUL,
                        Math.round(coord2.getKey()) *  MUL, Math.round(coord2.getValue())  *  MUL);
            }

            if(msg != null)
            {
                g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                g.setColor(Color.BLACK);

                g.drawString("New Job Started", Math.round(vehicle.getCurrentLocation().getPosition().getKey()) *  MUL, Math.round(vehicle.getCurrentLocation().getPosition().getValue()) *  MUL);
            }
            msg=null;
        }

        public void actionPerformed(ActionEvent ev){
            if(ev.getSource()==timer){
                repaint();// this will call at every 1 second
            }

        }
    }


    public static void main(String[] args)
    {

        Runtime rt = Runtime.instance();
// create a default profile
        Profile p = new ProfileImpl();
// create the Main-container
        ContainerController mainContainer = rt.createMainContainer(p);


        try {
            AgentController ac = mainContainer.createNewAgent("drone", Drone.class.getName(), null);
            ac.start();
        } catch (jade.wrapper.StaleProxyException e) {
            System.err.println("Error launching agent...");
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            AgentController aController = mainContainer.createNewAgent("Carro fixe",Car.class.getName(),null);
            aController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            AgentController aController = mainContainer.createNewAgent("Contractor",JobContractor.class.getName(),null);
            aController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }



    }



}