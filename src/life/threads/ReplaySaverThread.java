package life.threads;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import life.gui.MainController;
import life.util.FileInterface;
import life.util.LifeEvent;

public class ReplaySaverThread extends AbstractControllerThread {

    FileInterface descriptor;
    private String fileName;

    public ReplaySaverThread(MainController mainController, String fileName) {
        super(mainController);
        this.fileName = fileName;
    }

    private void gridSave() {
        int cols, rows;
        ArrayList<Boolean> gridMap;
        try {
            descriptor = new FileInterface(FileInterface.WRITE_MODE, fileName);
        } catch (IOException ex) {
            Platform.runLater(() -> mainController.showErrorMessage("Replay save error occurred", ex.getMessage()));
        }
        synchronized (MainController.criticalZone) {
            rows = mainController.board.getRows();
            cols = mainController.board.getCols();
            gridMap = new ArrayList<>(rows * cols);
            mainController.board.getGrid().stream()
                    .forEach(i -> i
                            .forEach(j -> gridMap.add(j.getState())));
        }
        try {
            descriptor.saveGrid(gridMap, rows, cols);
        } catch (Exception ex) {
            Platform.runLater(() -> mainController.showErrorMessage("Replay save error occurred", ex.getMessage()));
        }
    }

    @Override
    synchronized public void run() {
        gridSave();
        try {
            while (true) {
                if (mainController.chronicle.isEmpty()) {
                    this.wait();
                }
                if (mainController.chronicle.get().getType() == LifeEvent.TICK)
                    this.wait();
                cascadeWrite();
            }
        } catch (InterruptedException ex) {
            while (!mainController.chronicle.isEmpty()) {
                cascadeWrite();
            }
            try {
                descriptor.close();
            } catch (IOException exception) {
                Platform.runLater(() ->
                        mainController.showErrorMessage("Replay file close error", exception.getMessage()));
            }
            Platform.runLater(() -> mainController.replaySaveButton.setDisable(false));
            mainController.replaysListThread = new SavesListThread(mainController,
                    MainController.replayPath, mainController.replaysList);
            mainController.replaysListThread.start();
        }
    }

    private void cascadeWrite() {
        try {
            descriptor.saveEvent(mainController.chronicle.poll());
        } catch (Exception ex) {
            Platform.runLater(() ->
                    mainController.showErrorMessage("Replay save error occurred", ex.getMessage()));
        }
    }
}
