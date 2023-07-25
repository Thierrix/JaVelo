package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.lang.reflect.Array;
import java.util.Arrays;

import static java.lang.Float.NaN;

/**
 * Classe représentant un calculateur de profil en long d'un itinéraire donné.
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class ElevationProfileComputer {

    /**
     *
     * @param route itinéraire dont on souhaite obtenir le profil en long
     * @param maxStepLength espacement maximum entre les échantillons du profil
     * @return le profile en long de l'itinéraire route
     */

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(0<maxStepLength);

        int nombreEchantillons = (int) Math.ceil(route.length()/maxStepLength) + 1;

        float[] tab = new float[nombreEchantillons];

        float[] newtab;

        for (int i = 0; i<tab.length; ++i) {
            tab[i] = (float)route.elevationAt(i * maxStepLength);
        }

        if (tableauVide(tab))
            return new ElevationProfile(route.length(),vide(tab));
        else {
            newtab = videGaucheDuTableau(tab);
            newtab = videDroiteDuTaleau(newtab);
            for (int i = 0; i< tab.length; ++i) {
                if (Float.isNaN(newtab[i])) {
                    int j = 0;
                    while (Float.isNaN(newtab[i+j])) {
                        ++j;
                    }
                    newtab = videMilieu(newtab,i-1, i+j);
                    i = i+j;
                }
            }
        }

        return new ElevationProfile(route.length(), newtab);
    }

    /**
     * méthodes ajoutées pour mieux organiser elevationProfile(Route route, double maxStepLength)
     * @param tableau tableau dont on souhaite savoir s'il ne contient que des NaN ou non
     * @return true si le tableau n'est rempli que de NaN, false sinon
     */

    private static boolean tableauVide(float[] tableau) {
        int k = 0;
        while ((k!= tableau.length) && Float.isNaN(tableau[k])) {
            ++k;
        }
        if (k == tableau.length)
            return true;
        else
            return false;
    }

    /**
     * méthodes ajoutées pour mieux organiser elevationProfile(Route route, double maxStepLength)
     * @param tableau rempli que de NaN
     * @return un tableau dont toutes les entrées sont égales à 0
     */

    private static float[] vide(float[] tableau) {
        Arrays.fill(tableau, 0);
        return tableau;
    }

    /**
     * méthodes ajoutées pour mieux organiser elevationProfile(Route route, double maxStepLength)
     * @param tableau tableau dont on veut supprimer les NaN avant la première valeur non nulle en allant de la gauche
     * @return tableau dont les trous de NaN à l'extremité gauche ont été bouchés par la premeière valeur non nulle depuis la gauche dans le tableau
     */

    private static float[] videGaucheDuTableau(float[] tableau) {
        int k = 0;
        while ((k<tableau.length) && Float.isNaN(tableau[k])) {
            ++k;
        }
        if (k == tableau.length){
            Arrays.fill(tableau,0, k, tableau[k-1]);
        } else
            Arrays.fill(tableau, 0, k, tableau[k]);
        return tableau;
    }

    /**
     * méthodes ajoutées pour mieux organiser elevationProfile(Route route, double maxStepLength)
     * @param tableau tableau dont on veut supprimer les NaN avant la première valeur non nulle en allant de la droite
     * @return tableau dont les trous de NaN à l'extremité droite ont été bouchés par la premeière valeur non nulle depuis la droite dans le tableau
     */


    private static float[] videDroiteDuTaleau(float[] tableau) {
        int k = tableau.length-1;
        while ((k>0) && Float.isNaN(tableau[k])) {
            --k;
        }
        Arrays.fill(tableau, k, tableau.length-1, tableau[k]);
        if (k!= tableau.length-1)
            tableau[tableau.length-1] = tableau[k];
        return tableau;
    }

    /**
     * méthodes ajoutées pour mieux organiser elevationProfile(Route route, double maxStepLength)
     * @param tableau tableau dont on veut rmeplir les NaN entre indexDebut et indexFin par des valeurs obtenues par interpolation linéaire
     * @param indexDebut index du premier élément à traiter
     * @param indexFin index du dernier élément à traiter
     * @return un tableau dans lequel tous les NaN ont été remplacés par des valeurs de type float
     */

    private static float[] videMilieu(float[] tableau, int indexDebut, int indexFin) {
        for (int i = indexDebut; i<indexFin; ++i) {
            tableau[i] = (float) Math2.interpolate(tableau[indexDebut], tableau[indexFin], ((double)i-indexDebut)/(indexFin-indexDebut));
        }
        return tableau;
    }


}

