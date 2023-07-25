package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * Enregistrement représentant le tableau de tous les nœuds du graphe JaVelo.
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private final static int NODE_INDEX = 3;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;
    private final static int NODE_DEBUT = 0;
    private final static int NODE_LONGUEUR = 32;
    private final static int ARETES_SORTANTES_DEBUT = 28;
    private final static int ARETES_SORTANTES_LONGUEUR = 4;
    private static int EDGE_DEBUT = 0;
    private static int EDGE_LONGUEUR = 28;

    /**
     * retourne le nombre total de nœuds
     * @return
     *      le nombre total de noeuds
     */
    public int count() {
        return buffer.capacity()/NODE_INTS;
    }

    /**
     * retourne la coordonnée E du nœud d'identité donnée
     * @param nodeId
     *      noeud d'identité donné
     * @return
     */

    public double nodeE(int nodeId) {
        return Q28_4.asDouble(Bits.extractSigned(buffer.get(NODE_INDEX*nodeId+OFFSET_E), NODE_DEBUT, NODE_LONGUEUR));

    }

    /**
     * retourne la coordonnée N du nœud d'identité donnée
     * @param nodeId
     *      noeud d'identité donné
     * @return
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(Bits.extractSigned(buffer.get(NODE_INDEX*nodeId+OFFSET_N),NODE_DEBUT, NODE_LONGUEUR));

    }

    /**
     * retourne le nombre d'arêtes sortant du nœud d'identité donné
     * @param nodeId
     *      noeud d'identité donné
     * @return
     *      nombre d'arêtes sortant du noeud d'identité donné
     */

    public int outDegree(int nodeId) {
        return Bits.extractUnsigned(buffer.get(NODE_INDEX*nodeId+OFFSET_OUT_EDGES),ARETES_SORTANTES_DEBUT, ARETES_SORTANTES_LONGUEUR);

    }

    /**
     * retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId
     * @param nodeId
     *      noeud d'identité donné
     * @param edgeIndex
     *      index d'arête sortant du noeud di'dentité nodeId
     * @return
     *      l'identité de l'arête voulue
     */

    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned(buffer.get(NODE_INDEX*nodeId+OFFSET_OUT_EDGES),EDGE_DEBUT, EDGE_LONGUEUR) + edgeIndex;

    }
}