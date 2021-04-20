package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Un programme permettant de participer à une partie distante, qui se déroule
 * sur un autre ordinateur.
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public class RemoteMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /*
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        RemotePlayerServer server = new RemotePlayerServer(
                new GraphicalPlayerAdapter());
        Thread gameThread = new Thread(() -> {
            System.out.println(
                    "La partie commencera à la connexion du client...");
            server.run();
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

}
