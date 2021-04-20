package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Représentation objet d'une carte du jeu de Jass
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class Card {
    // la carte sous forme empaquetée
    private final int packedCard;

    private Card(int packedCard) {
        this.packedCard = packedCard;
    }

    /**
     * Retourne la carte dont packed est la version empaquetée
     * 
     * @param packed
     *            la carte sous la forme empaquetée
     * @throws IllegalArgumentException
     *             si la carte empaquetée n'est pas valide.
     * @return retourne la carte dont packed est la version empaquetée
     */
    public static Card ofPacked(int packed) {
        if (!PackedCard.isValid(packed))
            throw new IllegalArgumentException();
        else
            return new Card(packed);
    }

    /**
     * Représente la couleur d'une carte.
     * 
     * @author Mohamed Saad Eddine El Moutaouakil (284843)
     * @author Taha Zakariya (288526)
     *
     */
    public enum Color {
        SPADE("\u2660"), HEART("\u2661"), DIAMOND("\u2662"), CLUB("\u2663");
        // Code du caractère représentant la couleur de la carte (en
        // hexadécimal)
        private String charCode;

        /**
         * Représente le nombre de couleurs
         */
        public static final int COUNT = 4;

        /**
         * Liste immuable contenant toutes les couleurs dans l'ordre de
         * déclaration
         */
        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));

        private Color(String codeColor) {
            charCode = codeColor;
        };

        /*
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return charCode;
        }
    }

    /**
     * Représente le rang d'une carte.
     * 
     * @author Mohamed Saad Eddine El Moutaouakil (284843)
     * @author Taha Zakariya (288526)
     *
     */
    public enum Rank {
        SIX("6", 0), SEVEN("7", 1), EIGHT("8", 2), NINE("9", 7), TEN("10",
                3), JACK("J", 8), QUEEN("Q", 4), KING("K", 5), ACE("A", 6);
        // Rang de la carte
        private String rank;
        // L'ordre de la carte d'atout selon le classement de ces dernières
        private int trumpOrder;
        /**
         * Représente le nombre de rangs
         */
        public static final int COUNT = 9;
        /**
         * Liste immuable de tous les rangs selon l'ordre de déclaration
         */
        public static final List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));

        private Rank(String code, int trumpOrder) {
            rank = code;
            this.trumpOrder = trumpOrder;
        }

        /**
         * Retourne la position, comprise entre 0 et 8, de la carte d'atout
         * ayant ce rang dans l'ordre des cartes d'atout
         * 
         * @return la position de la carte d'atout
         */
        public int trumpOrdinal() {
            return trumpOrder;
        }

        /*
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return this.rank;
        }

    }

    /**
     * Retourne la carte de couleur et de rang donnés
     * 
     * @param c
     *            couleur de la carte
     * @param r
     *            rang de la carte
     * @return la carte de couleur c et de rang r
     * 
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }

    /**
     * Retourne la version empaquetée de la carte
     * 
     * @return la version empaquetée de la carte
     */
    public int packed() {
        return packedCard;
    }

    /**
     * Retourne la couleur de la carte à partir de la version empaquetée
     * 
     * @return la couleur de la carte
     */
    public Color color() {
        return PackedCard.color(packedCard);
    }

    /**
     * Retourne le rang de la carte à partir de la version empaquetée
     * 
     * @return le rang de la carte
     */
    public Rank rank() {
        return PackedCard.rank(packedCard);
    }

    /**
     * Vérifie que le recépteur est supérieur à la carte passée en argument
     * 
     * @param trump
     *            couleur d'atout
     * @param that
     *            la carte à laquelle sera comparé le récepteur
     * @return vrai si le récepteur est supérieur à that , false sinon
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, packedCard, that.packedCard);
    }

    /**
     * Retourne la valeur de la carte, sachant que l'atout est trump
     * 
     * @param trump
     *            la couleur d'atout
     * @return le nombre de points que rapporte la carte
     */
    public int points(Color trump) {
        return PackedCard.points(trump, packedCard);
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO != null && thatO.getClass() == Card.class) {
            if (((Card) thatO).packedCard == this.packedCard) {
                return true;
            } else
                return false;
        } else
            return false;
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return packed();
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedCard.toString(packedCard);
    }
}
