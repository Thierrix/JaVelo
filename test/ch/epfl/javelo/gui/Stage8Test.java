package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class Stage8Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
//        ObservableList<Waypoint> waypoints =
//                FXCollections.observableArrayList(
//                        new Waypoint(new PointCh(2532697, 1152350), 159049),
//                        new Waypoint(new PointCh(2538659, 1154350), 117669));
        ErrorManager errorConsumer = new ErrorManager();
        CostFunction cf = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph,cf);
        RouteBean misterBean = new RouteBean(routeComputer);
//        misterBean.setWaypoints(waypoints);
        misterBean.setHighlightedPosition(1000);
        RouteManager routeManager = new RouteManager(misterBean,mapViewParametersP);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        misterBean.waypoints(),
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);

        StackPane mainPane =
                new StackPane(baseMapManager.pane(),
                        waypointsManager.pane(),routeManager.pane());
        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

//    private static final class ErrorConsumer
//            implements Consumer<String> {
//        @Override
//        public void accept(String s) { System.out.println(s); }
//    }
//
////    public static void main(String[] args) { launch(args); }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        Graph graph = Graph.loadFrom(Path.of("lausanne"));
//        CityBikeCF costFunction = new CityBikeCF(graph);
//        RouteComputer routeComputer =
//                new RouteComputer(graph, costFunction);
//
//        Route route = routeComputer
//                .bestRouteBetween(159049, 117669);
//        ElevationProfile profile = ElevationProfileComputer
//                .elevationProfile(route, 5);
//
//        ObjectProperty<ElevationProfile> profileProperty =
//                new SimpleObjectProperty<>(profile);
//        DoubleProperty highlightProperty =
//                new SimpleDoubleProperty(1500);
//
//        ElevationProfileManager profileManager =
//                new ElevationProfileManager(profileProperty,
//                        highlightProperty);
//
//        highlightProperty.bind(
//                profileManager.mousePositionOnProfileProperty());
//
//        Scene scene = new Scene(profileManager.pane());
//
//        primaryStage.setMinWidth(600);
//        primaryStage.setMinHeight(300);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
}