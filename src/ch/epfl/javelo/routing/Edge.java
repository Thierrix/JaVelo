package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Enregeristrement représentant une arête d'un itinéraire
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * méthode qui retourne la position le long de l'arête, en mètres, qui se trouve la plus proche du point donné
     * @param point
     * @return position le long de l'arête, en mètres, qui se trouve la plus proche de point
     */

    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * méthode qui retourne le point se trouvant à la position donnée sur l'arête, exprimée en mètres
     * @param position
     * @return point se trouvant à la position donnée sur l'arête, exprimée en mètres
     */

    public PointCh pointAt(double position) {
        if(length == 0) return fromPoint;
        double xPourcent = position/length;
        double newN = Math2.interpolate(fromPoint.n(), toPoint.n(), xPourcent);
        double newE = fromPoint.e() + (toPoint.e()-fromPoint.e())*xPourcent;
        return new PointCh(newE, newN);
    }

    /**
     * méthode qui retourne l'altitude, en mètres, à la position donnée sur l'arête
     * @param position
     * @return l'altitude, en mètres, à la position donnée sur l'arête
     */

    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
