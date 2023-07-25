package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * Classe immuable représentant un planificateur d'itinéraire.
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class RouteComputer {

    Graph graph;
    CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * méthode qui retourne l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité endNodeId dans le graphe
     * passé au constructeur, ou null si aucun itinéraire n'existe. Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException
     * @param startNodeId
     *      premier noeud de la route
     * @param endNodeId
     *      dernier noeud de la route
     * @return
     *      itinéraire de coût total minimal entre startNodeId à endNodeId
     */

    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        Preconditions.checkArgument(startNodeId!=endNodeId);

        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {

            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        float[] distances = new float[graph.nodeCount()]; // tableau des distances menant aux noeuds visités

        int[] predecesseur = new int[graph.nodeCount()]; // tableau des prédecesseur de chaque noeud visité

        PointCh endPoint = graph.nodePoint(endNodeId); // point correspondant au noeud d'arrivée voulu

        PointCh startPoint = graph.nodePoint(startNodeId); // point correspondant au noeud de départ

        Arrays.fill(distances, Float.POSITIVE_INFINITY);

        List<Edge> edges = new ArrayList<>();

        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();

        distances[startNodeId] = (float) startPoint.distanceTo(endPoint);

        enExploration.add(new WeightedNode(startNodeId, 0));

        int edgeId, noeudArrivee, node;
        float d; // variable permettant de tester les distances aux noeuds d'arrivée de chaque arête

        while (!enExploration.isEmpty()) {
            // on retire de enExploration le noeud qui minimise la distance au noeud de départ
            WeightedNode nodeProvisoire = enExploration.remove();

            if (nodeProvisoire.nodeId==endNodeId) { // moment où l'itinéraire est trouvé
                node = endNodeId;
                while (node!=startNodeId) { // construction de la liste des arêtes par lesquelles passe l'itinéraire
                    edges.add(Edge.of(graph, findEdge(predecesseur[node], node), predecesseur[node], node));
                    node = predecesseur[node];
                }
                Collections.reverse(edges);
                return new SingleRoute(edges);
            }

            PointCh pointProvisoire = graph.nodePoint(nodeProvisoire.nodeId);

            for (int i = 0; i<graph.nodeOutDegree(nodeProvisoire.nodeId); ++i) {
                edgeId = graph.nodeOutEdgeId(nodeProvisoire.nodeId, i); // identité de la i ème arête sortante
                noeudArrivee = graph.edgeTargetNodeId(edgeId); // identité du noeud d'arrivée de l'arête d'identité edgeId
                PointCh pointArrivee = graph.nodePoint(noeudArrivee); // point correspondant au noeud d'arrivée
                d = (float) (distances[nodeProvisoire.nodeId] - pointProvisoire.distanceTo(endPoint)
                        + graph.edgeLength(edgeId)*costFunction.costFactor(nodeProvisoire.nodeId,edgeId));
                // si la distance trouvée est inférieure à celle correspondante dans le tableau des distances, mettre à jour les informations
                // et attribuer une nouvelle distance au noeud d'arrivée ainsi qu'un nouveau prédecesseur, le noeud provisoire, celui dont on
                // étudie les arêtes sortantes
                // puis ajouter le noeud d'arrivée à enExploration
                if (d<(distances[noeudArrivee]-pointArrivee.distanceTo(endPoint)) && distances[nodeProvisoire.nodeId]!=Float.NEGATIVE_INFINITY) {
                    distances[noeudArrivee] = (float) (d + pointArrivee.distanceTo(endPoint));
                    predecesseur[noeudArrivee] = nodeProvisoire.nodeId;
                    enExploration.add(new WeightedNode(noeudArrivee, distances[noeudArrivee]));
                }
            }
            distances[nodeProvisoire.nodeId] = Float.NEGATIVE_INFINITY; // on marque le noeud sur lequel on vient de passer comme étant utilisé

        }

        return null; // il s'agit du cas où, malheureusement, aucun itinéraire n'a été trouvé !
    }

    /**
     * méthode que nous avons créée pour toruver l'arête qui relie le noeud d'identité nodeBefore et le noeud d'identité nodeAfter
     * @param nodeBefore
     *      première extremité de l'arête
     * @param nodeAfter
     *      deuxième extremité de l'arête
     * @return
     *      l'identité de l'arête reliant le noeud d'identité nodeBefore à celui d'identité endNodeBefore
     */

    private int findEdge(int nodeBefore, int nodeAfter) {
        int i = 0;
        while (graph.edgeTargetNodeId(graph.nodeOutEdgeId(nodeBefore,i))!=nodeAfter) {
            ++i;
        }
        return graph.nodeOutEdgeId(nodeBefore, i);
    }

}