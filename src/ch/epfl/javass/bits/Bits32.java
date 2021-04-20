package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Contient des méthodes statiques permettant de travailler sur des vecteurs de
 * 32 bits stockés dans des valeurs de type int
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class Bits32 {

    private Bits32() {
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
     *             (c-à-d comprise entre 0 et 32, inclus , et leur somme aussi)
     * @return Retourne un entier dont les bits d'index allant de start (inclus)
     *         à start + size (exclus) valent 1, les autres valant 0
     */
    public static int mask(int start, int size) {
        Preconditions.checkArgument(hasTheRightBounds(start, 0, Integer.SIZE)
                && hasTheRightBounds(size, 0, Integer.SIZE)
                && hasTheRightBounds(start + size, 0, Integer.SIZE));
        if (size == Integer.SIZE)
            return -1;
        else
            return ((1 << size) - 1) << start;

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
    public static int extract(int bits, int start, int size) {
        Preconditions.checkArgument(hasTheRightBounds(start, 0, Integer.SIZE)
                && hasTheRightBounds(size, 0, Integer.SIZE)
                && hasTheRightBounds(start + size, 0, Integer.SIZE));

        return (bits << Integer.SIZE - size - start) >>> Integer.SIZE - size;

    }

    /**
     * retourne les valeurs v1 et v2 empaquetées dans un entier de type int, v1
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
    public static int pack(int v1, int s1, int v2, int s2) {
        Preconditions.checkArgument(hasTheRightBoundsForPacking(v1, s1)
                && hasTheRightBoundsForPacking(v2, s2)
                && hasTheRightBounds(s1 + s2, 0, Integer.SIZE));

        return (v2 << s1) | v1;

    }

    /**
     * retourne les valeurs v1,v2 et v3 empaquetées dans un entier de type int,
     * v1 occupant les s1 bits de poids faible, v2 occupant les s2 bits suivants
     * et v3 occupant les s3 bits suivants tous les autres bits valant 0
     * 
     * @param v1
     *            La valeur à empaqueter dans les bits de poids faible
     * @param s1
     *            La taille de v1
     * @param v2
     *            La valeur à empaqueter dans les bits suivant v1
     * @param s2
     *            La taille de v2
     * @param v3
     *            La valeur à empaqueter dans les bits suivant v2
     * @param s3
     *            La taille de s3
     * @return retourne les valeurs v1,v2 et v3 empaquetées
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        Preconditions.checkArgument(hasTheRightBoundsForPacking(v1, s1)
                && hasTheRightBoundsForPacking(v2, s2)
                && hasTheRightBoundsForPacking(v3, s3)
                && hasTheRightBounds(s1 + s2 + s3, 0, Integer.SIZE));

        return pack(pack(v1, s1, v2, s2), s1 + s2, v3, s3);

    }

    /**
     * retourne les valeurs de v1 à v6 empaquetées dans un entier de type int,
     * v1 occupant les s1 bits de poids faible, v2 occupant les s2 bits suivants
     * , etc jusqu'à v6 occupant les s6 bits suivants v5 tous les autres bits
     * valant 0
     * 
     * @param v1
     *            La valeur à empaqueter dans les bits de poids faible
     * @param s1
     *            La taille de v1
     * @param v2
     *            La valeur à empaqueter dans les bits suivant v1
     * @param s2
     *            La taille de v2
     * @param v3
     *            La valeur à empaqueter dans les bits suivant v2
     * @param s3
     *            La taille de s3
     * @param v4
     *            La valeur à empaqueter dans les bits suivant v3
     * @param s4
     *            La taille de v4
     * @param v5
     *            La valeur à empaqueter dans les bits suivant v4
     * @param s5
     *            La taille de v5
     * @param v6
     *            La valeur à empaqueter dans les bits suivant v5
     * @param s6
     *            La taille de v6
     * @param v7
     *            La valeur à empaqueter dans les bits suivant v6
     * @param s7
     *            La taille de v7
     * @return
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        Preconditions.checkArgument(hasTheRightBoundsForPacking(v1, s1)
                && hasTheRightBoundsForPacking(v2, s2)
                && hasTheRightBoundsForPacking(v3, s3)
                && hasTheRightBoundsForPacking(v4, s4)
                && hasTheRightBoundsForPacking(v5, s5)
                && hasTheRightBoundsForPacking(v6, s6)
                && hasTheRightBoundsForPacking(v7, s7) && hasTheRightBounds(
                        s1 + s2 + s3 + s4 + s5 + s6 + s7, 0, Integer.SIZE));

        return pack(pack(v1, s1, v2, s2, v3, s3), s1 + s2 + s3,
                pack(v4, s4, v5, s5, v6, s6), s4 + s5 + s6, v7, s7);

    }

    private static boolean hasTheRightBounds(int number, int lowerBound,
            int upperBound) {
        return number >= lowerBound && number <= upperBound;
    }

    /*
     * Vérifie que la taille s de l'entier v est correcte , et retourne vrai ou
     * faux selon la valeur de s en prennant en considération les deux cas
     * limites : s=31bits dû à la representation signée des ints, et s=32 bits
     * dû au décalage qui est modulo 32 pour le type int.
     * 
     */
    private static boolean hasTheRightBoundsForPacking(int v, int s) {
        if (hasTheRightBounds(s, 1, Integer.SIZE - 2) && (1 << s) > v) {
            return true;
        }
        if (s == Integer.SIZE - 1 && ~(1 << s) > v)
            return true;
        if (s == Integer.SIZE)
            return true;
        else
            return false;
    }

}
