package ch.epfl.javass.net;

import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PlayerId;

public class Server {

    public static void main(String[] args) {
        RemotePlayerServer r = new RemotePlayerServer(
                new MctsPlayer(PlayerId.PLAYER_1, 1L, 10000));
        try {
            r.run();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
