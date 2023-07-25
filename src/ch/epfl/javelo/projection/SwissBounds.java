package ch.epfl.javelo.projection;

/**
 * Classe définissant les constantes publique et une méthode définissant si une valeur se trouve dans les limites de la Suisse
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class SwissBounds {

    private SwissBounds(){}

    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;

    public static final double WIDTH = Math.abs(MAX_E - MIN_E);
    public static final double HEIGHT = Math.abs(MAX_N - MIN_N);

    /**
     * Méthode retournant valeur booléenne qui dit si c'est dans les limites de la Suisse
     *
     * @param e
     *      coordonnée est
     * @param n
     *      coordonnée nord
     * @return
     *      valeur booléenne qui dit si c'est dans les limites de la Suisse
     */
    public static boolean containsEN(double e, double n){
        return MIN_E <= e && e <= MAX_E && MIN_N <= n && n <= MAX_N;
    }
}