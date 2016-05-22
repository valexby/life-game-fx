package life.threads;

import java.io.File;
import java.util.Collections;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import life.gui.Controller;

public class SavesListThread extends AbstractControllerThread {
    private final static String savePath = "saves";

    public SavesListThread(Controller controller) {
        super(controller);
    }

    private String[] GetSavesList() {
        File saveDirectory = new File(savePath);
        if (!saveDirectory.canRead()) {
            Platform.runLater(() -> controller.showErrorMessage("Saves directory error", saveDirectory.getPath()));
            return null;
        }
        return saveDirectory.list();
    }

    @Override
    public void run() {
        ObservableList<String> buffer = FXCollections.observableArrayList(GetSavesList());
        Collections.sort(buffer);
        Platform.runLater(() -> controller.savesList.setItems(buffer));
    }


}