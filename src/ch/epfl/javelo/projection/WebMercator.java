package ch.epfl.javelo.projection;
import ch.epfl.javelo.Math2;

/**
 * Classe permettant de convertir entre les coordonnées WGS84 et les coordonnées Web
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class WebMercator {

    /**
     * Constructeur pour le rendre non instanciable
     */

    private WebMercator(){}

    /**
     *  Méthode retournant la coordonnée x de la projection d'un point se trouvant à la longitude lon.
     *
     * @param lon
     *      longitude en radians
     * @return
     *      la coordonnée x de la projection d'un point se trouvant à une longitude lon
     */

    public static double x(double lon){
        return (lon+Math.PI)/(2*Math.PI);
    }

    /**
     * Méthode retournant la coordonnée y de la projection d'un point se trouvant à une latitude lat.
     *
     * @param lat
     *      la latitude en radians
     * @return
     *      la coordonnée y de la projection d'un point se trouvant à une latitude lat
     */
    public static double y(double lat){
        return((Math.PI - Math2.asinh(Math.tan(lat)))/(2*Math.PI));
    }

    /**
     * Méthode retournant  la longitude d'un point dont la projection se trouve à la coordonnée x
     *
     * @param x
     *      coordonnée x
     * @return
     *      la longitude en radians d'un point de projection coordonnée x
     */
    public static double lon(double x){
        return(2*Math.PI*x - Math.PI);
    }

    /**
     * Méthode retournant  la latitude d'un point dont la projection se trouve à la coordonnée y
     *
     * @param y
     *      coordonnée y
     * @return
     *      la latitude en radians d'un point de projection y
     */
    public static double lat(double y){
        return(Math.atan(Math.sinh(Math.PI - 2*Math.PI*y)));
    }
}
