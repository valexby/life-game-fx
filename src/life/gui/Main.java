package life.gui;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    private FXMLLoader fxmlLoader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getResource("gui.fxml");
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent parent = fxmlLoader.load(location.openStream());

        primaryStage.setTitle("Game of Life");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    @Override
    public void stop() {
        ((Controller) fxmlLoader.getController()).engineThread.interrupt();
        ((Controller) fxmlLoader.getController()).botThread.interrupt();
        ((Controller) fxmlLoader.getController()).replaySaverThread.interrupt();
    }
}
