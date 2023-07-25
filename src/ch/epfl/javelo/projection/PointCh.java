package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * Classe correspondant à un point dans le système de coordonnées suisses
 * Ses attributs sont :
 * - e la coordonnée est du point
 * - n la coordonnée nord du point
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record PointCh(double e, double n) {

    public PointCh { // constructeur compact
        Preconditions.checkArgument(SwissBounds.containsEN(e,n));
    }

    /**
     *
     * @param that
     * @return la distance en mètres séparant le récepteur de l'argument
     */

    public double distanceTo(PointCh that) {
        return Math2.norm(this.e - that.e, this.n - that.n);
    }

    /**
     *
     * @param that
     * @return le carré de la distance en mètres séparant le récepteur de l'argument
     */
    public double squaredDistanceTo(PointCh that) {
        return Math.pow(distanceTo(that),2);
    }

    /**
     *
     * @return la longitude du point dans le système WGS84 en radians
     */

    public double lon() {
        return Ch1903.lon(e,n);
    }

    /**
     *
     * @return la latitude du point dans le système WGS84 en radians
     */

    public double lat() {
        return Ch1903.lat(e,n);
    }
}


