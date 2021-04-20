package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Représente l'interface graphique d'un joueur humain
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public class GraphicalPlayer {

    private ObservableMap<Card, Image> cardImages;
    private ObservableMap<Card, Image> handImages;
    private ObservableMap<Card.Color, Image> trumpImages;
    private ArrayBlockingQueue<Card> communicationQueue;

    private final static int TRUMP_DIMENSION = 101;
    private final static int HEIGHT_TRICK = 180;
    private final static int WIDTH_TRICK = 120;
    private final static int HEIGHT_HAND = 120;
    private final static int WIDTH_HAND = 80;
    private final static int BLUR_RADIUS = 4;
    private final static double FULL_OPACITY = 1 ;
    private final static double REDUCED_OPACITY = 0.2;
    private Scene scene;
    private String title;

    /**
     * Construit l'interface graphique du joueur humain
     * 
     * @param id
     *            identité du joueur
     * @param playerNames
     *            le tableau associant l'identité de chaque joueur à son nom
     * @param scoreBean
     *            le bean des score
     * @param trickBean
     *            le bean des plis
     * @param handBean
     *            le bean de la main
     * @param communicationQueue
     *            la queue de communication entre les deux fils
     */
    public GraphicalPlayer(PlayerId id, Map<PlayerId, String> playerNames,
            ScoreBean scoreBean, TrickBean trickBean, HandBean handBean,
            ArrayBlockingQueue<Card> communicationQueue) {
        cardImages = createMapOfTrickImage();
        handImages = createMapOfHandImage();
        trumpImages = createMapOfTrumpImage();
        this.communicationQueue = communicationQueue;

        GridPane scorePane = createScorePane(playerNames, scoreBean);
        GridPane trickPane = createTrickPane(playerNames, id, trickBean);
        HBox handPane = createHandPane(handBean);
        BorderPane victoryPane_Team1 = createVictoryPanes(scoreBean,
                playerNames, TeamId.TEAM_1);
        BorderPane victoryPane_Team2 = createVictoryPanes(scoreBean,
                playerNames, TeamId.TEAM_2);
        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(trickPane);
        mainPane.setTop(scorePane);
        mainPane.setBottom(handPane);
        StackPane gamePane = new StackPane(mainPane, victoryPane_Team1,
                victoryPane_Team2);
        victoryPane_Team1.visibleProperty()
                .bind(scoreBean.winningTeamProperty().isEqualTo(TeamId.TEAM_1));
        victoryPane_Team2.visibleProperty()
                .bind(scoreBean.winningTeamProperty().isEqualTo(TeamId.TEAM_2));
        scene = new Scene(gamePane);
        title = "Javass - " + playerNames.get(id);
    }

    /**
     * Crée et renvoie la fenêtre de l'interface graphique
     * 
     * @return renvoie la fenêtre créée
     */
    public Stage createStage() {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title);
        return stage;
    }

    /**
     * Construit le panneau des scores et le renvoie
     * 
     * @param playerNames
     *            le tableau associant l'identité des joueurs à leur nom
     * @param scoreBean
     *            le bean représentant les scores
     * @return le panneau des scores
     */
    private GridPane createScorePane(Map<PlayerId, String> playerNames,
            ScoreBean scoreBean) {

        GridPane scorePane = new GridPane();
        scorePane.setStyle(
                "-fx-font: 16 Optima;-fx-background-color: lightgray;-fx-padding: 5px;-fx-alignment: center;");

        for (TeamId id : TeamId.ALL) {

            // La chaine représentant le nom des deux joueurs de l'équipe
            String playersName = playerNames.get(PlayerId.ALL.get(id.ordinal()))
                    + " et "
                    + playerNames.get(PlayerId.ALL.get(id.ordinal() + 2))
                    + " : ";
            // Les points du dernier pli joué
            IntegerProperty lastTrickPoints = new SimpleIntegerProperty();
            scoreBean.turnPointsProperty(id)
                    .addListener((observable, oldValue, newValue) -> {
                        lastTrickPoints
                                .set(newValue.intValue() - oldValue.intValue());
                    });
            Text names = new Text(playersName);
            Text turnPoints = new Text();
            turnPoints.textProperty()
                    .bind(Bindings.convert(scoreBean.turnPointsProperty(id)));
            Text gamePoints = new Text();
            gamePoints.textProperty()
                    .bind(Bindings.convert(scoreBean.gamePointsProperty(id)));
            Text lastTrick = new Text();

            // Le texte des points du dernier pli n'apparait qu'après avoir joué
            // un pli, ce qui correspond à avoir les points du tour
            // supérieur à 0
            lastTrick.textProperty()
                    .bind(Bindings
                            .when(scoreBean.turnPointsProperty(id).isEqualTo(0))
                            .then("").otherwise(Bindings.concat("(+",
                                    Bindings.convert(lastTrickPoints), ")")));
            Text totalText = new Text(" / Total : ");
            scorePane.addRow(id.ordinal(), names, turnPoints, lastTrick,
                    totalText, gamePoints);
            GridPane.setHalignment(names, HPos.RIGHT);
            GridPane.setHalignment(turnPoints, HPos.RIGHT);
            GridPane.setHalignment(gamePoints, HPos.RIGHT);

        }

        return scorePane;
    }

    /**
     * Construit le panneau du pli et le renvoie
     * 
     * @param playerNames
     *            le tableau associant l'identité des joueurs à leur nom
     * @param id
     *            l'identité du joueur
     * @param trickBean
     *            le bean du pli
     * @return le panneau du pli
     */
    private GridPane createTrickPane(Map<PlayerId, String> playerNames,
            PlayerId id, TrickBean trickBean) {

        // L'image représentant l'atout
        ImageView trump_image = new ImageView();
        trump_image.imageProperty()
                .bind(Bindings.valueAt(trumpImages, trickBean.TrumpProperty()));
        trump_image.setFitHeight(TRUMP_DIMENSION);
        trump_image.setFitWidth(TRUMP_DIMENSION);

        VBox[] couples = new VBox[PlayerId.COUNT];
        for (int playerIndex = 0; playerIndex < PlayerId.COUNT; ++playerIndex) {
            PlayerId player = PlayerId.ALL
                    .get((id.ordinal() + playerIndex) % 4);
            // L'image de la carte que joue le joueur
            ImageView image = createCardImage(trickBean, player);
            // Le halo correspondant à cette image
            StackPane halo = createHalo(player, image, trickBean);
            Text name = new Text(playerNames.get(player));
            name.setStyle("-fx-font: 14 Optima;");
            VBox couple;
            if (playerIndex == 0) {
                couple = new VBox(halo, name);
            } else {
                couple = new VBox(name, halo);

            }
            couple.setStyle("-fx-padding: 5px;-fx-alignment: center;");
            couples[playerIndex] = couple;

        }

        GridPane trickPane = new GridPane();
        trickPane.setStyle(
                "-fx-background-color: whitesmoke;-fx-padding: 5px;-fx-border-width: 3px 0px;-fx-border-style: solid;-fx-border-color: gray;-fx-alignment: center;");
        // Arranger les couples image/nom dans le panneau du pli
        trickPane.add(couples[3], 0, 0, 1, 3);
        trickPane.add(couples[2], 1, 0, 1, 1);
        trickPane.add(trump_image, 1, 1);
        trickPane.add(couples[0], 1, 2, 1, 1);
        trickPane.add(couples[1], 2, 0, 1, 3);
        GridPane.setHalignment(trump_image, HPos.CENTER);

        return trickPane;
    }

    /**
     * Construit et renvoie le panneau de victoire de l'équipe donnée en
     * argument
     * 
     * @param scoreBean
     *            le bean des scores
     * @param playerNames
     *            le tableau associant l'identité des joueurs à leur nom
     * @param id
     *            l'identité de l'équipe
     * @return le panneau de victoire
     */
    private BorderPane createVictoryPanes(ScoreBean scoreBean,
            Map<PlayerId, String> playerNames, TeamId id) {
        Text winningTeamText = new Text();
        winningTeamText.textProperty()
                .bind(Bindings.format(
                        "%s et %s ont gagné avec %d points contre %d .",
                        playerNames.get(PlayerId.ALL.get(id.ordinal())),
                        playerNames.get(PlayerId.ALL.get(id.ordinal() + 2)),
                        scoreBean.totalPointsProperty(id),
                        scoreBean.totalPointsProperty(id.other())));

        BorderPane victoryPane = new BorderPane(winningTeamText);
        victoryPane
                .setStyle("-fx-font: 16 Optima;-fx-background-color: white;");

        return victoryPane;
    }

    /**
     * Construit et renvoie le panneau de la main
     * 
     * @param handBean
     *            le bean de la main
     * @return le panneau de la main
     */
    private HBox createHandPane(HandBean handBean) {
        HBox handBox = new HBox();
        handBox.setStyle(
                "-fx-background-color: lightgray;-fx-spacing: 5px;-fx-padding: 5px;");
        for (int cardIndex = 0; cardIndex < Jass.HAND_SIZE; ++cardIndex) {
            ImageView image = createImageCardOfHand(handBean, cardIndex);
            handBox.getChildren().add(cardIndex, image);
        }
        return handBox;
    }

    /**
     * Construit et renvoie l'image correspondante à la carte à l'index donné
     * 
     * @param handBean
     *            le bean de la main
     * @param cardIndex
     *            l'index de la carte
     * @return l'image de la carte
     */
    private ImageView createImageCardOfHand(HandBean handBean, int cardIndex) {
        ImageView card = new ImageView();
        card.imageProperty().bind(Bindings.valueAt(handImages,
                Bindings.valueAt(handBean.hand(), cardIndex)));
        card.setFitHeight(HEIGHT_HAND);
        card.setFitWidth(WIDTH_HAND);
        card.setOnMouseClicked(e -> {
            communicationQueue.clear();
            communicationQueue.add(handBean.hand().get(cardIndex));
        });

        BooleanProperty isPlayable = new SimpleBooleanProperty();
        isPlayable.bind(Bindings.createBooleanBinding(() -> {
            return handBean.playableCards()
                    .contains(handBean.hand().get(cardIndex));
        }, handBean.playableCards()));

        card.opacityProperty()
                .bind(Bindings.when(isPlayable).then(FULL_OPACITY).otherwise(REDUCED_OPACITY));
        card.disableProperty().bind(isPlayable.not());
        return card;
    }

    /**
     * Construit et renvoie un panneau représentant une image munie d'un halo
     * qui apparait quand il représente la carte la plus forte du pli
     * 
     * @param id
     *            identité du joueur
     * @param image
     *            image à laquelle on veut créer le halo
     * @param trickBean
     *            le bean du pli
     * @return le panneau représentant l'image munie d'un halo
     */
    private StackPane createHalo(PlayerId id, ImageView image,
            TrickBean trickBean) {
        StackPane halo_player = new StackPane();
        Rectangle rectangle = new Rectangle(WIDTH_TRICK, HEIGHT_TRICK);
        rectangle.setStyle(
                "-fx-arc-width: 20;-fx-arc-height: 20;-fx-fill: transparent;-fx-stroke: lightpink;-fx-stroke-width: 5;-fx-opacity: 0.5;");
        GaussianBlur effect = new GaussianBlur(BLUR_RADIUS);
        halo_player.getChildren().add(rectangle);
        halo_player.getChildren().add(image);
        rectangle.setEffect(effect);
        rectangle.visibleProperty()
                .bind(trickBean.WinningPlayerProperty().isEqualTo(id));
        return halo_player;
    }

    /**
     * Construit et renvoie l'image du pli correspondante au joueur dont
     * l'identité est playerId
     * 
     * @param trickBean
     *            bean du pli
     * @param playerId
     *            identité du joueur
     * @return l'image correspodante du pli
     */
    private ImageView createCardImage(TrickBean trickBean, PlayerId playerId) {
        ImageView image_player = new ImageView();
        image_player.imageProperty().bind(Bindings.valueAt(cardImages,
                Bindings.valueAt(trickBean.trick(), playerId)));
        image_player.setFitHeight(HEIGHT_TRICK);
        image_player.setFitWidth(WIDTH_TRICK);
        return image_player;
    }

    /**
     * Construit une table associative observable associant l'ensemble des
     * cartes représentables dans l'interface graphique du pli aux images leur
     * correspondant
     * 
     * @return la table associative
     */
    private ObservableMap<Card, Image> createMapOfTrickImage() {
        ObservableMap<Card, Image> tab = FXCollections.observableHashMap();
        for (int i = 0; i < Color.COUNT; ++i) {
            for (int j = 0; j < Rank.COUNT; ++j) {
                Card card = Card.of(Color.ALL.get(i), Rank.ALL.get(j));
                String adress = "/card_" + Integer.toString(i) + "_"
                        + Integer.toString(j) + "_240.png";
                tab.put(card, new Image(adress));

            }
        }
        return tab;
    }

    /**
     * Construit une table associative observable associant l'ensemble des
     * cartes représentables dans l'interface graphique de la main aux images
     * leur correspondant
     * 
     * @return la table associative
     */
    private ObservableMap<Card, Image> createMapOfHandImage() {
        ObservableMap<Card, Image> tab = FXCollections.observableHashMap();
        for (int i = 0; i < Color.COUNT; ++i) {
            for (int j = 0; j < Rank.COUNT; ++j) {
                Card card = Card.of(Color.ALL.get(i), Rank.ALL.get(j));
                String adress = "/card_" + Integer.toString(i) + "_"
                        + Integer.toString(j) + "_160.png";
                tab.put(card, new Image(adress));

            }
        }
        return tab;
    }

    /**
     * Construit une table associative observable associant les atouts à l'image
     * leur correspondant
     * 
     * @return la table associative
     */
    private ObservableMap<Card.Color, Image> createMapOfTrumpImage() {
        ObservableMap<Card.Color, Image> tab = FXCollections
                .observableHashMap();
        for (Card.Color color : Color.ALL) {
            Image trump = new Image("/trump_" + color.ordinal() + ".png");
            tab.put(color, trump);
        }
        return tab;
    }
}