package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

public class OctogoneJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        final int seed = 101;
        final int iterations = 100;
        for (PlayerId pId : PlayerId.ALL) {
            Player player;
            if (pId.team().equals(TeamId.TEAM_1)) {
                player = new MctsPlayer(pId, seed, iterations);
            }

            else {
                player = new MctsPlayer(pId, 3, 100);
            }

            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        for (int i = 0; i < 20; i++) {
            System.out.println("Nouvelle GAME" + i);
            long t1 = System.currentTimeMillis();
            JassGame g = new JassGame(i, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();

            }
            /*
             * Score score = g.getScore(); int winCountTeam1 = 0; int
             * winCountTeam2 = 0;
             * if(score.totalPoints(TeamId.TEAM_1)>score.totalPoints(TeamId.
             * TEAM_2)) { winCountTeam1++;
             * System.out.println("Victoire de Team 1 " +
             * winCountTeam1+" | "+winCountTeam2); } else { winCountTeam2++;
             * System.out.println("Victoire de Team 2 " +
             * winCountTeam1+" | "+winCountTeam2); } long t2 =
             * System.currentTimeMillis();
             * System.out.println("Game terminée en " + (double)(t2-t1)/1000 +
             * " s");
             */ }

    }
}