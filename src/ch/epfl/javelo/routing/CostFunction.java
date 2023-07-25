package ch.epfl.javelo.routing;

public interface CostFunction {

    /**
     * retourne le facteur par lequel la longueur de l'arête d'identité edgeId, partant du nœud d'identité nodeId, doit être multipliée; ce facteur doit impérativement être supérieur ou égal à 1
     * @param nodeId
     * @param edgeId l'identité de l'arête
     * @return
     */
    public double costFactor(int nodeId, int edgeId);
}
