package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

/**
 * Un adaptateur permettant d'adapter l'interface graphique (c-à-d la classe
 * GraphicalPlayer) pour en faire un joueur, c-à-d une valeur de type Player
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */

public class GraphicalPlayerAdapter implements Player {
    private ScoreBean scoreBean;
    private HandBean handBean;
    private TrickBean trickBean;
    private GraphicalPlayer graphicalPlayer;
    private ArrayBlockingQueue<Card> communicationQueue;
    private final static int QUEUE_CAPACITY = 1;

    /**
     * Construit un adaptateur d'interface graphique pour en faire un joueur
     */
    public GraphicalPlayerAdapter() {
        scoreBean = new ScoreBean();
        handBean = new HandBean();
        trickBean = new TrickBean();
        communicationQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    }

    /*
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState,
     * ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        Platform.runLater(() -> {

            CardSet playableCards = state.trick().playableCards(hand);
            handBean.setPlayableCards(playableCards);

        });

        try

        {
            Card played = communicationQueue.take();
            handBean.setPlayableCards(CardSet.EMPTY);
            return played;
        } catch (InterruptedException e) {

            throw new Error(e);

        }
    }

    /*
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId,
     * java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean,
                trickBean, handBean, communicationQueue);
        Platform.runLater(() -> {
            graphicalPlayer.createStage().show();
        });
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> {
            handBean.setHand(newHand);
        });
    }

    /*
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> {
            trickBean.setTrump(trump);
        });

    }

    /*
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> {
            trickBean.setTrick(newTrick);
        });
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {
            for (TeamId id : TeamId.ALL) {
                scoreBean.setTurnPoints(id, score.turnPoints(id));
                scoreBean.setGamePoints(id, score.gamePoints(id));
                scoreBean.setTotalPoints(id, score.totalPoints(id));
            }

        });
    }

    /*
     * @see
     * ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> {
            scoreBean.setWinningTeam(winningTeam);
        });
    }

}