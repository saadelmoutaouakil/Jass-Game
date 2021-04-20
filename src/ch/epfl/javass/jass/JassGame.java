package ch.epfl.javass.jass;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Représente une partie de Jass
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class JassGame {
    private Map<PlayerId, CardSet> Hands;
    private TurnState turnState;
    private Random shuffleRng;
    private Random trumpRng;
    private final Map<PlayerId, Player> players;
    private final Map<PlayerId, String> playerNames;
    /*
     * Le counter sert à choisir un nouveau premier joueur pour les tours qui
     * suivent le tout premier
     */
    private int firstPlayerCounter = 0;

    /**
     * @param rngSeed
     *            la graine
     * @param players
     *            les joueurs donnés
     * @param playerNames
     *            le nom des joueurs donnés
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {
        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections
                .unmodifiableMap(new EnumMap<>(playerNames));
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
    }

    /**
     * teste si la partie est finie ou pas encore
     * 
     * @return retourne vrai ssi la partie est terminée, faux sinon
     */
    public boolean isGameOver() {
        if (turnState == null) {
            return false;
        } else {
            if (PackedScore.totalPoints(turnState.packedScore(),
                    TeamId.TEAM_1) >= Jass.WINNING_POINTS
                    || PackedScore.totalPoints(turnState.packedScore(),
                            TeamId.TEAM_2) >= Jass.WINNING_POINTS) {
                return true;
            } else
                return false;
        }
    }

    /**
     * Fait avancer l'état du jeu jusqu'à la fin du prochain pli, ou ne fait
     * rien si la partie est terminée
     */
    public void advanceToEndOfNextTrick() {
        Color trump;
        if (isGameOver()) {
        } else {
            if (turnState == null) {
                for (PlayerId key : players.keySet()) {
                    (players.get(key)).setPlayers(key, playerNames);
                }
                trump = Color.ALL.get(trumpRng.nextInt(Color.COUNT));
                Hands = shuffleAndDistributeCards();
                turnState = TurnState.initial(trump, Score.INITIAL,
                        veryFirstPlayer(Hands));
                setTrumpForAllPlayers(trump);
                updateScoreOfAllPlayers(turnState.score());
                updateTrickOfAllPlayers(turnState.trick());
            } else {
                // updateTrickOfAllPlayers(turnState.trick());
                turnState = turnState.withTrickCollected();
                if (!turnState.isTerminal())
                    updateTrickOfAllPlayers(turnState.trick());
                updateScoreOfAllPlayers(turnState.score());
            }
            if (turnState.isTerminal()) {
                ++firstPlayerCounter;
                Hands = shuffleAndDistributeCards();
                trump = Color.ALL.get(trumpRng.nextInt(Color.COUNT));
                PlayerId firstplayer = firstPlayer();
                turnState = TurnState.initial(trump,
                        turnState.score().nextTurn(), firstplayer);

                setTrumpForAllPlayers(trump);
                updateScoreOfAllPlayers(turnState.score());
                updateTrickOfAllPlayers(turnState.trick());

            }

            if (isGameOver())
                setWinningTeamForAllPlayers();
            else {
                int nextPlayerPlay = 0;
                while (!turnState.trick().isFull()) {

                    int playerIndice = turnState.trick().player(nextPlayerPlay)
                            .ordinal();
                    Player player = players.get(PlayerId.ALL.get(playerIndice));
                    CardSet hand = (Hands.get(PlayerId.ALL.get(playerIndice)));
                    Card card = player.cardToPlay(turnState, hand);
                    turnState = turnState.withNewCardPlayed(card);
                    Hands.put(PlayerId.ALL.get(playerIndice),
                            hand.remove(card));
                    updateTrickOfAllPlayers(turnState.trick());
                    player.updateHand(
                            Hands.get(PlayerId.ALL.get(playerIndice)));
                    ++nextPlayerPlay;
                }

            }
        }
    }

    /*
     * Construit une liste de toutes les cartes et les distribue aléatoirement
     * aux joueurs
     */
    private Map<PlayerId, CardSet> shuffleAndDistributeCards() {
        List<Card> deck = new LinkedList<Card>();
        for (int i = 0; i < Color.ALL.size(); ++i) {
            for (int j = 0; j < Rank.ALL.size(); ++j) {
                deck.add(Card.of(Color.ALL.get(i), Rank.ALL.get(j)));
            }
        }
        Collections.shuffle(deck, shuffleRng);

        Map<PlayerId, CardSet> playersHand = new HashMap<>();
        playersHand.put(PlayerId.PLAYER_1, CardSet.of(deck.subList(0, 9)));
        playersHand.put(PlayerId.PLAYER_2, CardSet.of(deck.subList(9, 18)));
        playersHand.put(PlayerId.PLAYER_3, CardSet.of(deck.subList(18, 27)));
        playersHand.put(PlayerId.PLAYER_4, CardSet.of(deck.subList(27, 36)));
        updateHandOfAllPlayers(playersHand);
        return playersHand;
    }

    /*
     * Détermine le tout premier joueur d'une partie
     */
    private PlayerId veryFirstPlayer(Map<PlayerId, CardSet> playersHand) {
        for (PlayerId key : playersHand.keySet()) {
            if (playersHand.get(key)
                    .contains(Card.of(Color.DIAMOND, Rank.SEVEN))) {
                firstPlayerCounter = key.ordinal();

                return key;
            }
        }
        return null;
    }

    /*
     * Détermine le premier joueur des tours qui suivent le tout premier
     */
    private PlayerId firstPlayer() {

        return PlayerId.ALL.get((firstPlayerCounter) % 4);

    }

    private void updateHandOfAllPlayers(Map<PlayerId, CardSet> playersHand) {
        for (PlayerId key : playersHand.keySet()) {
            (players.get(key)).updateHand(playersHand.get(key));
            ;
        }
    }

    private void updateTrickOfAllPlayers(Trick pkTrick) {
        for (PlayerId key : players.keySet()) {
            (players.get(key)).updateTrick(pkTrick);
            ;
        }
    }

    private void setTrumpForAllPlayers(Color trump) {
        for (PlayerId key : players.keySet()) {
            (players.get(key)).setTrump(trump);
            ;
        }
    }

    private void updateScoreOfAllPlayers(Score score) {
        for (PlayerId key : players.keySet()) {
            (players.get(key)).updateScore(score);
            ;
        }
    }

    private void setWinningTeamForAllPlayers() {
        TeamId winningTeam = PackedScore.totalPoints(turnState.packedScore(),
                TeamId.TEAM_1) >= Jass.WINNING_POINTS ? TeamId.TEAM_1
                        : TeamId.TEAM_2;
        ;
        for (PlayerId key : players.keySet()) {
            (players.get(key)).setWinningTeam(winningTeam);
            ;
        }
    }

    public Score getScore() {
        return this.turnState.score();
    }

}
