package ch.epfl.javelo.projection;

/**
 *
 * Classe contenant des méthodes convertissant WGS84 en coordonées suisses et vice-versa
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class Ch1903 {

    /**
     *Constructeur pour le rendre compact
     */
    private Ch1903(){}


    //les coordonnées, dans cette classe, sont en radians

    /**
     * Méthode donnant la longitude pour convertir de WGS84 en coordonnées suisses
     *
     * @param
     *      lon par défaut
     * @return
     *      la longitude qui doit être utilisée pour les calculs
     */
    private static double newLon(double lon){
        return(0.0001*(3600*lon -26782.5));
    }

    /**
     * Méthode donnant la longitude pour convertir de WGS84 en coordonnées suisses
     *
     * @param
     *      lat par défaut
     * @return
     *      donne la latitude qui doit être utilisée pour les calculs
     */
    private static double newLat(double lat){
        return(0.0001*(3600*lat - 169028.66));
    }

    /**
     * Méthode donnant la coordonnée est à partir de la latitude et longitude données (WGS84)
     *
     * @param lon
     *      longitude
     * @param lat
     *      latitude
     * @return
     *      convertit la coordonnée est à partir de la latitude et longitude données (WGS84)
     */
    public static double e(double lon, double lat){
        lon = 0.0001*(3600*Math.toDegrees(lon) -26782.5);
        lat = 0.0001*(3600*Math.toDegrees(lat) - 169028.66);

        double coordEst = 2600072.37 + 211455.93 * lon - 10938.51 * lon * lat
                - 0.36 * lon * Math.pow(lat,2)- 44.54*Math.pow(lon,3);
        return(coordEst);
    }

    /**
     * Méthode la coordonnée nord (coordonnées suisses)
     *
     * @param lon
     *      longitude
     * @param lat
     *      latitude
     * @return
     *      convertit la coordonée nord à partir de la longitude et la latitude données (WGS84)
     */
    public static double n(double lon, double lat){
        lon = newLon(Math.toDegrees(lon));
        lat = newLat(Math.toDegrees(lat));

        double coordNord = 1200147.07 + 308807.95 * lat + 3745.25 * lon*lon
                + 76.63 * lat*lat - 194.56* lon*lon*lat + 119.79*lat*lat*lat;
        return coordNord;
    }

    /**
     * Méthode donnant la longitude en WGS84
     *
     * @param e
     *      coordonnée Est
     * @param n
     *      coordonnée Nord
     * @return
     *      la longitude en WGS84
     */
    public static double lon(double e, double n){
        double x = Math.pow(10,-6) *(e - 2600000);
        double y = Math.pow(10,-6) *(n - 1200000);

        double lon = 2.6779094 + 4.728982*x + 0.791484*x*y + 0.1306*x*Math.pow(y,2) -0.0436*Math.pow(x,3);
        lon = lon * 100/36;
        return(Math.toRadians(lon));
    }

    /**
     * Méthode donnant la latitude en WGS84
     *
     * @param e
     *      la coordonnée Est
     * @param n
     *      la coordonnée Nord
     * @return
     *      la latitude en WGS84
     */
    public static double lat(double e, double n){
        double x = Math.pow(10,-6) *(e - 2600000);
        double y = Math.pow(10,-6) *(n - 1200000);

        double lat = 16.9023892 + 3.238272*y - 0.270978*Math.pow(x,2)
                - 0.002528*Math.pow(y,2) - 0.0447 *Math.pow(x,2)*y - 0.0140 * Math.pow(y,3);
        return(Math.toRadians(lat * 100/36));
    }

}