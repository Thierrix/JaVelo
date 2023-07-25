package ch.epfl.javelo;

/**
 * Classe qui permet la conversion de nombres entre version Q28.4 et d'autres
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class Q28_4 {

    private final static int DECALAGE_DE_QUATRE = 4;

    /**
     * Constructeur pour le rendre non instanciable
     */

    public Q28_4() {}

    /**
     * Retounre la valeur Q28.4 correspondant à l'entier i
     * @param i
     * @return valeur Q28.4 correspondant à l'entier i
     */

    public static int ofInt(int i) {
        return i << DECALAGE_DE_QUATRE;
    }

    /**
     * Retourne la valeur de type double égale à la valeur q28_4
     * @param q28_4
     * @return
     */

    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -DECALAGE_DE_QUATRE);
    }

    /**
     * Retourne la valeur de type float correspondant à la valeur q28_4
     * @param q28_4
     * @return
     */

    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4, -DECALAGE_DE_QUATRE);
    }

}
