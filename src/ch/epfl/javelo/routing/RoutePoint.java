package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregeristrement représentant le point d'un itinéraire le plus proche d'un point de référence donné
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * Constante représentant un RoutePoint null
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     *  Méthode qui retourne un point identique au récepteur mais dont la position est décalée
     *  de la différence donnée, qui peut être positive ou négative
     *
     * @param positionDifference
     *      Diff de position
     *
     * @return
     *      un point qui est impacté par positionDifference
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     * Méthode qui retourne this si sa distance à la référence est inférieure ou égale à celle de that,
     * et that sinon
     *
     * @param that
     *      un autre RoutePoint
     *
     * @return
     *      le point récepteur, ou bien le point that
     */
    public RoutePoint min(RoutePoint that){
        return(this.distanceToReference<= that.distanceToReference) ? this : that;
    }

    /**
     *  Méthode qui compare la distanceToReference de this et la compare à thatDistanceToReference et agit en fonction
     *
     * @param thatPoint
     *      un PointCh
     * @param thatPosition
     *      une position
     * @param thatDistanceToReference
     *      la distance de référence
     * @return
     *      this si la distance à la référence de this est inférieure ou égale à thatDistanceToReference,
     *      et une nouvelle instance de RoutePoint prenant en paramètre les arguments passés à min sinon.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return(this.distanceToReference <= thatDistanceToReference) ? this: new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}