
package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.file.Path;

public final class kmlDoc {

    public static void writesKMLFile(int startNodeId, int endNodeId, String name) throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(startNodeId, endNodeId);
        KmlPrinter.write(name, r);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
    }

    public static void main(String[] args) throws IOException {
        writesKMLFile(159049, 117669, "javelo.kml");
        writesKMLFile(175000, 143235, "javelo2.kml");
        writesKMLFile(124587, 185320, "javelo3.kml");
        writesKMLFile(127654, 183194, "javelo4.kml");
        writesKMLFile(158149, 173706, "javelo5.kml");
        writesKMLFile(157404, 114881, "javelo6.kml");
        writesKMLFile(119948, 157182, "javelo7.kml");
        writesKMLFile(143505, 115460, "javelo8.kml");
        writesKMLFile(139011, 165274, "javelo9.kml");
        writesKMLFile(121806, 135247, "javelo10.kml");
    }

}


