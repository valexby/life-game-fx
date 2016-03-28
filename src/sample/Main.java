package sample;

/**
 * Created by valex on 23.3.16.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("gui.fxml"));

        primaryStage.setTitle("Game of Life");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}
