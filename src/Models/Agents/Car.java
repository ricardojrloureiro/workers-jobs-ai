package Models.Agents;

import Models.Tool;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Car extends Vehicle {

    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.f1);
        tools.add(Tool.f2);

        return tools;
    }

    public Car() {
        super(3, 500, 550, Vehicle.STREET, new Pair<>(0.0f, 0.0f), Car.generateToolsArray());
    }
}
