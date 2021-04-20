package ch.epfl.javass.gui;

import java.util.EnumMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import javafx.application.Application;
import javafx.stage.Stage;

public final class TestMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
        ps.put(PlayerId.PLAYER_1, new GraphicalPlayerAdapter());
        ps.put(PlayerId.PLAYER_2, new PacedPlayer(
                new MctsPlayer(PlayerId.PLAYER_2, 123, 10_000), 1));
        ps.put(PlayerId.PLAYER_3, new PacedPlayer(
                new MctsPlayer(PlayerId.PLAYER_3, 456, 10_000), 1));
        ps.put(PlayerId.PLAYER_4, new PacedPlayer(
                new MctsPlayer(PlayerId.PLAYER_4, 789, 10_000), 1));

        Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach(i -> ns.put(i, i.name()));

        new Thread(() -> {
            JassGame g = new JassGame(0, ps, ns);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }).start();
    }
}