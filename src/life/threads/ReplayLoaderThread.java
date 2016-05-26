package life.threads;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import life.core.Cell;
import life.gui.MainController;
import life.util.Chronicle;
import life.util.FileInterface;
import life.util.LifeEvent;

public class ReplayLoaderThread extends AbstractFrequencyThread {

    private boolean initialized = false;
    private String replayPath;
    private LifeEvent current;
    private Chronicle requests;

    public ReplayLoaderThread(MainController mainController, String fileName) {
        super(mainController);
        maxFrequency = 300;
        currentFrequency = 3;
        replayPath = fileName;
    }

    public void initialize() {
        try {
            requests = new Chronicle(replayPath);
            mainController.board.injectBoard(requests.getBoard(), 0, 0);
        } catch (Exception ex) {
            Platform.runLater(() -> {
                mainController.showErrorMessage("Replay file load error", ex.getMessage());
                mainController.releaseControl();
                synchronized (MainController.criticalZone) {
                    Platform.runLater(() -> mainController.display.displayBoard(mainController.board));
                }
            });
        }
        initialized = true;
    }

    protected boolean process() throws Exception {
        if (!initialized) initialize();
        if (requests.isEmpty() && current == null) {
            Platform.runLater(() -> mainController.releaseControl());
            initialized = false;
            return false;
        }
        if (current == null)
            current = requests.poll();
        switch (current.getType()) {
            case LifeEvent.TICK:
                synchronized (MainController.criticalZone) {
                    mainController.board.update();
                    Platform.runLater(() -> mainController.display.displayBoard(mainController.board));
                }
                current.tick();
                if (current.getNumber() == 0) {
                    current = null;
                }
                break;
            case LifeEvent.BOT:
                try {
                    synchronized (MainController.criticalZone) {
                        mainController.board.injectBoard(mainController.bot.getBotUnit(current.getNumber()),
                                current.getRow(), current.getCol());
                        Platform.runLater(() -> mainController.display.displayBoard(mainController.board));
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        mainController.releaseControl();
                        mainController.showErrorMessage("Replay load error occurred", ex.getMessage());
                    });
                    initialized = false;
                    return false;
                }
                current = null;
                break;
            case LifeEvent.CLICK:
                synchronized (MainController.criticalZone) {
                    Cell cell = mainController.board.getGrid().get(current.getRow()).get(current.getCol());
                    Color newColor = cell.getState() ? Color.WHITE : Color.STEELBLUE;
                    int position = current.getRow() * mainController.board.getCols() + current.getCol();
                    if (mainController.engineThread == null || !mainController.engineThread.isAlive()) {
                        Rectangle currentRect = ((Rectangle) mainController.display.getPane().getChildren().
                                get(position));
                        Platform.runLater(() -> currentRect.setFill(newColor));
                    }
                    cell.setNewState(!cell.getState());
                    cell.updateState();
                    current = null;
                }
        }
        return true;
    }

}
