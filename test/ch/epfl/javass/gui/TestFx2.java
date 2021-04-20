package ch.epfl.javass.gui;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestFx2 extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ObjectProperty<String> s = new SimpleObjectProperty<>();
        s.set("onon");
        boolean b = false;
        Label nameL = new Label("Nom :");
        TextField nameF = new TextField();
        Label pwL = new Label("Mot de passe :");
        TextField pwF = new TextField();

        Button connectB = new Button("Connexion");
        Button connectA = new Button("Connexion");

        GridPane grid = new GridPane();
        grid.addRow(0, nameL, nameF);
        grid.addRow(1, pwL, pwF);
        grid.add(connectB, 0, 2, 2, 1);

        GridPane.setHalignment(connectB, HPos.CENTER);
        BorderPane border = new BorderPane(connectA);
        StackPane stack = new StackPane(grid, border);
        border.visibleProperty().bind(s.isEqualTo("onon"));
        Scene scene = new Scene(stack);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}