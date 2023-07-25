package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * Classe immuable représentant le profil en long d'un itinéraire simple ou multiple.
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class ElevationProfile {

    private double length;
    private float[] elevationSamples;
    private final double MIN_ELEVATION;
    private final double MAX_ELEVATION;
    private DoubleUnaryOperator functionElevation;


    /**
     * Constructeur
     *
     * @param length
     *      longueur de l'itinéraire
     * @param elevationSamples
     *      tableau contenant les échantillons d'élévation
     */
    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2 );

        this.length = length;
        this.elevationSamples = elevationSamples;

        DoubleSummaryStatistics analyse  = new DoubleSummaryStatistics();

        for (float elevation : elevationSamples) {
            analyse.accept(elevation);
        }
        MIN_ELEVATION = analyse.getMin();
        MAX_ELEVATION = analyse.getMax();

        this.functionElevation = Functions.sampled(elevationSamples, length);
    }

    /**
     * Méthode retournant la longueur du profil en mètres
     * @return
     *      la longueur du profil
     */
    public double length(){
        return length;
    }

    /**
     * Méthode retournant  l'altitude minimum du profil, en mètres
     *
     * @return
     *      l'altitude minimum du profil
     */
    public double minElevation(){

        return MIN_ELEVATION;
    }

    /**
     * Méthode retournant  l'altitude maximum du profil, en mètres
     * @return
     *      l'altitude maximum du profil
     */
    public double maxElevation(){

        return MAX_ELEVATION;
    }

    /**
     * Méthode retournant le dénivelé positif total du profil, en mètres
     *
     * @return
     *      le dénivelé positif total du profil
     */
    public double totalAscent(){

        return totalAscentOrDescent(true);
    }

    /**
     * Méthode retournant le dénivelé négatif total du profil, en mètres
     *
     * @return
     *      le dénivelé négatif total du profil
     */
    public double totalDescent(){

        return totalAscentOrDescent(false);
    }

    /**
     * Méthode retournant l'altitude du profil à la position donnée
     *
     * @param position
     *      position donnée
     * @return
     *      l'altitude du profil en fonction de la position
     */
    public double elevationAt(double position){
        return functionElevation.applyAsDouble(position);
    }

    /**
     * Méthode auxiliaire permettant de calculer le dénivelé positif si le boolean positif est true et le dénivelé négatif sinon
     * @param positif
     *      est true si le dénivelé à calculer est le dénivelé positif, false s'il s'agit du dénivelé négatif
     * @return dénivelé positif si positif est true, dénivelé négatif sinon
     */
    private double totalAscentOrDescent(boolean positif) {
        double sum = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            double precedentElevation = elevationSamples[i - 1];
            double currentElevation = elevationSamples[i];
            double diff = currentElevation - precedentElevation;
            if (diff > 0 && positif)
                sum += diff;
            else if (diff < 0 && !positif)
                sum += Math.abs(diff);
        }
        return sum;
    }

}