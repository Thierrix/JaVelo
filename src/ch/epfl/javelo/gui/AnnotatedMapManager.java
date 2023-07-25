package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Classe gérant l'affichage de la carte annotée
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */
public final class AnnotatedMapManager {

    private final Graph graph;
    private final TileManager gestionnaireTuiles;
    private final RouteBean beanItineraire;
    private final ErrorManager errorManager;

    private final StackPane pane;
    private final DoubleProperty mousePositionOnProfile = new SimpleDoubleProperty(0);
    private final ObjectProperty<Point2D> posActuelleSourisProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<MapViewParameters> mapViewParameterProperty = new SimpleObjectProperty<>();

    private final WaypointsManager gestionPtPassage;
    private final BaseMapManager gestionCarte;
    private final RouteManager gestionRoute;

    private final static int DISTANCE_SEPARATION = 15;
    private final static int ZOOM_LEVEL = 12;
    private final static int COORD_X = 543200;
    private final static int COORD_Y = 370650;

    public AnnotatedMapManager(Graph graph, TileManager gestionnaireTuiles,
                               RouteBean beanItineraire, ErrorManager errorManager){

        this.beanItineraire = beanItineraire;
        this.graph = graph;
        this.gestionnaireTuiles = gestionnaireTuiles;
        this.errorManager = errorManager;

        mapViewParameterProperty.set(new MapViewParameters(ZOOM_LEVEL, COORD_X, COORD_Y));

        gestionPtPassage = new WaypointsManager(graph, mapViewParameterProperty,
                                                                 beanItineraire.waypoints(), errorManager);
        gestionCarte = new BaseMapManager(gestionnaireTuiles,
                                                         gestionPtPassage,
                                                         mapViewParameterProperty);

        gestionRoute = new RouteManager(beanItineraire, mapViewParameterProperty);

        pane = new StackPane(gestionCarte.pane(), gestionPtPassage.pane(), gestionRoute.pane());
        pane.getStylesheets().add("map.css");

        installerGestionEvenements();
    }

    /**
     * Méthode retournant le peau contenant le fond de carte
     * @return
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Méthode retournant la propriété contenant la position de la souris sur la route
     * @return
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnProfile;
    }

    private void installerGestionEvenements(){
            pane.setOnMouseMoved(event -> {
            posActuelleSourisProperty.set( new Point2D(event.getX(), event.getY()));

        });

        mousePositionOnProfile.bind(
                Bindings.createDoubleBinding(
                        () ->{
                            if(posActuelleSourisProperty.get() == Point2D.ZERO) return Double.NaN;

                            if(beanItineraire.getRoute() == null) return Double.NaN;
                            PointWebMercator ptSourisMap = mapViewParameterProperty.get().pointAt(posActuelleSourisProperty.get().getX(), posActuelleSourisProperty.get().getY());
                            if(ptSourisMap.toPointCh() ==  null) return Double.NaN;

                            RoutePoint ptSourisProcheRoute = beanItineraire.getRoute().pointClosestTo(ptSourisMap.toPointCh());

                            PointWebMercator ptSourisProcheWM = PointWebMercator.ofPointCh(beanItineraire.route().get().pointAt(ptSourisProcheRoute.position()));
                            Point2D ptProcheSouris = new Point2D(mapViewParameterProperty.get().viewX(ptSourisProcheWM),
                                    mapViewParameterProperty.get().viewY(ptSourisProcheWM));

                            return posActuelleSourisProperty.get().distance(ptProcheSouris) <= DISTANCE_SEPARATION ? ptSourisProcheRoute.position()
                                    : Double.NaN;

                        }, posActuelleSourisProperty, mapViewParameterProperty, beanItineraire.route())
        );

        pane.setOnMouseExited(event -> posActuelleSourisProperty.set(Point2D.ZERO));

    }
}
