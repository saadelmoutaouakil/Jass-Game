package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
 * Représente le client d'un joueur
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 */
public final class RemotePlayerClient implements Player, AutoCloseable {

    private Socket s;
    private BufferedReader r;
    private BufferedWriter w;

    /**
     * Permet la connexion entre le client et l'hôte sur lequel s'exécute le
     * serveur du joueur distant
     * 
     * @param hostName
     *            le nom de l'hôte sur lequel s'exécute le serveur du joueur
     *            distant
     * @throws IOException
     *             si une erreur survient lors de la création du Socket
     */
    public RemotePlayerClient(String hostName) throws IOException {
        s = new Socket(hostName, RemotePlayerServer.PORT);
        r = new BufferedReader(
                new InputStreamReader(s.getInputStream(), US_ASCII));
        w = new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream(), US_ASCII));
    }

    /*
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState,
     * ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        String serializedScore = StringSerializer
                .serializeLong(state.packedScore());
        String serializedUnplayedCards = StringSerializer
                .serializeLong(state.packedUnplayedCards());
        String serializedTrick = StringSerializer
                .serializeInt(state.packedTrick());
        String serializedHand = StringSerializer.serializeLong(hand.packed());

        String message = JassCommand.CARD.name() + " "
                + StringSerializer.combine(",", serializedScore,
                        serializedUnplayedCards, serializedTrick)
                + " " + serializedHand;
        writeAndFlush(message);
        while (true) {
            try {
                String cardToPlay = r.readLine();
                if (cardToPlay != null)
                    return Card.ofPacked(
                            StringSerializer.deserializeInt(cardToPlay));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    /*
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId,
     * java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> serializedNames = new ArrayList<>();
        for (String name : playerNames.values()) {
            serializedNames.add(StringSerializer.serializeString(name));
        }
        String combinedNames = StringSerializer.combine(",",
                serializedNames.toArray(new String[serializedNames.size()]));
        StringBuilder message = new StringBuilder();
        message.append(JassCommand.PLRS.name()).append(" ")
                .append(StringSerializer.serializeInt(ownId.ordinal()))
                .append(" ").append(combinedNames);

        writeAndFlush(message.toString());

    }

    /*
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        String message = JassCommand.HAND.name() + " "
                + StringSerializer.serializeLong(newHand.packed());
        writeAndFlush(message);
    }

    /*
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        String message = JassCommand.TRMP.name() + " "
                + StringSerializer.serializeInt(trump.ordinal());
        writeAndFlush(message);
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        String message = JassCommand.TRCK.name() + " "
                + StringSerializer.serializeInt(newTrick.packed());
        writeAndFlush(message);
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        String message = JassCommand.SCOR.name() + " "
                + StringSerializer.serializeLong(score.packed());
        writeAndFlush(message);
    }

    /*
     * @see
     * ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        String message = JassCommand.WINR.name() + " "
                + StringSerializer.serializeInt(winningTeam.ordinal());
        writeAndFlush(message);
    }

    /*
     * Permet de fermer les flots d'entrée et de la sortie ,et le socket.
     * 
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        r.close();
        w.close();
        s.close();
    }

    /**
     * Permet l'écriture du message sur le flot de sortie ,et le vidange de ce
     * dernier pour assurer que les données qui y ont été écrites jusqu'alors
     * sont bien envoyées sur le réseau à ce moment-là.
     * 
     * @param message
     *            message échangé entre le client et le serveur
     */
    private void writeAndFlush(String message) {
        try {
            w.write(message);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
