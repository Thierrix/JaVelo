package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

/**
 * Classe gérant l'affichage de l'itinéraire et une partie de l'interaction avec lui
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class RouteManager {

    private final RouteBean beanItineraire;
    private final ReadOnlyObjectProperty<MapViewParameters> MapViewParameterProperty;

    private final Pane pane;
    private final Polyline polyligneRoute;
    private final Circle cercleSurligne;
    private  Point2D anciennePosTopleftLigne;
    private Point2D anciennePosTopleftCercle;


    public RouteManager(RouteBean beanItineraire, ReadOnlyObjectProperty<MapViewParameters> MapViewParameterProperty){

        this.beanItineraire = beanItineraire;
        this.MapViewParameterProperty = MapViewParameterProperty;


        pane = new Pane();
        pane.setPickOnBounds(false);

        polyligneRoute = new Polyline();
        polyligneRoute.setId("route");
        initialisationPolyligne();

        cercleSurligne = new Circle(5);
        cercleSurligne.setId("highlight");
        initialisationCercle();

        pane.getChildren().add(polyligneRoute);
        pane.getChildren().add(cercleSurligne);


        conditionVisible();
        installerGestionEvenements();
        installerAuditeurs();
    }

    /**
     * Méthode retournant le panneau le panneau JavaFX contenant la ligne représentant l'itinéraire
     * et le disque de mise en évidence.
     * @return
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Méthode vérifiant si la ligne et le cercle peuvent être visibles ou non
     */
    private void conditionVisible(){
        if(beanItineraire.getRoute() == null){
            polyligneRoute.setVisible(false);
            cercleSurligne.setVisible(false);
        }
        else{
            polyligneRoute.setVisible(true);
            cercleSurligne.setVisible(true);
        }
    }

    /**
     * Méthode se chargeant d'initialiser la polyligne
     */
    private void initialisationPolyligne() {
        if (beanItineraire.getRoute() != null) {
            polyligneRoute.getPoints().clear();
            for (PointCh point : beanItineraire.getRoute().points()) {
                PointWebMercator pointTraduit = PointWebMercator.ofPointCh(point);
                double xPane = MapViewParameterProperty.getValue().viewX(pointTraduit);
                double yPane = MapViewParameterProperty.getValue().viewY(pointTraduit);
                polyligneRoute.getPoints().add(xPane);
                polyligneRoute.getPoints().add(yPane);
                anciennePosTopleftLigne = MapViewParameterProperty.getValue().topLeft();
            }
            changeLigneDisposition();
        }
    }

    /**
     * Méthode se chargeant de l'initialisation du cercle
     */
    private void initialisationCercle(){

        if(beanItineraire.getRoute() != null && !Double.isNaN(beanItineraire.highlitedPosition())){
            PointCh highlightedPoint = beanItineraire.getRoute().pointAt(beanItineraire.highlitedPosition());
            PointWebMercator pointTraduit = PointWebMercator.ofPointCh(highlightedPoint);
            double xCircle = MapViewParameterProperty.getValue().viewX(pointTraduit);
            double yCircle = MapViewParameterProperty.getValue().viewY(pointTraduit);
            cercleSurligne.setCenterX(xCircle);
            cercleSurligne.setCenterY(yCircle);
            anciennePosTopleftCercle =MapViewParameterProperty.get().topLeft();
        }
        changeCercleDisposition();
    }

    /**
     * Méthode se chargeant du changement de disposition du cercle
     */
    private void changeCercleDisposition(){
        if(anciennePosTopleftCercle != null) {
            cercleSurligne.setLayoutX(anciennePosTopleftCercle.getX() - MapViewParameterProperty.getValue().topLeft().getX());
            cercleSurligne.setLayoutY(anciennePosTopleftCercle.getY() - MapViewParameterProperty.getValue().topLeft().getY());
        }
    }

    /**
     * Méthode se chargeant du changement de disposition
     */
    private void changeLigneDisposition(){
        if(anciennePosTopleftLigne != null){
            polyligneRoute.setLayoutX(anciennePosTopleftLigne.getX() - MapViewParameterProperty.getValue().topLeft().getX());
            polyligneRoute.setLayoutY(anciennePosTopleftLigne.getY() - MapViewParameterProperty.getValue().topLeft().getY());
        }
    }

    /**
     * Méthode se chargeant de la gestion des évènements
     */
    private void installerGestionEvenements(){

        cercleSurligne.setOnMouseClicked(event -> {
            Point2D posAMettre = pane.localToParent(event.getX(), event.getY());
            PointWebMercator pointCoord = MapViewParameterProperty.getValue()
                                                                  .pointAt(posAMettre.getX(), posAMettre.getY());
            if(beanItineraire.getRoute().nodeClosestTo(beanItineraire.highlitedPosition()) != 0) {
                beanItineraire.waypoints().add(beanItineraire.indexOfNonEmptySegmentAt(beanItineraire.highlitedPosition())+1,
                        new Waypoint(pointCoord.toPointCh(),
                                beanItineraire.getRoute().nodeClosestTo(beanItineraire.highlitedPosition())));
            }
        });
    }

    /**
     * Méthode s'occupant d'installer des auditeurs
     */
    private void installerAuditeurs(){

        beanItineraire.route().addListener(observable -> {
            conditionVisible();
            if (beanItineraire.waypoints().size()>1) {
                initialisationCercle();
                initialisationPolyligne();
            }
        });

        beanItineraire.highlightedPositionProperty().addListener((o, po, co)->{
            initialisationCercle();
            if(Double.isNaN((Double)o.getValue())){
                cercleSurligne.setVisible(false);
            }
            else {
                cercleSurligne.setVisible(true);
                initialisationCercle();
            }
        });

        MapViewParameterProperty.addListener((observable, oldValue, newValue) -> {
            cercleSurligne.setVisible(false);
            changeCercleDisposition();
            if(oldValue.zoomLevel() != newValue.zoomLevel()){
                initialisationCercle();
                changeCercleDisposition();
                initialisationPolyligne();
            }
            else if(MapViewParameterProperty.getValue().topLeft() != newValue.topLeft()
                    && oldValue.zoomLevel() == newValue.zoomLevel()){
                changeCercleDisposition();
                changeLigneDisposition();
            }
        });

    }
}