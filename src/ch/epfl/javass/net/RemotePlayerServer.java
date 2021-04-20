package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Représente le serveur d'un joueur, qui attend une connexion sur le port 5108
 * et pilote un joueur local en fonction des messages reçus
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */

public final class RemotePlayerServer {
    public static final int PORT = 5108;
    private Player player;
    private boolean gameOn = true;

    /**
     * Construit le serveur du joueur qui attend la connexion avec le client
     * 
     * @param player
     *            le joueur local
     */
    public RemotePlayerServer(Player player) {
        this.player = player;

    }

    /**
     * Attend un message du client, appelle la méthode correspondante du joueur
     * local, et dans le cas de cardToPlay, renvoie la valeur de retour au
     * client.
     */
    public void run() {
        try (ServerSocket s0 = new ServerSocket(PORT);
                Socket s = s0.accept();
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), US_ASCII));
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                        s.getOutputStream(), US_ASCII))) {
            while (gameOn) {

                String line = r.readLine();
                String[] instructions = StringSerializer.split(" ", line);
                String jassCommand = instructions[0];

                // un switch qui traite les messages du client au cas par cas,
                // et dans le cas de cardToPlay renvoie la carte à jouer
                switch (Enum.valueOf(JassCommand.class, jassCommand)) {
                case PLRS: {
                    PlayerId ownId = PlayerId.ALL.get(
                            StringSerializer.deserializeInt(instructions[1]));
                    Map<PlayerId, String> playerNames = new HashMap<>();
                    String[] names = StringSerializer.split(",",
                            instructions[2]);
                    for (int i = 0; i < names.length; ++i) {
                        playerNames.put(PlayerId.ALL.get(i),
                                StringSerializer.deserializeString(names[i]));
                    }
                    player.setPlayers(ownId, playerNames);
                }
                    break;
                case TRMP: {
                    player.setTrump(Color.ALL.get(
                            StringSerializer.deserializeInt(instructions[1])));
                }
                    break;
                case HAND: {
                    player.updateHand(CardSet.ofPacked(
                            StringSerializer.deserializeLong(instructions[1])));
                }
                    break;
                case TRCK: {
                    player.updateTrick(Trick.ofPacked(
                            StringSerializer.deserializeInt(instructions[1])));
                }
                    break;
                case CARD: {
                    String[] turnStateComponents = StringSerializer.split(",",
                            instructions[1]);
                    TurnState state = TurnState.ofPackedComponents(
                            StringSerializer
                                    .deserializeLong(turnStateComponents[0]),
                            StringSerializer
                                    .deserializeLong(turnStateComponents[1]),
                            StringSerializer
                                    .deserializeInt(turnStateComponents[2]));
                    Card toPlay = player.cardToPlay(state, CardSet.ofPacked(
                            StringSerializer.deserializeLong(instructions[2])));
                    w.write(StringSerializer.serializeInt(toPlay.packed()));
                    w.write('\n');
                    w.flush();
                }
                    break;
                case SCOR: {
                    player.updateScore(Score.ofPacked(
                            StringSerializer.deserializeLong(instructions[1])));
                }
                    break;
                case WINR: {
                    player.setWinningTeam(TeamId.ALL.get(
                            StringSerializer.deserializeInt(instructions[1])));
                    gameOn = false;
                }
                    break;
                default: {
                    gameOn = false;
                }
                    break;

                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
}
