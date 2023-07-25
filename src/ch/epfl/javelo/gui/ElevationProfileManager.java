package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

public final class ElevationProfileManager {

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty posProperty;
    private final DoubleProperty mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);
    private final Pane pane = new Pane();
    private final BorderPane bPane = new BorderPane();
    private final Path path = new Path();
    private final Polygon profilGraphPolygon = new Polygon();
    private final VBox panneauVBox = new VBox();
    private final Group group = new Group();
    private Text vBoxText;
    private final Line line = new Line();

    private int espX = 100_000, espY = 1_000;
    private final Insets rectInsets = new javafx.geometry.Insets(10, 10, 20, 40);

    private final ObjectProperty<Rectangle2D> rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
    private final ObjectProperty<Transform> screenToWorldProperty= new SimpleObjectProperty<>(new Affine());
    private final ObjectProperty<Transform> worldToScreenProperty = new SimpleObjectProperty<>(new Affine());

    private final static int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final static int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    private final static int FROM_M_TO_KM = 1_000;
    private final static int FONT_SIZE = 10;
    private final static int CONSTANT_DECALEMENT = 2;
    private final static int DISTANCE_HORIZONTALE = 25;
    private final static int DISTANCE_VERTICALE = 50;
    private final static int CONSTANTE_NULLE = 0;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty posProperty){

        this.elevationProfileProperty = elevationProfileProperty;
        this.posProperty = posProperty;

        initAffichage();

        rectangleProperty.bind(Bindings.createObjectBinding(() ->
                        new Rectangle2D(rectInsets.getLeft(),
                                rectInsets.getTop(),
                                Math.max(pane.getWidth() - rectInsets.getRight() - rectInsets.getLeft(), CONSTANTE_NULLE),
                                Math.max(pane.getHeight() - rectInsets.getBottom() - rectInsets.getTop(), CONSTANTE_NULLE)),
                pane.widthProperty(), pane.heightProperty()));

        rectangleProperty.addListener(o -> {
            transformations();
            initPolygon();
            initGrilleEtEtiquettes();
        });

        elevationProfileProperty.addListener(e -> {
            transformations();
            initPolygon();
            initGrilleEtEtiquettes();
            initStats();
        });

        transformations();
        initPolygon();
        initGrilleEtEtiquettes();
        initStats();
        installerBindersLine();
        installerGestionEvenements();
    }

    /**
     * Fonction qui retourne le panneau contnenant le profil de l'altitude
     * @return
     */
    public Pane pane(){
        return bPane;
    }

    /**
     * Méthode qui retourne une propriété contenant la position de la souris dans le profil
     * @return
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }

    /**
     * Méthode initialisant l'affichage
     */
    private void initAffichage(){

        bPane.getStylesheets().add("elevation_profile.css");
        panneauVBox.setId("profile_data");
        path.setId("grid");
        profilGraphPolygon.setId("profile");

        pane.getChildren().add(path);
        pane.getChildren().add(group);
        pane.getChildren().add(profilGraphPolygon);
        pane.getChildren().add(line);

        bPane.setCenter(pane);
        bPane.setBottom(panneauVBox);
    }

    /**
     * Méthode créant les transformations du monde à l'écran et vice-versa
     */
    private void transformations() {
        if (elevationProfileProperty.get() == null) return;
        Affine a = new Affine();
        a.prependTranslation(-rectInsets.getLeft(), -rectInsets.getTop());
        a.prependScale(elevationProfileProperty.getValue().length() / rectangleProperty.get().getWidth(),
                -(elevationProfileProperty.getValue().maxElevation() - elevationProfileProperty.getValue().minElevation()) / rectangleProperty.get().getHeight());
        a.prependTranslation(CONSTANTE_NULLE, elevationProfileProperty.getValue().maxElevation());

        screenToWorldProperty.setValue(a);
        try {
            worldToScreenProperty.setValue(a.createInverse());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode installant les binders s'occupant de la ligne
     */
    private void installerBindersLine(){

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                        worldToScreenProperty.getValue().transform(posProperty.getValue(), CONSTANTE_NULLE)
                                                        .getX(),
                posProperty));

        line.startYProperty().bind(Bindings.select(rectangleProperty, "minY"));
        line.endYProperty().bind(Bindings.select(rectangleProperty, "maxY"));
        line.visibleProperty().bind(posProperty.greaterThanOrEqualTo(CONSTANTE_NULLE));

    }

    /**
     * Méthode s'occupant de l'initialisation des polygones
     */
    private void initPolygon(){

        if(elevationProfileProperty.get() == null) return;
        profilGraphPolygon.getPoints().clear();

        //Initialisation de la première extrémité en bas à gauche
        profilGraphPolygon.getPoints().add(rectInsets.getLeft());
        profilGraphPolygon.getPoints().add(rectangleProperty.get().getMaxY());

        //Initialisation de tout le polygone représentant l'altitude
        for(int x =(int)rectangleProperty.get().getMinX(); x < rectangleProperty.get().getMaxX(); x++){
            double coordX = screenToWorldProperty.get().transform(x, CONSTANTE_NULLE).getX();
            double elev = elevationProfileProperty.get().elevationAt(coordX);
            double coordY = worldToScreenProperty.get().transform(CONSTANTE_NULLE, elev).getY();
            profilGraphPolygon.getPoints().add( (double) x);
            profilGraphPolygon.getPoints().add(coordY);
        }

        //Initialisation de la seconde extrémité en bas à droite
        profilGraphPolygon.getPoints().add(rectangleProperty.get().getMaxX());
        profilGraphPolygon.getPoints().add(rectangleProperty.get().getMaxY());
    }

    /**
     * Méthode s'occupant de l'initialisation de la grille et des étiquettes
     */
    private void initGrilleEtEtiquettes(){
        if(elevationProfileProperty.get() == null) return;

        path.getElements().clear();
        group.getChildren().clear();

        int nbLignesPosFutures = CONSTANTE_NULLE;

        for(int i : POS_STEPS){
            nbLignesPosFutures = Math2.ceilDiv((int)elevationProfileProperty.get().length(),i);
            if(rectangleProperty.get().getWidth()/nbLignesPosFutures >= DISTANCE_VERTICALE){
                espX = i;
                break;
            }
        }

        int nbLignesElvFutures = CONSTANTE_NULLE;

        for(int i : ELE_STEPS){
            nbLignesElvFutures = Math2.ceilDiv((int)(elevationProfileProperty.get().maxElevation()-elevationProfileProperty.get().minElevation()),i);

            if(rectangleProperty.get().getHeight()/nbLignesElvFutures >= DISTANCE_HORIZONTALE){
                espY = i;
                break;
            }
        }
        System.out.println(espY);

        for(int x = 0; x<=nbLignesPosFutures*espX; x+=espX){

            path.getElements().add(new MoveTo(worldToScreenProperty.get().transform(x, CONSTANTE_NULLE)
                                                                        .getX(),
                                                rectInsets.getTop()));
            path.getElements().add(new LineTo(worldToScreenProperty.get().transform(x, CONSTANTE_NULLE)
                                                                        .getX(),
                                                rectangleProperty.get().getMaxY()));

            Text txt = new Text(Integer.toString(x/FROM_M_TO_KM));
            txt.setFont(Font.font("Avenir", FONT_SIZE));
            txt.textOriginProperty().set(VPos.TOP);

            txt.setX(worldToScreenProperty.get().transform(x, CONSTANTE_NULLE).getX()
                                                -txt.prefWidth(CONSTANTE_NULLE)/2);
            txt.setY(rectangleProperty.get().getMaxY());


            txt.getStyleClass().add("grid_label");
            txt.getStyleClass().add("horizontal");

            group.getChildren().add(txt);
        }

        int debut = Math2.ceilDiv((int)elevationProfileProperty.get().minElevation(),espY) * espY;

        for(int y = debut; y<debut + nbLignesElvFutures * espY; y+=espY){

            path.getElements().add(new MoveTo(rectInsets.getLeft(),
                                                worldToScreenProperty.get().transform(CONSTANTE_NULLE,y).getY()));
            path.getElements().add(new LineTo(rectangleProperty.get().getMaxX(),
                                                worldToScreenProperty.get().transform(CONSTANTE_NULLE,y).getY()));

            Text txt = new Text(Integer.toString(y));
            txt.setFont(Font.font("Avenir", FONT_SIZE));
            txt.textOriginProperty().set(VPos.CENTER);

            txt.setX(rectInsets.getLeft()-txt.prefWidth(CONSTANTE_NULLE)-CONSTANT_DECALEMENT);
            txt.setY(worldToScreenProperty.get().transform(CONSTANTE_NULLE,y).getY());

            txt.getStyleClass().add("grid_label");
            txt.getStyleClass().add("vertical");

            group.getChildren().add(txt);
        }

    }

    private void initStats(){
        if(elevationProfileProperty.get() == null) return;
        panneauVBox.getChildren().clear();

        vBoxText = new Text(String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                elevationProfileProperty.get().length()/FROM_M_TO_KM,
                elevationProfileProperty.get().totalAscent(),
                elevationProfileProperty.get().totalDescent(),
                elevationProfileProperty.get().minElevation(),
                elevationProfileProperty.get().maxElevation(
                )));
        panneauVBox.getChildren().add(vBoxText);

    }

    /**
     * Méthode s'occupant de la gestion des évènements
     */
    private void installerGestionEvenements(){

        pane.setOnMouseMoved(event -> {
            if(rectContientPointeur(event.getX(), event.getY())){
                mousePositionOnProfileProperty.setValue(screenToWorldProperty.get().transform(event.getX(), CONSTANTE_NULLE)
                                                        .getX());
            }
        });

        pane.setOnMouseExited(event -> mousePositionOnProfileProperty.setValue(Double.NaN));
    }

    /**
     * Si dans le rectangle, retourne true
     * sinon false
     * @param x
     *          Coordonnée x
     * @param y
     *          Coordonnée y
     * @return
     */
    private boolean rectContientPointeur(double x, double y) {
        return (x > rectangleProperty.getValue().getMinX() && x < rectangleProperty.getValue().getMaxX()
                && y < rectangleProperty.getValue().getMaxY() && y > rectangleProperty.getValue().getMinY());
    }
}