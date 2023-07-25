package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un itinéraire multiple, composé d'une séquence de segments contigus
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class MultiRoute implements Route{

    private final List<Route> segments;

    /**
     * Constructeur de Multiroute construisant un itinéraire multiple composé de segments
     * @param segments
     *      Liste de segments
     *
     * @Throws IllegalArgumentException
     *      si segments est vide
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(segments.size() != 0);
        this.segments = List.copyOf(segments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOfSegmentAt(double position) {
        int index = 0;
        position = Math2.clamp(0, position, length());

        for (Route seg : segments) {
            if (position < seg.length()) {
                index += seg.indexOfSegmentAt(position) + 1;
                break;
            } else {
                if (seg.indexOfSegmentAt(position) == 0) {
                    index += 1;
                } else {
                    index += seg.indexOfSegmentAt(position) + 1;
                }

                position -= seg.length();
            }
        }
        return --index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double length() {
        double length = 0;
        for(Route i : segments){
            length += i.length();
        }
        return length;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for(Route i: segments) {
            edges.addAll(i.edges());
        }
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> pointsMultiRoute = new ArrayList<>();
        List<PointCh> pointsSegment;
        pointsMultiRoute.addAll(segments.get(0).points());
        for(int i = 1; i < segments.size(); i++){
            pointsSegment = segments.get(i).points();
            pointsSegment.remove(0);
            pointsMultiRoute.addAll(pointsSegment);
        }

        return pointsMultiRoute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointCh pointAt(double position) {
        int index = nearPositionIndexFinder(position);
        position = positionSubstractor(index, position);

        return segments.get(index).pointAt(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeClosestTo(double position) {
        int index = nearPositionIndexFinder(position);
        position = positionSubstractor(index, position);

        return segments.get(index).nodeClosestTo(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double addedLength = 0;
        int index=0;
        RoutePoint routePointMin = segments.get(0).pointClosestTo(point);
        for(int i = 0; i<segments.size(); i++){
            if(point.squaredDistanceTo(segments.get(i).pointClosestTo(point).point()) < point.squaredDistanceTo(routePointMin.point())){
                routePointMin = segments.get(i).pointClosestTo(point);
                index = i;
            }
        }
        for (int i = 0; i < index; i++) {
            addedLength += segments.get(i).length();
        }
        return routePointMin.withPositionShiftedBy(addedLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double elevationAt(double position) {
        int index = nearPositionIndexFinder(position); //1
        position = positionSubstractor(index, position);
        return segments.get(index).elevationAt(position);
    }

    /**
     * Méthode auxiliaire calculant l'index du segment de la liste des segments le plus proche de la position
     *
     * @param position
     *      int position
     * @return
     *      index
     */
    private int nearPositionIndexFinder(double position){
        position = Math2.clamp(0, position, length());
        for(int i = 0; i<segments.size(); i++){
            if(position > segments.get(i).length()) {
                position -= segments.get(i).length();
            }
            else{
                return i;
            }
        }
        return segments.size()-1;
    }

    /**
     * Méthode retournant la position soustraite en fonction de l'index ayant la position la plus proche de la position donnée
     * @param index
     *      int index
     * @param position
     *      int position
     * @return
     *      nouvelle position
     */
    private double positionSubstractor(int index, double position){
        position = Math2.clamp(0, position, length());
        for(int i = 0; i<index; i++){
            position -= segments.get(i).length();
        }
        return position;
    }

}