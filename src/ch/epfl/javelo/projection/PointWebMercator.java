package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * Enregeristrement représentant un point dans le système Web Mercator
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record PointWebMercator(double x, double y) {

    private final static int INTERVALLE_MAX = 1;
    private final static int INTERVALLE_MIN = 0;
    private final static int DIM_IMAGE_ORIGINALE = 8;

    /**
     *Constructeur compact
     */

    public PointWebMercator{ //constructeur compact
        Preconditions.checkArgument(x <= INTERVALLE_MAX && INTERVALLE_MIN <= x
                && y <= INTERVALLE_MAX && INTERVALLE_MIN <= y);
    }

    /**
     * Méthode retournant le point dont les coordonnées x et y sont proches du niveau de zoom
     *
     * @param zoomLevel
     *      niveau de zoom
     * @param x
     *      coordonnée x du point
     * @param y
     *      coordonnée y du point
     * @return
     *      le point dont les coordonnées x et y sont proche du niveau de zoom
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator(Math.scalb(x, -zoomLevel - DIM_IMAGE_ORIGINALE),
                Math.scalb(y, -zoomLevel - DIM_IMAGE_ORIGINALE));
    }

    /**
     * Méthode retournant le point Web Mercator correspondant au point du système de coordonnées suisse
     *
     * @param pointCh
     *      le point en coordonnées suisses
     * @return
     *      le point Web Mercator correspondant au point du système de coordonnées suisse donné.
     */
    public static PointWebMercator ofPointCh(PointCh pointCh){

        double x = WebMercator.x(pointCh.lon());
        double y = WebMercator.y(pointCh.lat());

        return new PointWebMercator(x,y);
    }

    /**
     * Méthode retournant la coordonnée x au niveau de zoom donné
     *
     * @param zoomLevel
     *      niveau de zoom
     * @return
     *      la coordonnée x zoomée au niveau de zoom zoomLevel
     */
    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, zoomLevel+DIM_IMAGE_ORIGINALE);
    }

    /**
     * Méthode retournant la coordonnée y au niveau de zoom donné
     *
     * @param zoomLevel
     *      niveau de zoom
     * @return
     *      la coordonnée y zoomée au niveau de zoom zoomLevel
     */
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, zoomLevel+DIM_IMAGE_ORIGINALE);
    }

    /**
     * Méthode retournant la longitude du point en radians
     *
     * @return
     *      la longitude du point en radians
     */
    public double lon(){
        return WebMercator.lon(x);
    }

    /**
     * Méthode retournant la latitude du point
     *
     * @return
     *      la latitude du point en radians
     */
    public double lat(){
        return WebMercator.lat(y);
    }

    /**
     * Méthode retournant le point de coordonnées suisses se trouvant à la même position que le récepteur (this) ou null
     *
     * @return
     *      le point de coordonnées suisses qui se trouve à la même position que le récepteur ou sinon null
     */

    public PointCh toPointCh(){
        double lon = lon();
        double lat = lat();
        if(SwissBounds.containsEN(Ch1903.e(lon, lat), Ch1903.n(lon, lat))){
            return new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat));
        }
        return null;
    }

}