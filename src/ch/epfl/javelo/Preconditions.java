package ch.epfl.javelo;

/**
 * Classe qui demande à des méthodes de satisifaire certaines conditions, lève une exception sinon
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class Preconditions {

    private Preconditions() {}

    /**
     * Cette méthode check si l'argument est true ou false
     *
     * @param shouldBeTrue
     *      valeur booléenne qui doit être true
     * @throws IllegalArgumentException
     *      si son argument est faux
     */

    public static void checkArgument(boolean shouldBeTrue){

            if(!shouldBeTrue){
                throw new IllegalArgumentException();
            }

    }




}
