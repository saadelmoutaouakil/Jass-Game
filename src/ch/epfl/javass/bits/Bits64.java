package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Contient des méthodes statiques permettant de travailler sur des vecteurs de
 * 64 bits stockés dans des valeurs de type long
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class Bits64 {

    private Bits64() {
    }

    /**
     * Retourne un entier dont les bits d'index allant de start (inclus) à start
     * + size (exclus) valent 1, les autres valant 0
     * 
     * @param start
     *            L'indice du premier bit valant 1
     * @param size
     *            Le nombre de bits valant 1
     * @throws IllegalArgumentException
     *             si start et size ne désignent pas une plage de bits valide
     *             (c-à-d comprise entre 0 et 64, inclus , et leur somme aussi)
     * @return Retourne un entier dont les bits d'index allant de start (inclus)
     *         à start + size (exclus) valent 1, les autres valant 0
     */
    public static long mask(int start, int size) {
        Preconditions.checkArgument(hasTheRightBounds(start, 0, Long.SIZE)
                && hasTheRightBounds(size, 0, Long.SIZE)
                && hasTheRightBounds(start + size, 0, Long.SIZE));
        if (size == Long.SIZE)
            return -1L;
        else
            return ((1L << size) - 1) << start;
    }

    /**
     * Retourne une valeur dont les size bits de poids faible sont égaux à ceux
     * de bits allant de l'index start (inclus) à l'index start + size (exclus)
     * 
     * @param bits
     *            le vecteur initial
     * @param start
     *            La position du premier bit à extraire
     * @param size
     *            Le nombre de bits à extraire
     * @throws IllegalArgumentException
     *             si start et size ne désignent pas une plage de bits valide
     * @return retourne une valeur dont les size bits de poids faible sont égaux
     *         à ceux de bits allant de l'index start (inclus) à l'index start +
     *         size (exclus)
     */
    public static long extract(long bits, int start, int size) {
        Preconditions.checkArgument(hasTheRightBounds(start, 0, Long.SIZE)
                && hasTheRightBounds(size, 0, Long.SIZE)
                && hasTheRightBounds(start + size, 0, Long.SIZE));

        return (bits << Long.SIZE - size - start) >>> Long.SIZE - size;
    }

    /**
     * retourne les valeurs v1 et v2 empaquetées dans un entier de type long, v1
     * occupant les s1 bits de poids faible, et v2 occupant les s2 bits
     * suivants, tous les autres bits valant 0 
     * 
     * @param v1
     *            La valeur à empaqueter dans les bits de poids faible
     * @param s1
     *            La taille de v1
     * @param v2
     *            La valeur à empaqueter dans les bits suivant v1
     * @param s2
     *            La taille de v2
     * @return retourne les valeurs v1 et v2 empaquetées
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        Preconditions.checkArgument(hasTheRightBoundsForPacking(v1, s1)
                && hasTheRightBoundsForPacking(v2, s2)
                && hasTheRightBounds(s1 + s2, 0, Long.SIZE));
        return (v2 << s1) | v1;
    }

    private static boolean hasTheRightBounds(int number, int lowerBound,
            int upperBound) {
        return number >= lowerBound && number <= upperBound;
    }

    /*
     * Vérifie que la taille s de l'entier v est correcte , et retourne vrai ou
     * faux selon la valeur de s en prennant en considération les deux cas
     * limites : s=63 bits dû à la representation signée des long, et s=64 bits
     * dû au décalage qui est modulo 64 pour le type long.
     * 
     */
    private static boolean hasTheRightBoundsForPacking(long v, int s) {
        if (hasTheRightBounds(s, 1, Long.SIZE - 2) && (1L << s) > v) {
            return true;
        }
        if (s == Long.SIZE - 1 && ~(1L << s) > v)
            return true;
        if (s == Long.SIZE)
            return true;
        else
            return false;
    }
}
