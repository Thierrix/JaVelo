package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 * Enregeristrement représentant un ensemble d'attributs OSM
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public record AttributeSet(long bits) {

    /**
     *Constructeur compact
     **/

    public AttributeSet {
        Preconditions.checkArgument((bits & (1L << (Attribute.COUNT+1) | 1L << Attribute.COUNT)) == 0);
    }

    /**
     * Retourne un ensemble contenant uniquement les attributs donnés en argument
     *
     * @param attributes
     *      une surchage d'attributs
     * @return
     *      un ensemble contenant uniquement les attributs donnés en argument
     */

    public static AttributeSet of(Attribute... attributes) {
        long x = 0;
        for (Attribute a : attributes) {
            long mask = 1L << a.ordinal();
            x = mask | x;
        }
        return new AttributeSet(x);
    }

    /**
     * Retourne un ensemble contenant uniquement les attributs donnés en argument
     *
     * @param attribute
     *      l'attribut donné
     * @return
     *       un ensemble contenant uniquement les attributs donnés en argument
     */
    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();
        return ((mask & bits) == mask);
    }

    /**
     * Retourne true s'il y a une intersection en commun
     *
     * @param that
     *      un ensemble d'attributs
     * @return
     *      true s'il y a une intersection en commun
     */

    public boolean intersects(AttributeSet that) {
        return ((that.bits & this.bits) != 0);
    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (Attribute attr: Attribute.values()) {
            if (this.contains(attr)) {
                j.add(attr.toString());
            }
        }
        return j.toString();
    }

}
