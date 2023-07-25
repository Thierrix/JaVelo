package ch.epfl.javelo;

/**
 * Classe permettant d'extraire des séquences de 32bits
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class Bits {


    /**
     * Constructeur pour le rendre non instanciable
     */

    private Bits(){}

    private final static int LONGUEUR_BITS_MAX = Integer.SIZE;
    private final static int INDEX_BITS_MAX = Integer.SIZE - 1;
    private final static int INDEX_BITS_MIN = 0;

    /**
     * Méthode retournant une plage, interprétée comme un complément à deux
     *
     * @param value
     *      vecteur de 32 bits
     * @param start
     *      index à partir de ou séquencer
     * @param length
     *      longueur de l'extrait
     * @return
     * la plage de bits commencant au bit d'index start
     */

    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(start>=INDEX_BITS_MIN && start<=INDEX_BITS_MAX && length>0
                && length<=LONGUEUR_BITS_MAX-start);
        int a = value << LONGUEUR_BITS_MAX-length-start;
        return a >> LONGUEUR_BITS_MAX-length;
    }

    /**
     * Méthode retournant une plage non signée
     *
     * @param value
     *      vecteur de 32 bits
     * @param start
     *      index à partir de ou séquencer
     * @param length
     *      longueur de l'extrait
     * @return
     * la plage de bits commencant au bit d'index start
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(start >=INDEX_BITS_MIN && start <= INDEX_BITS_MAX && length >= 1
                && 0 < LONGUEUR_BITS_MAX - length && LONGUEUR_BITS_MAX - length - start>=0);
        return (value << (LONGUEUR_BITS_MAX - length - start)) >>> (LONGUEUR_BITS_MAX - length);
    }


}
