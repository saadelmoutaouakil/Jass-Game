package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Représente un pli
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class Trick {

    /**
     * représente un pli invalide
     */
    public final static Trick INVALID = new Trick(PackedTrick.INVALID);
    private final int packedTrick;

    private Trick(int packedTrick) {
        this.packedTrick = packedTrick;
    }

    /**
     * retourne le pli vide — c-à-d sans aucune carte — d'index 0 avec l'atout
     * et le premier joueur donnés
     * 
     * @param trump
     *            l'atout
     * @param firstPlayer
     *            le premier joueur du pli
     * @return pli vide d'index 0 avec l'atout et le premier joueur donnés
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * retourne le pli dont la version empaquetée est celle donnée
     * 
     * @param packed
     *            pli empaqueté
     * @throws IllegalArgumentException
     *             si le pli n'est pas valide (selon PackedTrick.isValid)
     * @return le pli dont la version empaquetée est celle donnée
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * retourne la version empaquetée du pli
     * 
     * @return la version empaquetée du pli
     */
    public int packed() {
        return packedTrick;
    }

    /**
     * retourne le pli empaqueté vide suivant celui donné (supposé plein)
     * 
     * @throws IllegalStateException
     *             si le pli n'est pas plein
     * @return le pli vide dont l'atout est identique à celui du pli donné,
     *         l'index est le successeur de celui du pli donné et le premier
     *         joueur est le vainqueur du pli donné ; si le pli donné est le
     *         dernier du tour, alors le pli invalide est retourné,
     */
    public Trick nextEmpty() {
        if (!PackedTrick.isFull(packedTrick))
            throw new IllegalStateException();
        return new Trick(PackedTrick.nextEmpty(packedTrick));
    }

    /**
     * vérifie si le pli est vide
     * 
     * @return retourne vrai ssi le pli est vide, c-à-d s'il ne contient aucune
     *         carte
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(packedTrick);
    }

    /**
     * vérifie si le pli est plein
     * 
     * @return retourne vrai ssi le pli est plein, c-à-d s'il contient 4 cartes
     */
    public boolean isFull() {
        return PackedTrick.isFull(packedTrick);
    }

    /**
     * vérifie si le pli est le dernier du tour
     * 
     * @return retourne vrai ssi le pli est le dernier du tour, c-à-d si son
     *         index vaut 8
     */
    public boolean isLast() {
        return PackedTrick.isLast(packedTrick);
    }

    /**
     * retourne la taille du pli, c-à-d le nombre de cartes qu'il contient
     * 
     * @return la taille du pli
     */
    public int size() {
        return PackedTrick.size(packedTrick);
    }

    /**
     * retourne l'atout du pli
     * 
     * @return l'atout du pli
     */
    public Color trump() {
        return PackedTrick.trump(packedTrick);
    }

    /**
     * retourne l'index du pli
     * 
     * @return l'index du pli
     */
    public int index() {
        return PackedTrick.index(packedTrick);
    }

    /**
     * retourne le joueur d'index donné dans le pli, le joueur d'index 0 étant
     * le premier du pli
     * 
     * @param index
     *            l'index du joueur
     * @throws IndexOutOfBoundsException
     *             si l'index n'est pas compris entre 0 (inclus) et 4 (exclus)
     * @return le joueur d'index donné dans le pli
     */
    public PlayerId player(int index) {
        Preconditions.checkIndex(index, 4);
        return PackedTrick.player(packedTrick, index);
    }

    /**
     * retourne la version objet de la carte du pli à l'index donné (supposée
     * avoir été posée)
     * 
     * @param index
     *            index de la carte
     * @throws IndexOutOfBoundsException
     *             si l'index n'est pas compris entre 0 (inclus) et la taille du
     *             pli (exclus)
     * @return la version objet de la carte du pli à l'index donné
     */
    public Card card(int index) {
        Preconditions.checkIndex(index, size());
        return Card.ofPacked(PackedTrick.card(packedTrick, index));
    }

    /**
     * retourne un pli identique à celui donné (supposé non plein), mais à
     * laquelle la carte donnée a été ajoutée
     * 
     * @param c
     *            la carte à ajouter
     * @throws IllegalStateException
     *             si le pli est plein
     * @return un pli identique à celui donné mais à laquelle la carte donnée a
     *         été ajoutée
     */
    public Trick withAddedCard(Card c) {
        if (isFull())
            throw new IllegalStateException();
        return new Trick(PackedTrick.withAddedCard(packedTrick, c.packed()));
    }

    /**
     * retourne la couleur de base du pli, c-à-d la couleur de sa première carte
     * (supposée avoir été jouée)
     * 
     * @throws IllegalStateException
     *             si le pli est vide
     * @return la couleur de base du pli
     */
    public Color baseColor() {
        if (isEmpty())
            throw new IllegalStateException();
        return PackedTrick.baseColor(packedTrick);
    }

    /**
     * retourne le sous-ensemble des cartes de la main qui peuvent être jouées
     * comme prochaine carte du pli
     * 
     * @param hand
     *            la main du joueur
     * @throws IllegalStateException
     *             si le pli est plein
     * @return le sous-ensemble des cartes de la main qui peuvent être jouées
     *         comme prochaine carte du pli
     */
    public CardSet playableCards(CardSet hand) {
        if (isFull())
            throw new IllegalStateException();
        return CardSet.ofPacked(
                PackedTrick.playableCards(packedTrick, hand.packed()));
    }

    /**
     * retourne la valeur du pli, en tenant compte des « 5 de der »
     * 
     * @return la valeur du pli, en tenant compte des « 5 de der »
     */
    public int points() {
        return PackedTrick.points(packedTrick);
    }

    /**
     * retourne l'identité du joueur menant le pli
     * 
     * @throws IllegalStateException
     *             si le pli est vide
     * @return l'identité du joueur menant le pli
     */
    public PlayerId winningPlayer() {
        if (isEmpty())
            throw new IllegalStateException();
        return PackedTrick.winningPlayer(packedTrick);
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedTrick.toString(packedTrick);
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return packedTrick;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO != null && thatO.getClass() == Trick.class) {
            return (((Trick) thatO).packedTrick == this.packedTrick);

        } else
            return false;
    }
}