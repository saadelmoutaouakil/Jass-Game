package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * Contient des méthodes statiques permettant de manipuler des cartes d'un jeu
 * de Jass empaquetées dans un entier de type int
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class PackedCard {
    protected final static int RANK_INDEX_START = 0;
    protected final static int RANK_SIZE = 4;
    protected final static int COLOR_INDEX_START = 4;
    protected final static int COLOR_SIZE = 2;
    private final static int LEADING_ZEROS_INDEX_START = 6;
    private final static int LEADING_ZEROS_SIZE = 26;
    // L'élément à la position i , contient le score de la carte ayant le i'ème
    // rang dans le classement des cartes non atouts
    private final static int[] SCORES_NORMAL_CARDS = { 0, 0, 0, 0, 10, 2, 3, 4,
            11 };
    // L'élément à la position i , contient le score de la carte ayant le i'ème
    // rang dans le classement des cartes atouts
    private final static int[] SCORES_TRUMP_CARDS = { 0, 0, 0, 14, 10, 20, 3, 4,
            11 };
    /**
     * Représente une carte empaquetée invalide
     */
    public final static int INVALID = 0b111111;

    private PackedCard() {
    }

    /**
     * Vérifie que la valeur donnée correspond à celle d'une carte empaquetée
     * valide
     * 
     * @param pkCard
     *            la carte empaquetée
     * @return vrai ssi la valeur donnée est une carte empaquetée valide c-à-d
     *         si les bits contenant le rang contiennent une valeur comprise
     *         entre 0 et 8 (inclus) et si les bits inutilisés valent tous 0
     */
    public static boolean isValid(int pkCard) {
        if (Bits32.extract(pkCard, RANK_INDEX_START, RANK_SIZE) >= 0
                && Bits32.extract(pkCard, RANK_INDEX_START, RANK_SIZE) <= 8
                && Bits32.extract(pkCard, LEADING_ZEROS_INDEX_START,
                        LEADING_ZEROS_SIZE) == 0)
            return true;
        else
            return false;
    }

    /**
     * Retourne la carte empaquetée de couleur et rang donnés
     * 
     * @param c
     *            couleur de la carte
     * @param r
     *            rang de la carte
     * @return la carte empaquetée de couleur et rang donnés
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), RANK_SIZE, c.ordinal(), COLOR_SIZE);
    }

    /**
     * retourne la couleur de la carte empaquetée donnée
     * 
     * @param pkCard
     *            la carte empaquetée
     * @return la couleur de la carte empaquetée donnée
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);
        return Card.Color.ALL
                .get(Bits32.extract(pkCard, COLOR_INDEX_START, COLOR_SIZE));
    }

    /**
     * retourne le rang de la carte empaquetée donnée
     * 
     * @param pkCard
     *            la carte empaquetée
     * @return le rang de la carte empaquetée donnée
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);
        return Card.Rank.ALL
                .get(Bits32.extract(pkCard, RANK_INDEX_START, RANK_SIZE));
    }

    /**
     * Vérifie que la première carte donnée en argument est supérieure à la
     * deuxième carte.
     * 
     * @param trump
     *            couleur d'atout
     * @param pkCardL
     *            première carte à comparer sous la forme empaquetée
     * @param pkCardR
     *            deuxième carte à comparer sous la forme empaquetée
     * @return vrai ssi la première carte donnée est supérieure à la seconde,
     *         sachant que l'atout est trump Faux si la deuxième carte est
     *         supérieure à la première , ou si elles sont incompatibles
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        int colorL = Bits32.extract(pkCardL, COLOR_INDEX_START, COLOR_SIZE);
        int colorR = Bits32.extract(pkCardR, COLOR_INDEX_START, COLOR_SIZE);
        if (colorL == colorR) {
            if ((Card.Color.ALL.get(colorL).equals(trump))) {
                return rank(pkCardL).trumpOrdinal() > rank(pkCardR)
                        .trumpOrdinal();
            } else
                return rank(pkCardL).ordinal() > rank(pkCardR).ordinal();
        } else if ((Card.Color.ALL.get(colorL).equals(trump))) {
            return true;
        } else
            return false;
    }

    /**
     * Retourne la valeur de la carte, sachant que l'atout est trump.
     * 
     * @param trump
     *            la couleur d'atout
     * @param pkCard
     *            la carte empaquetée
     * @return le nombre de points que rapporte la carte
     */
    public static int points(Card.Color trump, int pkCard) {
        int color = Bits32.extract(pkCard, COLOR_INDEX_START, COLOR_SIZE);
        int rank = Bits32.extract(pkCard, RANK_INDEX_START, RANK_SIZE);
        if ((Card.Color.ALL.get(color).equals(trump))) {
            return SCORES_TRUMP_CARDS[rank];
        } else
            return SCORES_NORMAL_CARDS[rank];
    }

    /**
     * Retourne une représentation de la carte empaquetée donnée sous forme de
     * chaîne de caractères composée du symbole de la couleur et du nom abrégé
     * du rang
     * 
     * @param pkCard
     *            la carte empaquetée
     * @return la représentation de la carte empaquetée donnée sous forme de
     *         chaîne de caractères composée du symbole de la couleur et du nom
     *         abrégé du rang
     */
    public static String toString(int pkCard) {
        return color(pkCard).toString() + rank(pkCard).toString();
    }
}
