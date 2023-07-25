package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public final class JaVelo extends Application {

    private final static int DIM_HAUTEUR = 600;
    private final static int DIM_LONGUEUR = 600;

    public static void main(String[]args){ launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheTilePath = Path.of("osm-cache");
        String tuileHost = "tile.openstreetmap.org";

        ErrorManager errorManager = new ErrorManager();

        CostFunction cf = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, cf);
        RouteBean routeBean = new RouteBean(routeComputer);

        TileManager tileManager = new TileManager(cacheTilePath, tuileHost);

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph,
                tileManager,
                routeBean,
                errorManager);

        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(routeBean.elevationProfile(),
                routeBean.highlightedPositionProperty());

        BorderPane finalPane =  new BorderPane();
        SplitPane mapPane = new SplitPane(annotatedMapManager.pane());
        StackPane allPane = new StackPane();

        MenuItem menuItem = new MenuItem("Exporter GPX");
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Fichier", null);

        mapPane.setOrientation(Orientation.VERTICAL);
        allPane.getChildren().add(mapPane);
        allPane.getChildren().add(errorManager.pane());

        menu.getItems().add(menuItem);
        menuBar.getMenus().add(menu);
        menuBar.setUseSystemMenuBar(true);
        menuItem.setDisable(true);

        finalPane.setTop(menuBar);
        finalPane.setCenter(allPane);

        mapPane.setResizableWithParent(elevationProfileManager.pane(), false);

        menuItem.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo", routeBean.getRoute(), routeBean.getElevationProfile());
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });

        routeBean.route().addListener((value, oldValue, newValue) -> {
            if(oldValue != null && newValue == null){
                mapPane.getItems().remove(1);
                menuItem.setDisable(true);
            }
            if(oldValue == null && newValue != null){
                mapPane.getItems().add(1, elevationProfileManager.pane());
                menuItem.setDisable(false);
            }
        });

        routeBean.highlightedPositionProperty().bind(Bindings.createDoubleBinding(() -> {
            if (annotatedMapManager.mousePositionOnRouteProperty().getValue()>=0)
                return  annotatedMapManager.mousePositionOnRouteProperty().get();
            else
                return elevationProfileManager.mousePositionOnProfileProperty().get();

        },annotatedMapManager.mousePositionOnRouteProperty(),elevationProfileManager.mousePositionOnProfileProperty()));


        Scene scene = new Scene(finalPane);
        primaryStage.setMinHeight(DIM_HAUTEUR);
        primaryStage.setMinWidth(DIM_LONGUEUR);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }

}
