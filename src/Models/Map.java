package Models;

import Models.Agents.Locations.BatteryStation;
import Models.Agents.Locations.Location;
import Models.Agents.Locations.Store;
import Models.Agents.Locations.Warehouse;
import javafx.util.Pair;
import org.jgrapht.UndirectedGraph;
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

public class Map {

    private UndirectedGraph<Location,DistanceEdge> map;

    public Map() {
        this.map = new SimpleGraph<>(DistanceEdge.class);
    }

    public static void main(String[] args) {
        Map map = new Map();
        try {
            map.parse("src/POI.xml");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
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

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BatteryStation getBatteryStation(Element POI)
    {
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
                    map.addVertex(batteryStation);
                    break;
                case Store.STORE_TYPE:
                    Store store = getStore(POI);
                    map.addVertex(store);
                   break;
                case Warehouse.WAREHOUSE_TYPE:
                    Warehouse warehouse = getWarehouse(POI);
                    map.addVertex(warehouse);
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
        System.out.println(map.vertexSet().size());

        for(int i = 0; i<locations.getLength(); i++) {
            Element POI = (Element) locations.item(i);
            int id = Integer.parseInt(POI.getAttribute("id"));
            float xValue = Float.parseFloat(POI.getAttribute("x"));
            float yValue = Float.parseFloat(POI.getAttribute("y"));
            Location location = new Location(id,new Pair<>(xValue,yValue));

            map.addVertex(location);
        }

        System.out.println(map.vertexSet().size());


    }

    private Store getStore(Element POI) {

        int id = Integer.parseInt(POI.getAttribute("id"));
        String name = getTextValue(POI, "name");

        float xValue = getFloatValue(POI, "x");
        float yValue = getFloatValue(POI, "y");

        Store store = new Store(id,name);
        store.setPosition(new Pair<>(xValue,yValue));

        return store;
    }

    private Warehouse getWarehouse(Element POI) {

        int id = Integer.parseInt(POI.getAttribute("id"));
        String name = getTextValue(POI, "name");
        float maxWeight = getFloatValue(POI,"max_weight");

        float xValue = getFloatValue(POI, "x");
        float yValue = getFloatValue(POI, "y");

        Warehouse warehouse = new Warehouse(id,name,maxWeight);
        warehouse.setPosition(new Pair<>(xValue,yValue));

        return warehouse;
    }


}
