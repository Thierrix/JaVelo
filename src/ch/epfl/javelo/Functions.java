package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Classe contenant de sméthodes qui permettent la création d'objets représentant des fonctions mathématiques
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class Functions {

    /**
     * Constructeur pour le rendre non instanciable
     */

    private Functions(){}

    /**
     * retourne une fonction dont la valeur est toujours constante
     *
     * @param y
     *      un nombre décimal
     * @return
     *      Une fonction constante en y
     */
    public static DoubleUnaryOperator constant(double y) {
        DoubleUnaryOperator f = new Constant(y);
        f.applyAsDouble(y);
        return f;
    }

    private static final record Constant(double y) implements DoubleUnaryOperator {

        @Override
        public double applyAsDouble(double operand) {
            return this.y;
        }
    }

    /**
     * Retourne une fonction par interpolation linéaire entre les échantillons samples, espacés régulièrement et couvrant la plage allant de 0 à xMax
     *
     * @param samples
     *      liste d'échantillons
     * @param xMax
     *      l'abcisse maximale
     * @return
     *      une fonction par interpolation linéaire entre les échantillons samples, espacés régulièrement et couvrant la plage allant de 0 à xMax
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        double step = xMax/(samples.length-1);
        DoubleUnaryOperator f = new Sampled(samples,xMax);
        for (double i = 0; i<=xMax; i += step) {
            f.applyAsDouble(i);
        }
        return f;
    }

    private static final record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {

        @Override
        public double applyAsDouble(double operand) {
            if (operand<=0)
                return samples[0];
            if (operand>=xMax)
                return samples[samples.length-1];
            double step = xMax/(samples.length-1);
            double x = (operand - Math.floor(operand/step)*step)/step;
            return Math2.interpolate(samples[(int)Math.floor(operand/step)], samples[(int)Math.ceil(operand/step)], x);
        }
    }
}
