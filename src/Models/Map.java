package Models;

import Models.Agents.JobContractor;
import Models.Agents.Vehicles.Car;
import jade.wrapper.*;

import Models.Agents.Locations.BatteryStation;
import Models.Agents.Locations.Location;
import Models.Agents.Locations.Store;
import Models.Agents.Locations.Warehouse;
import javafx.util.Pair;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Map {

    private static Map ref;
    private UndirectedGraph<Location,DistanceEdge> graph;
    public ArrayList<Product> products_list;
    private HashMap<Integer,HashMap<Integer,Integer>> poi_products;

    private ArrayList<Pair<Location, Location>> connectionsArray = new ArrayList<>();

    public Product getProductById(int id)
    {
        for(Product p: products_list)
        {
            if(p.getId() == id)
                return p;
        }
        return null;
    }

    public UndirectedGraph<Location,DistanceEdge> getGraph(){ return this.graph; }


    private Map(ContainerController ac) {
        this.graph = new SimpleGraph<>(DistanceEdge.class);
        this.products_list = new ArrayList<>();
        this.poi_products = new HashMap<>();
        try {
            parseProduts("src/Products.xml");
            parse("src/POI.xml",ac);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Map getMap(ContainerController ac) {
        if (ref == null)
            // it's ok, we can call this constructor
            ref = new Map(ac);

        return ref;
    }

    public Location getNearestBatteryStation(Location currentPosition) {

        Set<Location> locations = graph.vertexSet();

        Location toReturn = null;
        float minDistance = 999999999;

        for(Location l : locations) {
            if(l instanceof BatteryStation) {
                float currentDistance = getLocationsDistance(l,currentPosition);
                if(currentDistance < minDistance) {
                    minDistance = currentDistance;
                    toReturn = l;
                }
            }
        }
        return toReturn;
    }

    private static class DistanceEdge extends DefaultEdge {
        private float mDistance;

        public DistanceEdge(float distance) {
            this.mDistance = distance;
        }

        public float getDistance() {
            return mDistance;
        }

    }

    public List<DistanceEdge> shortestPath(int idLoc1, int idLoc2) {
       return DijkstraShortestPath.findPathBetween(graph,getLocationFromId(idLoc1),getLocationFromId(idLoc2));
    }

    private void parseProduts(String filepath) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        try {
            Document doc = db.parse(new File(filepath));

            Element rootEle = doc.getDocumentElement();
            parseProdutsList(rootEle);
            parseProdutsConnections(rootEle);

        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private void parseProdutsConnections(Element rootEle) {
        NodeList points = rootEle.getElementsByTagName("point_of_interest");

        for(int i = 0; i < points.getLength(); i++) {
            Element point = (Element) points.item(i);

            int idPoint = Integer.parseInt(point.getAttribute("id"));

            NodeList connections = point.getElementsByTagName("product");

            HashMap<Integer,Integer> prod = new HashMap<>();

            for(int j = 0; j < connections.getLength(); j++) {
                Element connection = (Element) connections.item(j);

                int idProduct = Integer.parseInt(connection.getAttribute("id"));
                int quantity = Integer.parseInt(connection.getAttribute("quantity"));

                prod.put(idProduct,quantity);

            }

            poi_products.put(idPoint,prod);
        }

    }

    private void parseProdutsList(Element rootEle) {
        NodeList products_node_list = rootEle.getElementsByTagName("products");
        Element products_elem = (Element) products_node_list.item(0);
        NodeList products = products_elem.getElementsByTagName("product");

        for(int i = 0; i < products.getLength(); i++) {
            Element product = (Element) products.item(i);

            String name = getTextValue(product, "name");
            int weight = getIntValue(product, "weight");
            int price = getIntValue(product, "price");
            int id = getIntValue(product, "id");
            int time_to_produce = getIntValue(product,"time_to_produce");


            ArrayList<Tool> toolsArray = new ArrayList<>();
            NodeList tools = product.getElementsByTagName("tool");
            for(int j=0; j < tools.getLength(); j++)
            {
                Element toolEle = (Element) tools.item(j);

                String toolString=toolEle.getTextContent();

                toolsArray.add(new Tool(Tool.fromString(toolString)));
            }

            Product p = new Product(name,weight, toolsArray, price,id);
            p.setTimeRequiredToProduce(time_to_produce);

            products_list.add(p);

        }
    }

    private void parse (String filepath, ContainerController ac) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        try {
            Document doc = db.parse(new File(filepath));

            Element rootEle = doc.getDocumentElement();
            try {
                parseLocations(rootEle, ac);
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
            parseConections(rootEle);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseConections(Element root) {
        NodeList connections = root.getElementsByTagName("connection");

        for(int i = 0; i < connections.getLength(); i++) {
            Element connection = (Element) connections.item(i);

            int id1 = Integer.parseInt(connection.getAttribute("id1"));
            int id2 = Integer.parseInt(connection.getAttribute("id2"));

            Location l1 = getLocationFromId(id1);
            Location l2 = getLocationFromId(id2);

            float distance = (float) Math.sqrt((l1.getPosition().getKey()-l2.getPosition().getKey())*(l1.getPosition().getKey()-l2.getPosition().getKey()) + (l1.getPosition().getValue()-l2.getPosition().getValue())*(l1.getPosition().getValue()-l2.getPosition().getValue()));

            graph.addEdge(l1, l2, new DistanceEdge(distance));

            connectionsArray.add(new Pair<>(l1, l2));
        }

    }

    private BatteryStation getBatteryStation(Element POI, ContainerController ac) {
        int id = Integer.parseInt(POI.getAttribute("id"));
        float chargePerMinute = getFloatValue(POI, "charge_per_minute");
        String name = getTextValue(POI, "name");
        float xValue = getFloatValue(POI, "x");
        float yValue = getFloatValue(POI, "y");


        Object[] args = new Object[4];
        args[0] = id;
        args[1] = chargePerMinute;
        args[2] = name;
        args[3] = new Pair<>(xValue,yValue);

        BatteryStation batteryStation = new BatteryStation(id,name,chargePerMinute);
        batteryStation.setPosition(new Pair<>(xValue,yValue));
        try {
            AgentController aController = ac.createNewAgent(name,BatteryStation.class.getName(),args);

            aController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        return batteryStation;

    }

    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    private float getFloatValue(Element ele, String tagName) {
        return Float.parseFloat(getTextValue(ele, tagName));
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    private void parseLocations(Element root, ContainerController ac) throws StaleProxyException {
        NodeList pointsOfInterest = root.getElementsByTagName("point_of_interest");

        for(int i = 0; i<pointsOfInterest.getLength(); i++) {
            Element POI = (Element) pointsOfInterest.item(i);
            String type = POI.getAttribute("type");

            switch (type) {
                case BatteryStation.BATTERYSTATION_TYPE:
                    BatteryStation batteryStation = getBatteryStation(POI,ac);
                    graph.addVertex(batteryStation);
                    break;
                case Store.STORE_TYPE:
                    Store store = getStore(POI, ac);
                    graph.addVertex(store);
                   break;
                case Warehouse.WAREHOUSE_TYPE:
                    Warehouse warehouse = getWarehouse(POI, ac);
                    graph.addVertex(warehouse);
                    break;
                default:
                    break;

            }
        }
        NodeList pointsNoInterest = root.getElementsByTagName("no_interest_locations");
        
        if (pointsNoInterest.getLength() == 0)
            return;

        Element pointsNoInterestElement = (Element) pointsNoInterest.item(0);
        NodeList locations = pointsNoInterestElement.getElementsByTagName("location");

        for(int i = 0; i<locations.getLength(); i++) {
            Element POI = (Element) locations.item(i);
            int id = Integer.parseInt(POI.getAttribute("id"));
            float xValue = Float.parseFloat(POI.getAttribute("x"));
            float yValue = Float.parseFloat(POI.getAttribute("y"));
            Location location = new Location(id,new Pair<>(xValue,yValue));

            graph.addVertex(location);
        }


    }

    private Store getStore(Element POI, ContainerController ac) {

        int id = Integer.parseInt(POI.getAttribute("id"));
        String name = getTextValue(POI, "name");

        float xValue = getFloatValue(POI, "x");
        float yValue = getFloatValue(POI, "y");

        Store store = new Store(id,name);
        store.setPosition(new Pair<>(xValue, yValue));

        HashMap<Integer,Integer> productsQuantity = poi_products.get(id);
        HashMap<Product,Integer> productsToAdd = new HashMap<>();

        for (java.util.Map.Entry<Integer, Integer> entry : productsQuantity.entrySet()) {
            productsToAdd.put(products_list.get(entry.getKey() - 1), entry.getValue());
        }



        Object[] args = new Object[4];
        args[0] = id;
        args[1] = name;
        args[2] = new Pair<>(xValue,yValue);
        args[3] = productsToAdd;

        try {
            AgentController aController = ac.createNewAgent(name,Store.class.getName(),args);
            aController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        return store;
    }

    private Warehouse getWarehouse(Element POI, ContainerController ac) {

        int id = Integer.parseInt(POI.getAttribute("id"));
        String name = getTextValue(POI, "name");
        float maxWeight = getFloatValue(POI, "max_weight");

        float xValue = getFloatValue(POI, "x");
        float yValue = getFloatValue(POI, "y");

        Warehouse warehouse = new Warehouse(id,name,maxWeight);
        warehouse.setPosition(new Pair<>(xValue,yValue));

        HashMap<Integer,Integer> productsQuantity = poi_products.get(id);
        HashMap<Product,Integer> productsToAdd = new HashMap<>();

        for (java.util.Map.Entry<Integer, Integer> entry : productsQuantity.entrySet()) {
            productsToAdd.put(products_list.get(entry.getKey() - 1),entry.getValue());
        }


        Object[] args = new Object[5];
        args[0] = id;
        args[1] = name;
        args[2] = maxWeight;
        args[3] = new Pair<>(xValue,yValue);
        args[4] = productsToAdd;


        try {
            AgentController aController = ac.createNewAgent(name,Warehouse.class.getName(),args);
            aController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        return warehouse;
    }

    public Location getLocationFromId(int id) {
        Stream<Location> matchesId1 =  graph.vertexSet().stream().filter(l -> l.getId() == id);
        return matchesId1.findFirst().get();
    }

    public Location getLocationIdFromPosition(Pair<Float, Float> position) {
        Stream<Location> matchesId1 =  graph.vertexSet().stream().filter(l -> l.getPosition().equals(position));
        if( matchesId1.count() > 0){
            Stream<Location> matchesId2 =  graph.vertexSet().stream().filter(l -> l.getPosition().equals(position));
            return matchesId2.findFirst().get();
        }
        return null;
    }

    public float getLocationsDistance(Location l1, Location l2) {
        List<DistanceEdge> path = shortestPath(l1.getId(), l2.getId());

        float totalDist = 0;

        for(DistanceEdge e : path) {
            totalDist += e.getDistance();
        }

        return totalDist;
    }

    public float getDistancePassingLocation(Location l1, Location l2, Location l3) {
        float distance1 = getLocationsDistance(l1,l2);
        float distance2 = getLocationsDistance(l2,l3);

        return distance1 + distance2;
    }


    public ArrayList<Pair<Location, Location>> getAllConnections(){
        return this.connectionsArray;
    }

    @Override
    public String toString() {
        return this.graph.toString();
    }

}
