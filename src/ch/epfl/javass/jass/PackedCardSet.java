package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Contient des méthodes permettant de manipuler des ensembles de cartes
 * empaquetés dans des valeurs de type long (64 bits)
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class PackedCardSet {
    private final static int UNUSED_BITS_SIZE = 7;
    private final static int SPADE_INDEX_START = 0;
    private final static int HEART_INDEX_START = 16;
    private final static int DIAMOND_INDEX_START = 32;
    private final static int CLUB_INDEX_START = 48;
    private final static int CARDS_SIZE = 9;
    private final static int BITS_PER_COLOR = 16;

    /**
     * l'ensemble de cartes vide
     */
    public static final long EMPTY = 0L;
    /**
     * l'ensemble des 36 cartes du jeu de Jass
     */
    public static final long ALL_CARDS = 0b0000000111111111_0000000111111111_0000000111111111_0000000111111111L;
    private static final long[][] TRUMP_ABOVE_VALUES = trumpAboveValues();
    private static final long[] SUBSETS_OF_COLOR = subsetOfColorValues();

    private PackedCardSet() {
    }

    /**
     * Vérifie que l'ensemble de cartes empaqueté donnée est valide.
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @return vrai ssi la valeur donnée représente un ensemble de cartes
     *         empaqueté valide, c-à-d si aucun des 28 bits inutilisés ne vaut 1
     */
    public static boolean isValid(long pkCardSet) {
        long firstSevenUnusedBits = Bits64.extract(pkCardSet,
                SPADE_INDEX_START + CARDS_SIZE, UNUSED_BITS_SIZE);
        long secondSevenUnusedBits = Bits64.extract(pkCardSet,
                HEART_INDEX_START + CARDS_SIZE, UNUSED_BITS_SIZE);
        long thirdSevenUnusedBits = Bits64.extract(pkCardSet,
                DIAMOND_INDEX_START + CARDS_SIZE, UNUSED_BITS_SIZE);
        long fourthSevenUnusedBits = Bits64.extract(pkCardSet,
                CLUB_INDEX_START + CARDS_SIZE, UNUSED_BITS_SIZE);
        if (firstSevenUnusedBits == 0 && secondSevenUnusedBits == 0
                && thirdSevenUnusedBits == 0 && fourthSevenUnusedBits == 0) {
            return true;
        } else
            return false;
    }

    /**
     * retourne l'ensemble des cartes strictement plus fortes que la carte
     * empaquetée donnée, sachant qu'il s'agit d'une carte d'atout
     * 
     * @param pkCard
     *            l'ensemble de cartes empaqueté
     * @return l'ensemble des cartes strictement plus fortes que la carte
     *         empaquetée donnée, sachant qu'il s'agit d'une carte d'atout
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        int color = Bits32.extract(pkCard, PackedCard.COLOR_INDEX_START,
                PackedCard.COLOR_SIZE);
        int rank = Bits32.extract(pkCard, PackedCard.RANK_INDEX_START,
                PackedCard.RANK_SIZE);
        return TRUMP_ABOVE_VALUES[color][rank];
    }

    /**
     * retourne l'ensemble de cartes empaqueté contenant uniquement la carte
     * empaquetée donnée
     * 
     * @param pkCard
     *            la carte empaquetée donnée
     * @return l'ensemble de cartes empaqueté contenant uniquement la carte
     *         empaquetée donnée
     */
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return 1L << pkCard;
    }

    /**
     * Vérifie si l'ensemble de cartes empaqueté est vide
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @return retourne vrai ssi l'ensemble de cartes empaqueté donné est vide
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValid(pkCardSet);
        if (pkCardSet == EMPTY)
            return true;
        else
            return false;
    }

    /**
     * retourne la taille de l'ensemble de cartes empaqueté donné, c-à-d le
     * nombre de cartes qu'il contient
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @return la taille de l'ensemble de cartes empaqueté donné
     */
    public static int size(long pkCardSet) {
        assert isValid(pkCardSet);
        return Long.bitCount(pkCardSet);
    }

    /**
     * retourne la version empaquetée de la carte d'index donné de l'ensemble de
     * cartes empaqueté donné
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @param index
     *            indice de la carte. 0 représente la première carte de
     *            l'ensemble de cartes empaqueté donné
     * @return la version empaquetée de la carte d'index donné de l'ensemble de
     *         cartes empaqueté donné
     */
    public static int get(long pkCardSet, int index) {
        assert isValid(pkCardSet) && pkCardSet != EMPTY
                && Long.bitCount(pkCardSet) > index && index >= 0;

        int counter = 0;
        while (counter < index) {
            pkCardSet = pkCardSet & (~Long.lowestOneBit(pkCardSet));
            ++counter;
        }
        pkCardSet = Long.lowestOneBit(pkCardSet);
        int rank = Long.numberOfTrailingZeros(pkCardSet) % BITS_PER_COLOR;
        int color = ((int) Long.numberOfTrailingZeros(pkCardSet)
                / BITS_PER_COLOR);
        return Bits32.pack(rank, PackedCard.RANK_SIZE, color,
                PackedCard.COLOR_SIZE);

    }

    /**
     * retourne l'ensemble de cartes empaqueté donné auquel la carte empaquetée
     * donnée a été ajoutée
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @param pkCard
     *            la carte empaquetée donnée
     * @return l'ensemble de cartes empaqueté donné auquel la carte empaquetée
     *         donnée a été ajoutée
     */
    public static long add(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);
        return pkCardSet | singleton(pkCard);
    }

    /**
     * retourne l'ensemble de cartes empaqueté donné duquel la carte empaquetée
     * donnée a été supprimée
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @param pkCard
     *            la carte empaquetée donnée
     * @return l'ensemble de cartes empaqueté donné duquel la carte empaquetée
     *         donnée a été supprimée
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);
        return pkCardSet & (~singleton(pkCard));
    }

    /**
     * vérifie si l'ensemble de cartes empaqueté donné contient la carte
     * empaquetée donnée
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @param pkCard
     *            la carte empaquetée donnée
     * @return vrai ssi l'ensemble de cartes empaqueté donné contient la carte
     *         empaquetée donnée
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet) && PackedCard.isValid(pkCard);
        return (pkCardSet & singleton(pkCard)) != 0;
    }

    /**
     * retourne le complément de l'ensemble de cartes empaqueté donné
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @return le complément de l'ensemble de cartes empaqueté donné
     */
    public static long complement(long pkCardSet) {
        assert isValid(pkCardSet);
        return (~pkCardSet) & ALL_CARDS;
    }

    /**
     * retourne l'union des deux ensembles de cartes empaquetés donnés
     * 
     * @param pkCardSet1
     *            le premier ensemble de cartes empaqueté
     * @param pkCardSet2
     *            le second ensemble de cartes empaqueté
     * @return l'union des deux ensembles de cartes empaquetés donnés
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return pkCardSet1 | pkCardSet2;
    }

    /**
     * retourne l'intersection des deux ensembles de cartes empaquetés donnés
     * 
     * @param pkCardSet1
     *            le premier ensemble de cartes empaqueté
     * @param pkCardSet2
     *            le second ensemble de cartes empaqueté
     * @return l'intersection des deux ensembles de cartes empaquetés donnés
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return pkCardSet1 & pkCardSet2;
    }

    /**
     * retourne la différence entre le premier ensemble de cartes empaqueté
     * donné et le second
     * 
     * @param pkCardSet1
     *            le premier ensemble de cartes empaqueté
     * @param pkCardSet2
     *            le second ensemble de cartes empaqueté
     * @return la différence entre le premier ensemble de cartes empaqueté donné
     *         et le second
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return intersection(pkCardSet1, complement(pkCardSet2));
    }

    /**
     * retourne le sous-ensemble de l'ensemble de cartes empaqueté donné
     * constitué uniquement des cartes de la couleur donnée
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @param color
     *            la couleur donnée
     * @return le sous-ensemble de l'ensemble de cartes empaqueté donné
     *         constitué uniquement des cartes de la couleur donnée
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValid(pkCardSet);
        return pkCardSet & SUBSETS_OF_COLOR[color.ordinal()];
    }

    /**
     * retourne une chaîne constituée de la représentation textuelle de chacune
     * des cartes de l'ensemble, ordonnées par index croissant, séparées par une
     * virgule et entourées d'accolades
     * 
     * @param pkCardSet
     *            l'ensemble de cartes empaqueté
     * @return la représentation textuelle de l'ensemble de cartes empaqueté
     *         donné
     */
    public static String toString(long pkCardSet) {
        StringJoiner j = new StringJoiner(",", "{", "}");
        Card[] tabCards = new Card[Long.bitCount(pkCardSet)];
        for (int i = 0; i < Long.bitCount(pkCardSet); ++i) {
            tabCards[i] = Card.ofPacked(get(pkCardSet, i));
            j.add(String.valueOf(tabCards[i]));
        }
        return j.toString();
    }

    private static long[][] trumpAboveValues() {
        long[][] trumpAboveValues = new long[Color.COUNT][Rank.COUNT];
        long betterThanSix = 0b111111110L;
        long betterThanSeven = 0b111111100L;
        long betterThanEight = 0b111111000L;
        long betterThanNine = 0b000100000L;
        long betterThanTen = 0b111101000L;
        long betterThanJack = 0L;
        long betterThanQueen = 0b110101000L;
        long betterThanKing = 0b100101000L;
        long betterThanAce = 0b000101000L;
        long[] values = { betterThanSix, betterThanSeven, betterThanEight,
                betterThanNine, betterThanTen, betterThanJack, betterThanQueen,
                betterThanKing, betterThanAce };
        for (int i = 0; i < trumpAboveValues.length; ++i) {
            for (int j = 0; j < trumpAboveValues[0].length; ++j) {
                trumpAboveValues[i][j] = values[j] << (16 * i);
            }
        }
        return trumpAboveValues;
    }

    private static long[] subsetOfColorValues() {
        long[] subsetOfColorValues = new long[Color.COUNT];
        long allCardsOfAColor = 0b111111111L;
        for (int i = 0; i < subsetOfColorValues.length; ++i) {
            subsetOfColorValues[i] = allCardsOfAColor << 16 * i;
        }
        return subsetOfColorValues;

    }
}
