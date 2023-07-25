package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.file.Path;

public final class Stage6Test {
    public static void writesKMLFileLausanne(int startNodeId, int endNodeId, String name) throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(startNodeId, endNodeId);
        KmlPrinter.write(name, r);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
    }

    public static void writesKMLFileFromWest(int startNodeId, int endNodeId, String name) throws IOException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(startNodeId, endNodeId);
        KmlPrinter.write(name, r);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
    }

    public static void main(String[] args) throws IOException {

        /**
         writesKMLFileLausanne(159049, 117669, "javelo.kml");
         writesKMLFileLausanne(175000, 143235, "javelo2.kml");
         writesKMLFileLausanne(124587, 185320, "javelo3.kml");
         writesKMLFileLausanne(127654, 183194, "javelo4.kml");
         writesKMLFileLausanne(158149, 173706, "javelo5.kml");
         writesKMLFileLausanne(157404, 114881, "javelo6.kml");
         writesKMLFileLausanne(119948, 157182, "javelo7.kml");
         writesKMLFileLausanne(143505, 115460, "javelo8.kml");
         writesKMLFileLausanne(139011, 165274, "javelo9.kml");
         writesKMLFileLausanne(121806, 135247, "javelo10.kml");
         */
        for (int i = 0; i < 10; i++) {
            writesKMLFileFromWest(2046055,2694240, "javelo11.kml");
        }
    }

}