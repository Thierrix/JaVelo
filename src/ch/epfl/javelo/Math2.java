package ch.epfl.javelo;

/**
 * classe contenant les méthodes mathématiques pour la suite de la programmation
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */
public final class Math2 {

    private Math2() {
    }

    /**
     * Retourne la partie entière par excès de la division de x par y
     *
     * @param x
     *      le numérateur
     * @param y
     *      le dénominateur
     * @return
     *      partie entière par excès de la division de x par y, mais lève une IllegalArgumentException si x ou y est < 0
     */

    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x>=0 && y>0);
        return (x+y-1)/y;
    }

    /**
     *  Retourne l'ordonnée du point se trouvant sur la droite passant par (0,y0) et (1,y1) et de coordonnée x donnée
     *
     * @param y0
     *      coordonnée à l'origine
     * @param y1
     *      coordonnée d'abcisse 1
     * @param x
     *      abcisse de la coordonnée que l'on veut.
     * @return
     *      ordonnée de la coordonnée que l'on veut.
     */
    public static double interpolate(double y0, double y1, double x){
        return(Math.fma((y1-y0), x, y0));
    }

    /**
     * Méthode limitant la valeur v à l'intervalle allant de min à max
     *
     * @param min
     *      le minimum
     * @param v
     *      une valeur donnée
     * @param max
     *      le maximum
     * @return
     *      min si v<min et max si v>max
     */

    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min<max);
        return Math.max(min, Math.min(max, v));
    }

    /**
     * Méthode limitant la valeur v à l'intervalle allant de min à max
     *
     * @param min
     *      le minimum
     * @param v
     *      une valeur donnée
     * @param max
     *      le maximum
     * @return
     *      min si v<min et max si v>max
     */

    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(min<max);
        return Math.max(min, Math.min(max, v));
    }

    /**
     * Méthode retournant le sinus hyperbolique inverse de x
     *
     * @param x
     *      qui est l'abcisse de la coordonnée
     * @return
     *      arcsinh du x
     */
    public static double asinh(double x){
        return(Math.log(x + Math.sqrt(1+x*x)));
    }

    /**
     * Méthode retournant le produit scalaire entre u et v
     *
     * @param uX
     *      coordonnée x du vecteur U
     * @param uY
     *      coordonnée y du vecteur U
     * @param vX
     *      coordonnée x du vecteur V
     * @param vY
     *      coordonnée y du vecteur V
     * @return
     *      le produit scalaire entre v et u
     */

    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return (uX*vX + vY*uY);
    }

    /**
     * Méthode retournant le carré de la norme de u
     *
     * @param uX
     *      coordonnée x du vecteur
     * @param uY
     *      coordonnée y du vecteur
     * @return
     *      la norme au carré du vecteur U
     */
    public static double squaredNorm(double uX, double uY){
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Méthode retournant la norme de u
     *
     * @param uX
     *      coordonnée x du point U
     * @param uY
     *      coordonnée y du point U
     * @return
     *      la norme du vecteur u
     */

    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX,uY));
    }

    /**
     * Méthode retournant la longueur de la projection du vecteur allant de A à P sur le vecteur allant de A à B
     *
     * @param aX
     *      coordonnée x du point A
     * @param aY
     *      coordonnée y du point A
     * @param bX
     *      coordonnée x du point B
     * @param bY
     *      coordonnée y du point B
     * @param pX
     *      coordonnée x du point P
     * @param pY
     *      coordonnée y du point P
     * @return
     *      retourne la longueur de la projection du vecteur allant de A à P sur le vecteur allant de A à B
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double uv = dotProduct (pX-aX, pY-aY, bX-aX, bY-aY);
        return uv/norm(aX-bX, aY-bY);
    }


}
