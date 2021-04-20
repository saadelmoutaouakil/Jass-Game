package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

/**
 * représente l'état d'un tour de jeu
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 */
public final class TurnState {

    private final long packedScore;
    private final long packedUnplayedCards;
    private final int packedTrick;

    private TurnState(long packedScore, long packedUnplayedCards,
            int packedTrick) {
        this.packedScore = packedScore;
        this.packedUnplayedCards = packedUnplayedCards;
        this.packedTrick = packedTrick;
    }

    /**
     * retourne l'état initial correspondant à un tour de jeu dont l'atout, le
     * score initial et le joueur initial sont ceux donnés
     * 
     * @param trump
     *            l'atout donné
     * @param score
     *            le score donné
     * @param firstPlayer
     *            le joueur initial donné
     * @return l'état initial correspondant à un tour de jeu dont l'atout, le
     *         score initial et le joueur initial sont ceux donnés
     */
    public static TurnState initial(Color trump, Score score,
            PlayerId firstPlayer) {
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS,
                PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * retourne l'état dont les composantes empaquetées sont celles données
     * 
     * @param pkScore
     *            score empaquetées
     * @param pkUnplayedCards
     *            l'ensemble des cartes (empaqueté) qui n'ont pas encore été
     *            jouées durant le tour
     * @param pkTrick
     *            pli empaqueté
     * @throws IllegalArgumentException
     *             si l'une des composantes empaquetées est invalide selon la
     *             méthode isValid correspondante
     * @return l'état dont les composantes empaquetées sont celles données
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) {
        if (PackedScore.isValid(pkScore)
                && PackedCardSet.isValid(pkUnplayedCards)
                && PackedTrick.isValid(pkTrick)) {
            return new TurnState(pkScore, pkUnplayedCards, pkTrick);
        } else
            throw new IllegalArgumentException();
    }

    /**
     * retourne la version empaquetée du score courant
     * 
     * @return la version empaquetée du score courant
     */
    public long packedScore() {
        return packedScore;
    }

    /**
     * retourne la version empaquetée de l'ensemble des cartes pas encore jouées
     * 
     * @return la version empaquetée de l'ensemble des cartes pas encore jouées
     */
    public long packedUnplayedCards() {
        return packedUnplayedCards;
    }

    /**
     * retourne la version empaquetée du pli courant
     * 
     * @return la version empaquetée du pli courant
     */
    public int packedTrick() {
        return packedTrick;
    }

    /**
     * retourne le score courant en version objet
     * 
     * @return le score courant en version objet
     */
    public Score score() {
        return Score.ofPacked(packedScore);
    }

    /**
     * retourne l'ensemble des cartes pas encore jouées en version objet
     * 
     * @return l'ensemble des cartes pas encore jouées en version objet
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(packedUnplayedCards);
    }

    /**
     * retourne le pli courant en version objet
     * 
     * @return le pli courant en version objet
     */
    public Trick trick() {
        return Trick.ofPacked(packedTrick);
    }

    /**
     * vérifie si l'état est terminal, c-à-d si le dernier pli du tour a été
     * joué
     * 
     * @return vrai ssi l'état est terminal, faux sinon.
     */
    public boolean isTerminal() {
        return packedTrick == PackedTrick.INVALID;

    }

    /**
     * retourne l'identité du joueur devant jouer la prochaine carte
     * 
     * @throws IllegalStateException
     *             si le pli courant est plein
     * @return l'identité du joueur devant jouer la prochaine carte
     */
    public PlayerId nextPlayer() {
        if (PackedTrick.isFull(packedTrick)) {
            throw new IllegalStateException();
        } else
            return PackedTrick.player(packedTrick,
                    PackedTrick.size(packedTrick));
    }

    /**
     * retourne l'état correspondant à celui auquel on l'applique après que le
     * prochain joueur ait joué la carte donnée
     * 
     * @param card
     *            la carte donnée
     * @throws IllegalStateException
     *             si le pli courant est plein
     * @return l'état correspondant à celui auquel on l'applique après que le
     *         prochain joueur ait joué la carte donnée
     */
    public TurnState withNewCardPlayed(Card card) {
        if (PackedTrick.isFull(packedTrick)) {
            throw new IllegalStateException();
        } else
            return new TurnState(packedScore,
                    PackedCardSet.remove(packedUnplayedCards, card.packed()),
                    PackedTrick.withAddedCard(packedTrick, card.packed()));
    }

    /**
     * retourne l'état correspondant à celui auquel on l'applique après que le
     * pli courant ait été ramassé
     * 
     * @throws IllegalStateException
     *             si le pli courant n'est pas terminé (c-à-d plein)
     * @return l'état correspondant à celui auquel on l'applique après que le
     *         pli courant ait été ramassé
     */
    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(packedTrick)) {
            throw new IllegalStateException();
        } else {
            TeamId winningTeam = PackedTrick.winningPlayer(packedTrick).team();
            int trickPoints = PackedTrick.points(packedTrick);
            return new TurnState(
                    PackedScore.withAdditionalTrick(packedScore, winningTeam,
                            trickPoints),
                    packedUnplayedCards, PackedTrick.nextEmpty(packedTrick));
        }
    }

    /**
     * retourne l'état correspondant à celui auquel on l'applique après que le
     * prochain joueur ait joué la carte donnée, et que le pli courant ait été
     * ramassé s'il est alors plein
     * 
     * @param card
     *            la carte donnée
     * @throws IllegalStateException
     *             si le pli courant est plein
     * @return retourne l'état correspondant à celui auquel on l'applique après
     *         que le prochain joueur ait joué la carte donnée, et que le pli
     *         courant ait été ramassé s'il est alors plein
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        if (PackedTrick.isFull(packedTrick)) {
            throw new IllegalStateException();
        }
        TurnState withNewCardPlayedAndTrickCollected = withNewCardPlayed(card);

        if (PackedTrick
                .isFull(withNewCardPlayedAndTrickCollected.packedTrick)) {

            withNewCardPlayedAndTrickCollected = withNewCardPlayedAndTrickCollected
                    .withTrickCollected();
        }
        return withNewCardPlayedAndTrickCollected;
    }

}
