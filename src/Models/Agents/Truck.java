package Models.Agents;


import Models.Tool;
import javafx.util.Pair;

import java.util.ArrayList;

public class Truck extends Vehicle {

    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.f2);
        tools.add(Tool.f3);

        return tools;
    }

    public Truck() {
        super(1, 3000, 1000, Vehicle.STREET, new Pair<>(0.0f, 0.0f), Truck.generateToolsArray());
    }
}
