package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import javax.swing.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe immuable représentant un itinéraire simple.
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class SingleRoute implements Route {

    List<Edge> edges;
    double[] itineraryArray;

    public SingleRoute(List<Edge> edges) {
        this.edges = List.copyOf(edges);
        Preconditions.checkArgument(!edges().isEmpty());
        this.itineraryArray = new double[edges.size()];
        int index = 0;
        double overallItinerary = 0;
        for(Edge i: edges){;
            this.itineraryArray[index] = overallItinerary;
            overallItinerary += i.length();
            index++;
        }
    }

    /**
     * {@inheritDoc}
     * @param position
     *      position en mètres
     * @return
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public double length() {
        double length = 0;
        for(Edge edge : edges) {
            length += edge.length();
        }
        return length;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for (Edge edge : edges) {
            points.add(edge.fromPoint());
        }
        points.add(edges.get(edges.size()-1).toPoint());
        return points;
    }

    /**
     * {@inheritDoc}
     * @param position
     *      position en mètres
     * @return
     */
    @Override
    public PointCh pointAt(double position) {
        // changer quand position >length
        position = Math2.clamp(0, position, length());
        int index = indexLePlusProche(position);
        return edges.get(index).pointAt(position-itineraryArray[index]);
    }

    /**
     * {@inheritDoc}
     * @param position
     *      position en mètres
     * @return
     */
    @Override
    public int nodeClosestTo(double position) {
        int index = indexLePlusProche(position);
        return (position - itineraryArray[index]) > (edges.get(index).length()/2) ?
                edges.get(index).toNodeId() : edges.get(index).fromNodeId();
    }

    /**
     * {@inheritDoc}
     * @param point
     *      point de type PointCh
     * @return
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {

        List<PointCh> pointsProches = new ArrayList<>();

        for(Edge indexEdge: edges){
            if(indexEdge.length()==0)
                pointsProches.add(indexEdge.pointAt(indexEdge.positionClosestTo(point)));
            else
            pointsProches.add(indexEdge.pointAt(Math2.clamp(0,indexEdge.positionClosestTo(point), indexEdge.length())));
        }

        RoutePoint routePointMin = new RoutePoint(pointsProches.get(0), Math2.clamp(0, edges.get(0).positionClosestTo(point), edges.get(0).length()), pointsProches.get(0).distanceTo(point));

        for(int i = 1; i<pointsProches.size(); i++){
            if(edges.get(i).length()==0)
                routePointMin = routePointMin.min(pointsProches.get(i), itineraryArray[i], pointsProches.get(i).distanceTo(point));
            else
            routePointMin = routePointMin.min(pointsProches.get(i), itineraryArray[i] + Math2.clamp(0, edges.get(i).positionClosestTo(point), edges.get(i).length()), pointsProches.get(i).distanceTo(point));
        }

        return routePointMin;
    }

    /**
     * {@inheritDoc}
     * @param position
     *      position donnée
     * @return
     */
    @Override
    public double elevationAt(double position) {
        int index = indexLePlusProche(position);
        position = Math2.clamp(0, position, length());
        return edges.get(index).elevationAt(position-itineraryArray[index]);
    }

    /**
     *
     * @param position
     *      position en mètres
     * @return
     *      retourne l'index après opération
     */
    private int indexLePlusProche(double position){

        if (position < 0)
            return (0);
        if (position >= this.length())
            return edges.size()-1; //possible outofboundsexception
        int valueFromBinarySearch = Arrays.binarySearch(itineraryArray, position);

        if(valueFromBinarySearch == -1){
            return 0;
        }

        int trueValue = (valueFromBinarySearch < 0) ? -valueFromBinarySearch-2 : valueFromBinarySearch;

        return trueValue;
    }

}

