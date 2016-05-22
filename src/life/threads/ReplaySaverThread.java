package life.threads;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import life.gui.Controller;
import life.util.FileInterface;
import life.util.LifeEvent;

public class ReplaySaverThread extends AbstractControllerThread {

    FileInterface descriptor;
    private String fileName;

    public ReplaySaverThread(Controller controller, String fileName) {
        super(controller);
        this.fileName = fileName;
    }

    private void gridSave() {
        int cols, rows;
        ArrayList<Boolean> gridMap;
        try {
            descriptor = new FileInterface(FileInterface.WRITE_MODE, fileName);
        } catch (IOException ex) {
            Platform.runLater(() -> controller.showErrorMessage("Replay save error occurred", ex.getMessage()));
        }
        synchronized (Controller.criticalZone) {
            rows = controller.board.getRows();
            cols = controller.board.getCols();
            gridMap = new ArrayList<>(rows * cols);
            controller.board.getGrid().stream()
                    .forEach(i -> i
                            .forEach(j -> gridMap.add(j.getState())));
        }
        try {
            descriptor.saveGrid(gridMap, rows, cols);
        } catch (Exception ex) {
            Platform.runLater(() -> controller.showErrorMessage("Replay save error occurred", ex.getMessage()));
        }
    }

    @Override
    synchronized public void run() {
        gridSave();
        try {
            while (true) {
                if (controller.chronicle.isEmpty()) {
                    this.wait();
                }
                if (controller.chronicle.get().getType() == LifeEvent.TICK)
                    this.wait();
                cascadeWrite();
            }
        } catch (InterruptedException ex) {
            while (!controller.chronicle.isEmpty()) {
                cascadeWrite();
            }
            try {
                descriptor.close();
            } catch (IOException exception) {
                Platform.runLater(() ->
                        controller.showErrorMessage("Replay file close error", exception.getMessage()));
            }
            Platform.runLater(() -> controller.replaySaveButton.setDisable(false));
        }
    }

    private void cascadeWrite() {
        try {
            descriptor.saveEvent(controller.chronicle.poll());
        } catch (Exception ex) {
            Platform.runLater(() ->
                    controller.showErrorMessage("Replay save error occurred", ex.getMessage()));
        }
    }
}
