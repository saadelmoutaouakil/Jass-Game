package ch.epfl.javass;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Un programme permettant de jouer à une partie locale, à laquelle des joueurs
 * distants peuvent éventuellement participer
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public class LocalMain extends Application {

    private final static int PARAMETERS_WITHOUT_SEED = 4;
    private final static int PARAMETERS_WITH_SEED = 5;
    private final static String[] DEFAULT_NAMES = { "Aline", "Bastien",
            "Colette", "David" };
    private final static int DEFAULT_ITERATIONS = 10000;
    private final static int MAX_COMPONENT_H = 2;
    private final static int MAX_COMPONENT_S_R = 3;
    private final static int MINIMAL_ITERATIONS = 10;
    private final static int MINTIME = 2;
    private final static int TIME_BETWEEN_TRICKS = 1000;

    public static void main(String[] args) {
        launch(args);
    }

    /*
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> parameters = getParameters().getRaw();
        /*
         * On initialise d'abord les paramètres par défaut qui changeront en
         * fonction des arguments donnés à LocalMain
         */
        Random rng = new Random();
        String host = "localhost";
        Map<PlayerId, Player> players = new HashMap<PlayerId, Player>();
        Map<PlayerId, String> names = new HashMap<PlayerId, String>();
        for (PlayerId id : PlayerId.ALL) {
            names.put(id, DEFAULT_NAMES[id.ordinal()]);
        }

        int iterations = DEFAULT_ITERATIONS;
        /*
         * Vérification du nombre de paramètres
         */
        if (parameters.size() != PARAMETERS_WITHOUT_SEED
                && parameters.size() != PARAMETERS_WITH_SEED) {
            System.err.println(
                    "Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>]\n"
                            + "où :\n" + "<jn> spécifie le joueur n, ainsi:\n"
                            + "  h:<nom>  un joueur humain nommé <nom>\n"
                            + "  s:<nom>:<Nombre d'iterations>  un joueur simulé nommé <nom> et dont le nombre d'itérations de son algorithme MCTS est <Nombre d'iterations>\n"
                            + "  r:<nom>:<Nom ou adresse IP de l'hôte>  un joueur distant nommé <nom> dont le nom ou l'adresse IP de l'hôte sur lequel le serveur du joueur s'exécute est <Nom ou adresse IP de l'hôte>\n"
                            + "  [<graine>] un entier optionnel, spécifie la graine à utiliser pour générer les graines des différents générateurs aléatoires du programme\n");
            System.exit(1);
        }
        /*
         * Vérification, dans le cas de 5 paramètres, de la validité de la
         * graine
         */
        if (parameters.size() == PARAMETERS_WITH_SEED) {
            try {
                Long seed = Long
                        .parseLong(parameters.get(PARAMETERS_WITHOUT_SEED));
                rng = new Random(seed);
            } catch (NumberFormatException e) {
                System.err.println(
                        "Erreur : la graine n'est pas un entier valide.");
                System.exit(1);
            }
        }

        // La graine utilisée pour le JassGame plus tard
        long rngJassGame = rng.nextLong();

        /*
         * Boucle qui vérifie les autres exceptions possibles, que ce soit une
         * identification invalide du joueur ou un nombre de paramètres erroné
         * pour un type de joueur, sinon crée le joueur en question
         */
        for (int playerIndex = 0; playerIndex < PARAMETERS_WITHOUT_SEED; ++playerIndex) {
            String[] components = StringSerializer.split(":",
                    parameters.get(playerIndex));

            // Cas d'une spécification erronée
            if (!(components[0].equals("h") || components[0].equals("s")
                    || components[0].equals("r"))) {
                System.err
                        .println("Erreur : spécification de joueur invalide : "
                                + components[0] + ".");
                System.exit(1);
            }

            // Cas d'un joueur humain
            if (components[0].equals("h")) {
                // 2 composantes maximum
                if (components.length > MAX_COMPONENT_H) {
                    System.err.println("Erreur : la spécification du joueur "
                            + (playerIndex + 1)
                            + " humain comporte trop de composants.");
                    System.exit(1);
                }
                // Si il y a deux composantes, remplacer le nom par défaut par
                // celui indiqué
                if (components.length == MAX_COMPONENT_H) {
                    names.replace(PlayerId.ALL.get(playerIndex), components[1]);
                }
                players.put(PlayerId.ALL.get(playerIndex),
                        new GraphicalPlayerAdapter());
            }
            // Cas d'un joueur simulé
            if (components[0].equals("s")) {
                // 3 composantes maximum
                if (components.length > MAX_COMPONENT_S_R) {
                    System.err.println("Erreur : la spécification du joueur "
                            + (playerIndex + 1)
                            + " simulé comporte trop de composants.");
                    System.exit(1);
                }
                // Si 3 composantes, vérifier que la dernière est un entier
                // valide supérieur ou égal à 10
                if (components.length == MAX_COMPONENT_S_R) {
                    try {
                        iterations = Integer.parseInt(components[2]);
                        if (iterations < MINIMAL_ITERATIONS) {
                            System.err.println(
                                    "Erreur : le nombre d'itération du joueur "
                                            + (playerIndex + 1)
                                            + " simulé est inférieur à 10.");
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println(
                                "Erreur : le nombre d'itération du joueur "
                                        + (playerIndex + 1)
                                        + " simulé n'est pas un entier valide.");
                        System.exit(1);
                    }
                }
                // Si 2 composantes ou plus, il y a forcément soit une chaine
                // vide soit le nom du joueur est précisé, dans le deuxième cas
                // l'attribuer au joueur
                if (components.length >= MAX_COMPONENT_S_R - 1) {
                    if (!components[1].equals(""))
                        names.replace(PlayerId.ALL.get(playerIndex),
                                components[1]);
                }
                Long rngSeed = rng.nextLong();
                players.put(PlayerId.ALL.get(playerIndex),
                        new PacedPlayer(
                                new MctsPlayer(PlayerId.ALL.get(playerIndex),
                                        rngSeed, iterations),
                                MINTIME));
            }

            // Cas d'un joueur distant
            if (components[0].equals("r")) {
                // 3 composantes maximum
                if (components.length > MAX_COMPONENT_S_R) {
                    System.err.println("Erreur : la spécification du joueur "
                            + (playerIndex + 1)
                            + " distant comporte trop de composants.");
                    System.exit(1);
                }
                // Si 2 composantes ou plus, il y a forcément soit une chaine
                // vide soit le nom du joueur est précisé, dans le deuxième cas
                // l'attribuer au joueur
                if (components.length >= MAX_COMPONENT_S_R - 1) {
                    if (!components[1].equals(""))
                        names.replace(PlayerId.ALL.get(playerIndex),
                                components[1]);
                }
                // Si 3 composantes, remplacer localhost par celle-ci
                if (components.length == MAX_COMPONENT_S_R) {
                    host = components[2];
                }
                try {
                    players.put(PlayerId.ALL.get(playerIndex),
                            new RemotePlayerClient(host));
                } catch (IOException e) {
                    System.err.println(
                            "Erreur : une erreur est survenue lors de la connection du RemotePlayerClient du joueur "
                                    + (playerIndex + 1) + " avec le serveur.");
                    System.exit(1);
                }
            }
        }
        // Fil d'exécution séparé dans lequel la partie se déroule
        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(rngJassGame, players, names);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(TIME_BETWEEN_TRICKS);
                } catch (Exception e) {
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();

    }
}