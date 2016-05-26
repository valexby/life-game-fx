package life.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import life.util.SceneSaver;


public class Main extends Application {

    private FXMLLoader fxmlLoader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL location = getClass().getResource("mainGui.fxml");
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent parent = fxmlLoader.load(location.openStream());
        primaryStage.setTitle("Game of Life");
        SceneSaver.getInstance().setMainScene(new Scene(parent));
        primaryStage.setScene(SceneSaver.getInstance().getMainScene());
        primaryStage.show();
    }


    @Override
    public void stop() {
        if (((MainController) fxmlLoader.getController()).engineThread!=null)
            ((MainController) fxmlLoader.getController()).engineThread.interrupt();
        if (((MainController) fxmlLoader.getController()).botThread!=null)
            ((MainController) fxmlLoader.getController()).botThread.interrupt();
        if (((MainController) fxmlLoader.getController()).replaySaverThread!=null)
            ((MainController) fxmlLoader.getController()).replaySaverThread.interrupt();
        if (((MainController) fxmlLoader.getController()).replayLoaderThread!=null)
            ((MainController) fxmlLoader.getController()).replayLoaderThread.interrupt();
        if (((MainController) fxmlLoader.getController()).saveGenerator!=null)
            ((MainController) fxmlLoader.getController()).saveGenerator.interrupt();
    }
}
