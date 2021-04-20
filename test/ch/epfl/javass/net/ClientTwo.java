package ch.epfl.javass.net;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.RandomPlayer;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.net.RemotePlayerClient;

public class ClientTwo {
    public static void main(String[] args)
            throws UnknownHostException, IOException {
        // System.out.println("lllllllllll");
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        final int seed = 0;
        boolean mcts = true;
        for (PlayerId pId : PlayerId.ALL) {
            Player player;
            if (mcts) {

                player = new RemotePlayerClient("localhost");

                // RemotePlayerServer serveur = new RemotePlayerServer(new
                // RandomPlayer(seed));
                // serveur.run();
                mcts = false;
            } else {
                player = new RandomPlayer(seed);
            }

            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        for (int i = 0; i < 1; ++i) {
            System.out.println("Nouvelle GAME" + i);
            long t1 = System.currentTimeMillis();
            JassGame g = new JassGame(i, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();

            }
            Score score = g.getScore();
            // g.getScore() rajouter un geter dans JassGame pour que ça
            // fonctionne :
            // public Score getScore() { //dans JassGame
            // return this.turnState.score();
            // }
            int winCountTeam1 = 0;
            int winCountTeam2 = 0;
            if (score.totalPoints(TeamId.TEAM_1) > score
                    .totalPoints(TeamId.TEAM_2)) {
                winCountTeam1++;
                System.out.println("Victoire de Team 1 " + winCountTeam1 + " | "
                        + winCountTeam2);
            } else {
                winCountTeam2++;
                System.out.println("Victoire de Team 2 " + winCountTeam1 + " | "
                        + winCountTeam2);
            }
            long t2 = System.currentTimeMillis();
            System.out.println(
                    "Game terminée en " + (double) (t2 - t1) / 1000 + " s");
        }
    }
}