package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * Interface représentant un itinéraire
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public interface Route {
    /**
     * Méthode retournant l'index du segment à la position donnée
     *
     * @param position
     *      position en mètres
     * @return
     *      index du segment
     */
    int indexOfSegmentAt(double position);

    /**
     * Méthode retournant la longueur de l'itinéraire
     *
     * @return
     *      longueur en mètres
     */
    double length();

    /**
     * Méthode retournant une liste contenant la totalité des arêtes de l'itinéraire
     *
     * @return
     *      List<> contenant la totalité des arêtes de l'itinéraire
     */
    List<Edge> edges();

    /**
     * Méthode retournant la totalité des points situés aux extrémités des arêtes de l'itinéraire
     *
     * @return
     *      List<> contenant la totalité des points situés aux extrémités
     */
    List<PointCh> points();

    /**
     * Méthode retournant le point se trouvant à la position donnée le long de l'itinéraire
     *
     * @param position
     *      position en mètres
     * @return
     *     le point de classe PointCh
     */

    PointCh pointAt(double position);

    /**
     * Méthode retournant l'identité du nœud appartenant à l'itinéraire et
     * se trouvant le plus proche de la position donnée
     *
     * @param position
     *      position en mètres
     * @return
     *      l'identité du noeud appartenant à l'itinéraire le plus proche
     */

    int nodeClosestTo(double position);

    /**
     * Méthode retournant le point de l'itinéraire se trouvant le plus proche du point de référence donné
     *
     * @param point
     *      point de type PointCh
     * @return
     *      le point de l'itinéraire se trouvant le plus proche du pt de référence donné
     */
    RoutePoint pointClosestTo(PointCh point);

    /**
     * Méthodde retournant l'altitude à la position donnée
     *
     * @param position
     *      position donnée
     * @return
     *      altitude en mètres
     */
    double elevationAt(double position);

}
