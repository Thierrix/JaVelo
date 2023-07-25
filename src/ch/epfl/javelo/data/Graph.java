package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Classe immuable représentant le graphe JaVelo
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Constructeur public
     *
     * @param nodes
     * @param sectors
     * @param edges
     * @param attributeSets
     */

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.edges = edges;
        this.sectors = sectors;
        this.attributeSets = List.copyOf(attributeSets);

    }

    public static Graph loadFrom(Path basePath) throws IOException {
        String[] pathArray = {"nodes.bin", "edges.bin", "elevations.bin", "profile_ids.bin",
                "attributes.bin", "sectors.bin"};

        ShortBuffer elevationsBuffer;
        ByteBuffer edgesBuffer;
        IntBuffer nodesBuffer;
        IntBuffer profileIdsBuffer;
        ByteBuffer sectorsBuffer;
        LongBuffer attributesBuffer;
        List<AttributeSet> attributeSets = new ArrayList<>();

        try (FileChannel channel = FileChannel.open(basePath.resolve(pathArray[0]))) {
            nodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }

        try (FileChannel channel = FileChannel.open(basePath.resolve(pathArray[1]))) {
            edgesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }

        try (FileChannel channel = FileChannel.open(basePath.resolve(pathArray[2]))) {

            elevationsBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asShortBuffer();
        }

        try (FileChannel channel = FileChannel.open(basePath.resolve(pathArray[3]))) {

            profileIdsBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }

        try (FileChannel channel = FileChannel.open(basePath.resolve(pathArray[4]))) {
            attributesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
            for (int j = 0; j < attributesBuffer.capacity(); j++) {
                AttributeSet el = new AttributeSet(attributesBuffer.get(j));
                attributeSets.add(el);
            }
        }
        try (FileChannel channel = FileChannel.open(basePath.resolve(pathArray[5]))) {
            sectorsBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }

        return new Graph(new GraphNodes(nodesBuffer), new GraphSectors(sectorsBuffer),
                new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer), attributeSets);
    }

    /**
     * méthode qui retourne le nombre total de noeuds dans le graphe
     * @return nombre total de noeuds dans le graphe
     */

    public int nodeCount() {
        return nodes.count();
    }

    /**
     * méthode qui retourne la position du noeud d'identité
     * @param nodeId
     * @return position du noeud d'identité
     */

    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * méthode qui retourne le nonbre d'arêtes sortant du noeud d'identité donnée
     * @param nodeId
     * @return nombre d'arêtes sortant du noeud d'identité donnée
     */

    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * méthode qui retourne l'identité de la edgeIndex-ième arête sortant du noeud d'identité
     * @param nodeId
     * @param edgeIndex
     * @return identité de la edgeIndex-ième arête sortant du noeud d'identité
     */

    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * méthode qui retourne l'identité du noeud se trouvant le plus proche du point donné, à la distance maximale donnée, ou -1 si aucun noeud ne correspond à ces critères
     * @param point
     * @param searchDistance distance maximale en mètres
     * @return identité du nœud se trouvant le plus proche du point donné, à la distance maximale donnée (en mètres), ou -1 si aucun nœud ne correspond à ces critères
     */

    public int nodeClosestTo(PointCh point, double searchDistance) {
        int idRecherchee = -1;
        double distanceProvisoire = Math.pow(searchDistance,2);
        List<GraphSectors.Sector> secteursAutour = sectors.sectorsInArea(point, searchDistance);
        for (int j = 0; j<secteursAutour.size(); ++j) {
            for (int k = secteursAutour.get(j).startNodeId(); k < secteursAutour.get(j).endNodeId(); ++k) {
                if (point.squaredDistanceTo(nodePoint(k)) < distanceProvisoire) {
                    distanceProvisoire = point.squaredDistanceTo(nodePoint(k));
                    idRecherchee = k;
                }
            }
        }
        return idRecherchee;
    }

    /**
     * méthode qui retourne l'identité du nœud destination de l'arête d'identité donnée
     * @param edgeId
     * @return identité du nœud destination de l'arête d'identité donnée
     */

    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * méthode qui retourne vrai ssi l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient
     * @param edgeId
     * @return vrai ssi l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient
     */

    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * méthode qui retourne l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     * @param edgeId
     * @return ensemble des attributs OSM attachés à l'arête d'identité donnée
     */

    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * méthode qui retourne la longueur, en mètres, de l'arête d'identité donnée
     * @param edgeId
     * @return longueur, en mètres, de l'arête d'identité donnée
     */

    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * méthode qui retourne le dénivelé positif total de l'arête d'identité donnée
     * @param edgeId
     * @return dénivelé positif total de l'arête d'identité donnée
     */

    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * méthode qui retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction; si l'arête ne possède pas de profil, alors cette fonction retourne Double.NaN pour tout argument
     * @param edgeId
     * @return profil en long de l'arête d'identité donnée, sous la forme d'une fonction; si l'arête ne possède pas de profil, alors cette fonction retourne Double.NaN
     */

    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (edges.hasProfile(edgeId))
            return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
        return Functions.constant(Double.NaN);
    }

}
