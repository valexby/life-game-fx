package life.Threads;

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

    @Override
    public void run() {
        File saveDirectory = new File(savePath);
        if (!saveDirectory.canRead()) {
            if (!saveDirectory.mkdir())
                Platform.runLater(() -> {
                    controller.showErrorMessage("Saves directory error", saveDirectory.getPath());
                });
            return;
        }
        ObservableList<String> buffer = FXCollections.observableArrayList();
        buffer.addAll(saveDirectory.list());
        Collections.sort(buffer);
        Platform.runLater(() -> {
            controller.savesList.setItems(buffer);
        });
    }
}