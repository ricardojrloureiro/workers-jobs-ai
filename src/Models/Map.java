package Models;

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
import java.util.stream.Stream;

public class Map {

    private static Map ref;
    private UndirectedGraph<Location,DistanceEdge> graph;

    private Map() {
        this.graph = new SimpleGraph<>(DistanceEdge.class);
        try {
            parse("src/POI.xml");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Map getMap() {
        if (ref == null)
            // it's ok, we can call this constructor
            ref = new Map();
        return ref;
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

    private void parse (String filepath) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        try {
            Document doc = db.parse(new File(filepath));

            Element rootEle = doc.getDocumentElement();
            parseLocations(rootEle);
            parseConections(rootEle);

            //System.out.println(graph);
            //System.out.println(DijkstraShortestPath.findPathBetween(graph, getLocationFromId(1), getLocationFromId(6)));

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

            System.out.println(distance);

            graph.addEdge(l1, l2, new DistanceEdge(distance));

        }

    }

    private BatteryStation getBatteryStation(Element POI) {
        int id = Integer.parseInt(POI.getAttribute("id"));
        float chargePerMinute = getFloatValue(POI, "charge_per_minute");
        String name = getTextValue(POI, "name");
        float xValue = getFloatValue(POI, "x");
        float yValue = getFloatValue(POI, "y");

        BatteryStation batteryStation = new BatteryStation(id,name,chargePerMinute);
        batteryStation.setPosition(new Pair<>(xValue,yValue));

        return batteryStation;

    }

    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele,tagName));
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

    private void parseLocations(Element root) {
        NodeList pointsOfInterest = root.getElementsByTagName("point_of_interest");

        for(int i = 0; i<pointsOfInterest.getLength(); i++) {
            Element POI = (Element) pointsOfInterest.item(i);
            String type = POI.getAttribute("type");


            switch (type) {
                case BatteryStation.BATTERYSTATION_TYPE:
                    BatteryStation batteryStation = getBatteryStation(POI);
                    graph.addVertex(batteryStation);
                    break;
                case Store.STORE_TYPE:
                    Store store = getStore(POI);
                    graph.addVertex(store);
                   break;
                case Warehouse.WAREHOUSE_TYPE:
                    Warehouse warehouse = getWarehouse(POI);
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
        System.out.println(graph.vertexSet().size());

        for(int i = 0; i<locations.getLength(); i++) {
            Element POI = (Element) locations.item(i);
            int id = Integer.parseInt(POI.getAttribute("id"));
            float xValue = Float.parseFloat(POI.getAttribute("x"));
            float yValue = Float.parseFloat(POI.getAttribute("y"));
            Location location = new Location(id,new Pair<>(xValue,yValue));

            graph.addVertex(location);
        }

        System.out.println(graph.vertexSet().size());


    }

    private Store getStore(Element POI) {

        int id = Integer.parseInt(POI.getAttribute("id"));
        String name = getTextValue(POI, "name");

        float xValue = getFloatValue(POI, "x");
        float yValue = getFloatValue(POI, "y");

        Store store = new Store(id,name);
        store.setPosition(new Pair<>(xValue, yValue));

        return store;
    }

    private Warehouse getWarehouse(Element POI) {

        int id = Integer.parseInt(POI.getAttribute("id"));
        String name = getTextValue(POI, "name");
        float maxWeight = getFloatValue(POI, "max_weight");

        float xValue = getFloatValue(POI, "x");
        float yValue = getFloatValue(POI, "y");

        Warehouse warehouse = new Warehouse(id,name,maxWeight);
        warehouse.setPosition(new Pair<>(xValue,yValue));

        return warehouse;
    }

    private Location getLocationFromId(int id) {
        Stream<Location> matchesId1 =  graph.vertexSet().stream().filter(l -> l.getId() == id);
        return matchesId1.findFirst().get();
    }

    @Override
    public String toString() {
        return this.graph.toString();
    }


}
