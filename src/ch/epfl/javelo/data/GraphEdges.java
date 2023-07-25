package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Enregistrement représentant le tableau de toutes les arêtes du graphe JaVelo
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private final static int INDEX_BITS_MAX = 31;
    private final static int EDGE_MAX_OFFSET = 10;
    private final static int LONGUEUR_OFFSET = 4;
    private final static int ELEVATION_OFFSET = 6;
    private final static int ATTR_OFFSET = 8;
    private final static int ID_DEBUT = 0;
    private final static int PROFIL_DEBUT = 30;
    private final static int LONGUEUR_PROFILS_BITS = 2;

    /**
     * retourne vrai ssi l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient
     * @param edgeId
     *      ID du edge donné
     * @return
     *      valeur booléenne dépendante de son sens
     */

    public boolean isInverted(int edgeId) {
        return (Bits.extractUnsigned(edgesBuffer.get(EDGE_MAX_OFFSET * edgeId), INDEX_BITS_MAX, 1) > 0);
    }

    /**
     * retourne l'identité du nœud destination de l'arête d'identité donnée
     * @param edgeId
     *      ID du edge donné
     * @return
     *      identité du noeud de destination de l'arête
     */

    public int targetNodeId(int edgeId) {
        if (isInverted(edgeId))
            return ~(edgesBuffer.getInt(EDGE_MAX_OFFSET * edgeId));
        else
            return edgesBuffer.getInt(EDGE_MAX_OFFSET * edgeId);
    }

    /**
     * retourne la longueur, en mètres, de l'arête d'identité donnée
     * @param edgeId
     *      ID du edge donné
     * @return
     *      la longueur en mètres
     */

    public double length(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(EDGE_MAX_OFFSET * edgeId + LONGUEUR_OFFSET)));
    }


    /**
     * retourne le dénivelé positif, en mètres, de l'arête d'identité donnée
     * @param edgeId
     *      ID du edge donné
     * @return
     *      le dénivelé de l'arête d'identité donnée
     */

    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(EDGE_MAX_OFFSET* edgeId + ELEVATION_OFFSET)));
    }

    /**
     * retourne vrai ssi l'arête d'identité donnée possède un profil
     * @param edgeId
     *      ID du edge donné
     * @return
     *      valeur booléenne retourant le fait de posséder un profil
     */

    public boolean hasProfile(int edgeId) {
        return !(Bits.extractUnsigned(profileIds.get(edgeId), 30, 2) == 0);
    }

    /**
     * retourne le tableau des échantillons du profil de l'arête d'identité donnée, qui est vide si l'arête ne possède pas de profil
     * @param edgeId
     *      ID du edge donné
     * @return
     *      tableau des échantillons du profil de l'arête donnée
     */

    public float[] profileSamples(int edgeId) {
        short longueurArete = edgesBuffer.getShort(EDGE_MAX_OFFSET * edgeId + LONGUEUR_OFFSET);
        int nombreEchantillons = 1 + Math2.ceilDiv(Short.toUnsignedInt(longueurArete), Q28_4.ofInt(2));
        float[] sample = new float[nombreEchantillons];
        int profileIndex = Bits.extractUnsigned(profileIds.get(edgeId), ID_DEBUT, PROFIL_DEBUT);
        int profilType = Bits.extractUnsigned(profileIds.get(edgeId), PROFIL_DEBUT, LONGUEUR_PROFILS_BITS);

        /*
        switch(profilType) {

            case 0 :
                return new float[0];
            case 1 :
                for (int i = 0; i < nombreEchantillons; ++i) {
                    sample[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(profileIndex + i)));
                }
            case 2 : case 3 :
                int type = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
                sample = type2et3(type, nombreEchantillons, elevations, profileIndex);
        }

         */




        // cas ou type = 0
        if (profilType == 0)
            return new float[0];

        // cas ou type = 1
        else if (profilType == 1) {
            for (int i = 0; i < nombreEchantillons; ++i) {
                sample[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(profileIndex + i)));
            }
        }
        else { // cas ou type = 2 ou 3
            int type = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
            sample = type2et3(type, nombreEchantillons, elevations, profileIndex);
        }



        if(isInverted(edgeId)) {
            int j = 0;
            float temp;
            if (nombreEchantillons%2==0) {
                while (j<nombreEchantillons/2) {
                    temp = sample[j];
                    sample[j] = sample[nombreEchantillons-1-j];
                    sample[nombreEchantillons-1-j] = temp;
                    ++j;
                }
            } else {
                while (j<Math.floor(nombreEchantillons/2)) {
                    temp = sample[j];
                    sample[j] = sample[nombreEchantillons-1-j];
                    sample[nombreEchantillons-1-j] = temp;
                    ++j;
                }
            }
        }
        return sample;
    }


    /**
     * retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     * @param edgeId
     *      ID du edge donné
     * @return
     *      ensemble l'identité des attributs d'une arête donnée
     */

    public int attributesIndex ( int edgeId){
        return Short.toUnsignedInt(edgesBuffer.getShort(EDGE_MAX_OFFSET * edgeId + ATTR_OFFSET));
    }

    /**
     * Méthode auxiliaire permettant de former le tableau des échantillons du profil d'une arête dont le profil est de type 2 ou 3
     * @param type
     *      type du profil (soit 2, soit 3)
     * @param nombreEchantillons
     *      le nombre d'échantillons du profil, donc la taille du tableau
     * @param elevations
     *      le ShortBuffer contenant les informations nécessaire à la construction du tableau
     * @param profileIndex
     *      l'index du profil en question
     * @return
     *      tableau de float[] constitué des échantillons du profil d'index profileIndex, de type 2 ou 3
     */

    private float[] type2et3(int type, int nombreEchantillons, ShortBuffer elevations, int profileIndex) {
        int t = 1;
        int a = 0;
        float[] sample = new float[nombreEchantillons];
        sample[0] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(profileIndex)));
        while (t < nombreEchantillons) {
            if ((t % 2 == 1 && type == 2) || (t % 4 == 1 && type == 3))
                a += 1;
            if (t % 2 == 0)
                sample[t] = sample[t - 1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(profileIndex + a),
                        type == 2 ? 0 : t % 4 == 0 ? 0 : 8, type == 2 ? 8 : 4));
            else if (t % 2 == 1)
                sample[t] = sample[t - 1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(profileIndex + a),
                        type == 2 ? 8 : t % 4 == 1 ? 12 : 4, type == 2 ? 8 : 4));
            ++t;
        }
        return sample;
    }
}