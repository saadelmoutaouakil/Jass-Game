package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Permet de s'assurer qu'un joueur met un temps minimum pour jouer
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class PacedPlayer implements Player {
    private Player player;
    private double minTime;

    /**
     * Construit un joueur qui permet de s'assurer qu'un joueur met un temps
     * minimum pour jouer, avec le joueur et le temps minimum donnés
     * 
     * @param underlyingPlayer
     *            le joueur qui doit mettre un temps minimum pour jouer
     * @param minTime
     *            le temps minimum que doit mettre un joueur
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        this.player = underlyingPlayer;
        this.minTime = minTime;
    }

    /*
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState,
     * ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long start = System.currentTimeMillis();
        Card card = player.cardToPlay(state, hand);
        long end = System.currentTimeMillis();
        long diff = end - start;

        try {
            if ((diff) < minTime * 1000) {
                Thread.sleep((long) (minTime * 1000 - diff));
            }
        } catch (InterruptedException e) {
        }

        return card;
    }

    /*
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId,
     * java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        player.setPlayers(ownId, playerNames);
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        player.updateHand(newHand);
    }

    /*
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        player.setTrump(trump);
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        player.updateTrick(newTrick);
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        player.updateScore(score);
    }

    /*
     * @see
     * ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        player.setWinningTeam(winningTeam);
    }

}
