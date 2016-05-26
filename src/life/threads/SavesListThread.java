package life.threads;

import java.io.File;
import java.util.Collections;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import life.gui.MainController;

public class SavesListThread extends AbstractControllerThread {
    private final static String savePath = "saves/", replayPath = "replay/";
    private String path;
    private ListView<String> destinationList;
    public enum ListId {SAVES, REPLAYS}
    private ListId listId;
    public SavesListThread(MainController mainController, String path, ListView<String> destinationList) {
        super(mainController);
        this.path = path;
        this.destinationList = destinationList;
    }

    private String[] getSavesList() {
        File directory = new File(path);
        if (!directory.canRead()) {
            Platform.runLater(() -> mainController.showErrorMessage("Saves directory error", directory.getPath()));
            return null;
        }
        return directory.list();
    }

    @Override
    public void run() {
        ObservableList<String> buffer = FXCollections.observableArrayList(getSavesList());
        Collections.sort(buffer);
        Platform.runLater(() -> destinationList.setItems(buffer));
    }


}