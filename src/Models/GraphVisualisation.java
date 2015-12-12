package Models;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import Models.Agents.JobContractor;
import Models.Agents.Locations.*;
import Models.Agents.Vehicles.*;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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
                            g.drawString("BS - " + l.getId(), Math.round(j)* MUL, Math.round(i)* MUL);
                        }
                        else if(l instanceof Warehouse)
                        {
                            g.setColor(Color.BLUE);
                            g.drawString("WH - " + l.getId(), Math.round(j)* MUL, Math.round(i)* MUL);
                        }else if(l instanceof Store)
                        {
                            g.setColor(Color.RED);
                            g.drawString("ST - " + l.getId(), Math.round(j)* MUL, Math.round(i)* MUL);
                        }
                        else{
                            g.setColor(Color.LIGHT_GRAY);
                            g.drawString("PI - " + l.getId(), Math.round(j)* MUL, Math.round(i)* MUL);
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


            g.setFont(new Font("TimesRoman", Font.PLAIN, 12));
            g.setColor(Color.BLACK);
            g.drawString("Bat: " + vehicle.getBateryCharge(),
                    90 *  MUL,
                    50 *  MUL);
            g.drawString("Work: " + vehicle.working,
                    90 *  MUL,
                    51 *  MUL);
            g.drawString("Avai: " + vehicle.available,
                    90 *  MUL,
                    52 *  MUL);
            g.drawString("Cash: " + vehicle.mMoney + "$",
                    90 *  MUL,
                    53 *  MUL);
            if(vehicle.currentJob != null) {
                g.drawString("Job: " + vehicle.currentJob.getPrice() + "$",
                        90 * MUL,
                        54 * MUL);
                g.drawString("Destination: " + vehicle.currentJob.getFinalDestinationId(),
                        90 *  MUL,
                        55 *  MUL);
                g.drawString("Auction: " + vehicle.currentJob.isAuction(),
                        90 *  MUL,
                        56 *  MUL);
            }else{
                g.drawString("Job: ----",
                        90 * MUL,
                        54 * MUL);
            }




            if(msg != null)
            {
                g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                g.setColor(Color.BLACK);

                g.drawString(msg, Math.round(vehicle.getCurrentLocation().getPosition().getKey()) *  MUL, Math.round(vehicle.getCurrentLocation().getPosition().getValue()) *  MUL);

            }
            msg=null;
        }

        public void actionPerformed(ActionEvent ev){
            if(ev.getSource()==timer){
                repaint();// this will call at every 1 second
            }

        }
    }


    public static void parse(Element root, ContainerController ac)
    {
        NodeList vehicles = root.getElementsByTagName("vehicle");

        for(int i = 0; i<vehicles.getLength(); i++) {
            Element v = (Element) vehicles.item(i);
            String type = v.getAttribute("type");

            Object arguments[] = new Object[2];
            arguments[0] = v.getAttribute("x");
            arguments[1] = v.getAttribute("y");

            switch (type) {
                case "drone":
                    try {
                        AgentController agC = ac.createNewAgent(v.getAttribute("name"), Drone.class.getName(), arguments);
                        agC.start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                    break;
                case "car":
                    try {
                        AgentController agC = ac.createNewAgent(v.getAttribute("name"), Car.class.getName(), arguments);
                        agC.start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                    break;
                case "motorcycle":
                    try {
                        AgentController agC = ac.createNewAgent(v.getAttribute("name"), Motorcycle.class.getName(), arguments);
                        agC.start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                    break;
                case "truck":
                    try {
                        AgentController agC = ac.createNewAgent(v.getAttribute("name"), Truck.class.getName(), arguments);
                        agC.start();
                    } catch (StaleProxyException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void createAgents (String filepath, ContainerController ac) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        try {
            Document doc = db.parse(new File(filepath));

            Element rootEle = doc.getDocumentElement();
            parse(rootEle, ac);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            createAgents("src/Vehicles.xml", mainContainer);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
/*
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
*/

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