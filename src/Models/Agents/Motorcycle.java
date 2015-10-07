package Models.Agents;

import Models.Tool;
import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;

public class Motorcycle extends Vehicle {

    private static ArrayList<Integer> generateToolsArray()
    {
        ArrayList<Integer> tools = new ArrayList<>();
        tools.add(Tool.tool1);
        tools.add(Tool.f3);

        return tools;
    }

    public Motorcycle() {
        super(4, 350, 300, Vehicle.STREET,new Pair<>(0.0f, 0.0f), Motorcycle.generateToolsArray());
    }
}
