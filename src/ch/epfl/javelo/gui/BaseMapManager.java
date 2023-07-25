package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
//import ch.epfl.javelo.gui.WaypointsManager;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Classe gérant l'affichage et l'interaction avec le fond de carte
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */
public final class BaseMapManager {

    private final TileManager gestionnaireTuiles;
    private final WaypointsManager gestionPointsPassage;
    private final ObjectProperty<MapViewParameters> MapParameterProperty;
    private boolean redrawNeeded;
    private final Pane pane;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private static final int LONGUEUR_TUILE = 256;
    private static final int ZOOM_MIN = 8;
    private static final int ZOOM_MAX = 19;

    //constructeur
    public BaseMapManager(TileManager gestionnaireTuiles, WaypointsManager gestionPointsPassage,
                          ObjectProperty<MapViewParameters> MapParameterProperty) {

        pane  = new Pane();
        this.canvas = new Canvas();

        this.gestionnaireTuiles = gestionnaireTuiles;
        this.gestionPointsPassage = gestionPointsPassage;
        this.MapParameterProperty = MapParameterProperty;

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        pane.getChildren().add(canvas);
        this.gc = canvas.getGraphicsContext2D();

        installerGestionEvenements();
        installerAuditeurs();
        redrawOnNextPulse();
    }


    /**
     * Méthode retournant le panneau JavaFX affichant le fond de carte
     * @return
     */
    public Pane pane(){
        return pane;

    }

    /**
     * Méthode s'occupant de redesiner le fond de carte
     */
    private void redrawIfNeeded() {

        if (!redrawNeeded) return;
        redrawNeeded = false;

        dessinCarte();
    }

    /**
     * Méthode qui appelle le redessin au prochain battement
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Méthode s'occupant du dessin de la carte
     */
    private void dessinCarte(){

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        int xInitial = (int) MapParameterProperty.get().topLeft().getX();
        int yInitial = (int) MapParameterProperty.get().topLeft().getY();

        //c'est pour avoir l'index en fonction de LONGUEUR_TUILE d'ou la division puis la multiplication (vu que c'est des int)
        int xTopLeft = (xInitial/ LONGUEUR_TUILE)*LONGUEUR_TUILE;
        int yTopLeft = (yInitial/LONGUEUR_TUILE)*LONGUEUR_TUILE;

        try {
            for (int x = xTopLeft; x < xTopLeft+canvas.getWidth()+LONGUEUR_TUILE; x+=LONGUEUR_TUILE) {
                for (int y = yTopLeft; y < yTopLeft+canvas.getHeight()+LONGUEUR_TUILE; y += LONGUEUR_TUILE) {

                    gc.drawImage(gestionnaireTuiles.imageForTileAt(
                                    new TileManager.TileId(MapParameterProperty.get().zoomLevel(),
                                            x/LONGUEUR_TUILE,
                                            y/LONGUEUR_TUILE)),
                            (x/LONGUEUR_TUILE)*LONGUEUR_TUILE - xInitial, (y/LONGUEUR_TUILE)*LONGUEUR_TUILE - yInitial);
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    /**
     * Méthode s'occupant d'installer des gestionnaires d'évènements
     */
    private void installerGestionEvenements(){

        // ScrollEvent for zoom
        SimpleLongProperty minScrollTime = new SimpleLongProperty();

        pane.setOnScroll(event -> {
            if (event.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            int scrollZoom = (int) Math.signum(event.getDeltaY());
            int newZoom = Math2.clamp(ZOOM_MIN, MapParameterProperty.get().zoomLevel()+scrollZoom, ZOOM_MAX);
            PointWebMercator ptZoom = MapParameterProperty.get().pointAt(event.getX(), event.getY());
            double multiplicateur = scrollZoom > 0  ? 2: 0.5;
            if(newZoom!=MapParameterProperty.get().zoomLevel()) {
                MapParameterProperty.setValue(new MapViewParameters(newZoom,
                        (MapParameterProperty.get().xHautGauche() + event.getX()) * multiplicateur - event.getX(),
                        (MapParameterProperty.get().yHautGauche() + event.getY()) * multiplicateur - event.getY()));
            }

        });

        ObjectProperty<Point2D> begin = new SimpleObjectProperty<>();
        begin.setValue(new Point2D(0, 0));
        ObjectProperty<Point2D> end = new SimpleObjectProperty<>();
        end.setValue(new Point2D(0, 0));

        //trois gestionaires d'evenement
        pane.setOnMousePressed(event -> {
                    begin.setValue(new Point2D(event.getX(), event.getY()));
                }
        );

        pane.setOnMouseDragged(event -> {
            end.setValue(new Point2D(event.getX(), event.getY()));

            float decalageX = -((float)end.get().getX() - (float)begin.get().getX());
            float decalageY = -((float)end.get().getY() - (float)begin.get().getY());


            Point2D newTopLeft = MapParameterProperty.get().topLeft().add(decalageX, decalageY);
            double xTopLeft = newTopLeft.getX();
            double yTopLeft = newTopLeft.getY();
            MapParameterProperty.setValue(MapParameterProperty.get().withMinXY(xTopLeft, yTopLeft));
            begin.setValue(new Point2D(event.getX(),event.getY()));
        });


        pane.setOnMouseReleased(event -> {
            if(!event.isStillSincePress()) {
                end.setValue(new Point2D(0, 0));
                begin.setValue(new Point2D(0, 0));
            }
        });

        pane.setOnMouseClicked(event -> {
            try{
                if (event.isStillSincePress()) gestionPointsPassage.    addWaypoint(event.getX(), event.getY());
            } catch (Exception e){
            }
        });
    }


    /**
     * Méthode s'occupant d'installer des auditeurs détectant les changements
     */
    private void installerAuditeurs(){

        MapParameterProperty.addListener((o, oS, nS) -> redrawOnNextPulse());
        canvas.widthProperty().addListener(observable -> redrawOnNextPulse());
        canvas.heightProperty().addListener(observable -> redrawOnNextPulse());
    }

}
