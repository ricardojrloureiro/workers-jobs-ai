package Models.Locations;

import javafx.util.Pair;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Map {

    public static void main(String[] args) {
        cenas();
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

    private static void cenas() {
        UndirectedGraph<Location, DistanceEdge> map = new SimpleGraph<>(DistanceEdge.class);

        Location l1 = new Location(1,new Pair<>(0.0f,0.0f));
        Location l2 = new Location(1,new Pair<>(1.0f,1.0f));
        map.addVertex(l1);
        map.addVertex(l2);
        map.addEdge(l1, l2, new DistanceEdge(50));

        System.out.println(map.toString());
    }

}
