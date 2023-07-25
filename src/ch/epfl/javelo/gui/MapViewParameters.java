package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Enregistrement représentant les paramètres du fond de carte présenté dans l'interface graphique
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record MapViewParameters(int zoomLevel, double xHautGauche, double yHautGauche) {

    /**
     * Méthode qui retourne les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D
     * @return coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D
     */
    public Point2D topLeft() {
        return new Point2D(xHautGauche, yHautGauche);
    }

    /**
     * Méthode qui retourne une instance de MapViewParameters identique au récepteur, dont les coordonnées du coin haut-gauche sont celles passées en arguments
     * @param xHautGauche coordonnées x à attribuer à l'instance de MapViewParameters créée
     * @param yHautGauche coordonnées y à attribuer à l'instance de MapViewParameters créée
     * @return instance de MapViewParameters indentique au récepteur mais avec xHautGauche et yHautGauche comme coordonnées
     */
    public MapViewParameters withMinXY(double xHautGauche, double yHautGauche) {
        return new MapViewParameters(zoomLevel, xHautGauche, yHautGauche);
    }

    /**
     * Méthode qui prend en arguments les coordonnées x et y d'un point, exprimées par rapport au coin haut-gauche de la portion de carte affichée à l'écran, et retourne ce point sous la forme d'une instance de PointWebMercator
     * @param x coordonée x du point voulu
     * @param y coordonnée y du point voulu
     * @return point de coordonnées x et y
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(zoomLevel, x+xHautGauche, y+yHautGauche);
    }

    /**
     * Méthode qui retourne la position x du point Web Mercator donné en argumenet
     * @param point point Web Mercator dont on veut extraire le x
     * @return position x du point Web Mercator donné en argumenet
     */
    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel)-xHautGauche;
    }

    /**
     * Méthode qui retourne la position y du point Web Mercator donné en argumenet
     * @param point point Web Mercator dont on veut extraire le y
     * @return position y du point Web Mercator donné en argumenet
     */
    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel)-yHautGauche;
    }

}
