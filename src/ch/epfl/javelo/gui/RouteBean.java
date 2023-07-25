package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Enregistrement regroupant les propriétés relatives aux points de passage et à l'itinéraire correspondant
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */
public final class RouteBean {

    private RouteComputer routeComputer;
    private ObservableList<Waypoint> waypoints;
    private ObjectProperty<Route> route;
    private DoubleProperty highlightedPosition;
    private ObjectProperty<ElevationProfile> elevationProfile;
    private LinkedHashMap<Pair<Waypoint, Waypoint>, Route> tempCacheRoute;

    public RouteBean(RouteComputer routeComputer){
        this.routeComputer = routeComputer;
        waypoints = FXCollections.observableArrayList();
        route = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty();
        elevationProfile = new SimpleObjectProperty<>();
        tempCacheRoute = new LinkedHashMap<>();

        installerAuditeurs();
    }

    public ObservableList<Waypoint> waypoints() {
        return waypoints;
    }

    public Route getRoute(){
        return route.getValue();
    }

    public ReadOnlyObjectProperty<Route> route() {
        return route;
    }

    public ElevationProfile getElevationProfile(){ return elevationProfile.getValue(); }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile() {
        return elevationProfile;
    }

    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    public double highlitedPosition(){
        return highlightedPosition.getValue();
    }

    public void setHighlightedPosition(double position) {
        highlightedPosition.setValue(position);
    }

    public int indexOfNonEmptySegmentAt(double position) {
        int index = getRoute().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).noeudProcheId();
            int n2 = waypoints.get(i + 1).noeudProcheId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    private ElevationProfile initialisationElevation(){
        return route.getValue() != null ? ElevationProfileComputer.elevationProfile(route.getValue(),
                5) : null;
    }

    private Route initialisationRoute(){
        List<Route> segmentsdeRoute = new ArrayList<>();
        if(waypoints.size() >1){
            for(int i =0 ; i < waypoints.size() - 1; i++){
                Pair nouvellePaire = new Pair<>(waypoints.get(i), waypoints.get(i+1));
                if(tempCacheRoute.get(nouvellePaire) != null){
                    Route routeTrouvee = tempCacheRoute.get(nouvellePaire);
                    if(routeTrouvee == null){
                        highlightedPosition.setValue(Double.NaN);
                        return null;
                    }
                    segmentsdeRoute.add(routeTrouvee);
                }
                else{
                    if(waypoints.get(i).noeudProcheId() == waypoints.get(i+1).noeudProcheId())
                        continue;
                        Route nouvelleRoute = routeComputer.bestRouteBetween(waypoints.get(i).noeudProcheId(),
                                waypoints.get(i + 1).noeudProcheId());
                        tempCacheRoute.put(nouvellePaire, nouvelleRoute);
                        if (nouvelleRoute == null) return null;
                        segmentsdeRoute.add(nouvelleRoute);
                }
            }
            return new MultiRoute(segmentsdeRoute);
        }
        highlightedPosition.setValue(Double.NaN);
        return null;
    }

    private void installerAuditeurs(){

        waypoints.addListener((InvalidationListener) observable -> {
            if(waypoints.size()<2){
                route.setValue(null);
                elevationProfile.setValue(null);
            }
            else {
                route.setValue(initialisationRoute());
                elevationProfile.setValue(initialisationElevation());
            }
        });
    }
}