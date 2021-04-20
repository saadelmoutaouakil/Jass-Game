package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Représente un joueur
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public interface Player {

    /**
     * Retourne la carte que le joueur désire jouer, sachant que l'état actuel
     * du tour est celui décrit par state et que le joueur a les cartes hand en
     * main.
     * 
     * @param state
     *            l'état actuel donné
     * @param hand
     *            la main du joueur
     * @return Retourne la carte que le joueur désire jouer
     */
    Card cardToPlay(TurnState state, CardSet hand);

    /**
     * Appelée une seule fois en début de partie pour informer le joueur qu'il a
     * l'identité ownId et que les différents joueurs (lui inclus) sont nommés
     * selon le contenu de la table associative playerNames
     * 
     * @param ownId
     *            l'identité du joueur
     * @param playerNames
     *            le nom des joueurs
     */
    default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    };

    /**
     * Appelée chaque fois que la main du joueur change — soit en début de tour
     * lorsque les cartes sont distribuées, soit après qu'il ait joué une
     * carte — pour l'informer de sa nouvelle main
     * 
     * @param newHand
     *            la nouvelle main du joueur
     */
    default void updateHand(CardSet newHand) {
    };

    /**
     * Appelée chaque fois que l'atout change — c-à-d au début de chaque
     * tour — pour informer le joueur de l'atout
     * 
     * @param trump
     *            l'atout dont on veut informer le joueur
     */
    default void setTrump(Color trump) {
    };

    /**
     * Appelée chaque fois que le pli change, c-à-d chaque fois qu'une carte est
     * posée ou lorsqu'un pli terminé est ramassé et que le prochain pli (vide)
     * le remplace
     * 
     * @param newTrick
     *            le nouveau pli
     */
    default void updateTrick(Trick newTrick) {
    };

    /**
     * Appelée chaque fois que le score change, c-à-d chaque fois qu'un pli est
     * ramassé
     * 
     * @param score
     *            le nouveau score
     */
    default void updateScore(Score score) {
    };

    /**
     * Appelée une seule fois dès qu'une équipe à gagné en obtenant 1000 points
     * ou plus
     * 
     * @param winningTeam
     *            l'équipe gagnante
     */
    default void setWinningTeam(TeamId winningTeam) {
    };

}
