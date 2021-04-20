package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Représente un ensemble de cartes
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 */
public final class CardSet {

    /**
     * l'ensemble de cartes vide.
     */
    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    /**
     * l'ensemble des 36 cartes du jeu de Jass.
     */
    public static final CardSet ALL_CARDS = new CardSet(
            PackedCardSet.ALL_CARDS);
    private final long packedCardSet;

    private CardSet(long pkCardSet) {
        packedCardSet = pkCardSet;
    }

    /**
     * retourne l'ensemble des cartes contenues dans la liste donnée
     * 
     * @param cards
     *            liste de cartes
     * @return l'ensemble des cartes contenues dans la liste donnée
     * 
     */
    public static CardSet of(List<Card> cards) {
        long cardSet = PackedCardSet.EMPTY;
        for (Card c : cards) {
            cardSet = PackedCardSet.add(cardSet, c.packed());
        }
        return new CardSet(cardSet);
    }

    /**
     * retourne l'ensemble de cartes dont packed est la version empaquetée
     * 
     * @param packed
     *            l'ensemble de cartes empaquetée
     * @throws IllegalArgumentException
     *             si l'ensemble empaqueté n'est pas valide
     * @return l'ensemble de cartes dont packed est la version empaquetée
     * 
     */
    public static CardSet ofPacked(long packed) {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }

    /**
     * retourne la version empaquetée de l'ensemble de cartes.
     * 
     * @return la version empaquetée de l'ensemble de cartes.
     */
    public long packed() {
        return packedCardSet;
    }

    /**
     * Vérifie si l'ensemble est vide
     * 
     * @return vrai ssi l'ensemble de cartes empaqueté donné est vide
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(packedCardSet);
    }

    /**
     * retourne le nombre de cartes que l'ensemble de cartes empaqueté contient
     * 
     * @return le nombre de cartes que l'ensemble de cartes empaqueté contient
     */
    public int size() {
        return PackedCardSet.size(packedCardSet);
    }

    /**
     * retourne la carte d'index donné depuis l'ensemble récepteur
     * 
     * @param index
     *            indice de la carte. 0 représente la première carte de
     *            l'ensemble
     * @return la carte d'index donné
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(packedCardSet, index));
    }

    /**
     * retourne l'ensemble de cartes récepteur auquel la carte donnée a été
     * ajoutée
     * 
     * @param card
     *            la carte donnée
     * @return l'ensemble de cartes récepteur auquel la carte donnée a été
     *         ajoutée
     */
    public CardSet add(Card card) {
        return new CardSet(PackedCardSet.add(packedCardSet, card.packed()));
    }

    /**
     * retourne l'ensemble de cartes récepteur auquel la carte donnée a été
     * enlevée
     * 
     * @param card
     *            la carte donnée
     * @return l'ensemble de cartes récepteur auquel la carte donnée a été
     *         enlevée
     */
    public CardSet remove(Card card) {
        return new CardSet(PackedCardSet.remove(packedCardSet, card.packed()));
    }

    /**
     * vérifie si l'ensemble de cartes récepteur contient la carte donnée
     * 
     * @param card
     *            la carte donnée
     * @return retourne vrai ssi l'ensemble de cartes récepteur contient la
     *         carte donnée
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(packedCardSet, card.packed());
    }

    /**
     * retourne le complément de l'ensemble de cartes récepteur
     * 
     * @return le complément de l'ensemble de cartes récepteur
     */
    public CardSet complement() {
        return new CardSet(PackedCardSet.complement(packedCardSet));
    }

    /**
     * retourne l'union de l'ensemble de cartes récepteur et de l'ensemble donné
     * 
     * @return l'union de l'ensemble de cartes récepteur et de l'ensemble donné
     */
    public CardSet union(CardSet that) {
        return new CardSet(
                PackedCardSet.union(packedCardSet, that.packedCardSet));
    }

    /**
     * retourne l'intersection de l'ensemble de cartes récepteur et de
     * l'ensemble donné
     * 
     * @return l'intersection de l'ensemble de cartes récepteur et de l'ensemble
     *         donné
     */
    public CardSet intersection(CardSet that) {
        return new CardSet(
                PackedCardSet.intersection(packedCardSet, that.packedCardSet));
    }

    /**
     * retourne la différence de l'ensemble de cartes récepteur et de l'ensemble
     * donné
     * 
     * @return la différence de l'ensemble de cartes récepteur et de l'ensemble
     *         donné
     */
    public CardSet difference(CardSet that) {
        return new CardSet(
                PackedCardSet.difference(packedCardSet, that.packedCardSet));
    }

    /**
     * retourne le sous-ensemble de l'ensemble de cartes donné constitué
     * uniquement des cartes de la couleur donnée
     * 
     * @param color
     *            la couleur donnée
     * @return le sous-ensemble de l'ensemble de cartes empaqueté donné
     *         constitué uniquement des cartes de la couleur donnée
     */
    public CardSet subsetOfColor(Card.Color color) {
        return new CardSet(PackedCardSet.subsetOfColor(packedCardSet, color));
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedCardSet.toString(packedCardSet);
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packedCardSet);
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO != null && thatO.getClass() == CardSet.class) {
            if (((CardSet) thatO).packedCardSet == this.packedCardSet) {
                return true;
            } else
                return false;
        } else
            return false;
    }
}
