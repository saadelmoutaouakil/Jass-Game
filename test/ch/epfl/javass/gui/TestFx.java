package ch.epfl.javass.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public final class TestFx extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("click me!");
        button.setOnAction(e -> System.out.println("click"));

        Scene scene = new Scene(button);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
