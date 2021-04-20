package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestDePerformance {

    public static void main(String[] args) {
        double x = 0;
        Random rng = new Random(2019);
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        final int seed = 2019;
        final int iterations = 10000;
        for (PlayerId pId : PlayerId.ALL) {
            Player player;
            if (pId.team().equals(TeamId.TEAM_1)) {
                player = new MctsPlayer(pId, seed, iterations);
            }

            else {
                player = new RandomPlayer(seed);
            }

            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        for (int i = 0; i < 10; i++) {
            System.out.println("Nouvelle GAME" + i);
            long t1 = System.currentTimeMillis();

            int seedVar = rng.nextInt(3000);
            int finalValue = seedVar >= 1000 ? seedVar : seedVar + 1000;
            JassGame g = new JassGame(finalValue, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();

            }
            /*
             * Score score = g.getScore(); int winCountTeam1 = 0; int
             * winCountTeam2 = 0;
             * if(score.totalPoints(TeamId.TEAM_1)>score.totalPoints(TeamId.
             * TEAM_2)) { winCountTeam1++;
             * System.out.println("Victoire de Team 1 " +
             * winCountTeam1+" | "+winCountTeam2); } else { ++x;
             * winCountTeam2++; System.out.println("Victoire de Team 2 " +
             * winCountTeam1+" | "+winCountTeam2); } long t2 =
             * System.currentTimeMillis();
             * System.out.println("Game terminée en " + (double)(t2-t1)/1000 +
             * " s");
             */ }

        System.out.println("La perfomance est de " + x / 10);
    }

}
