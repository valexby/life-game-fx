package life.threads;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import life.core.Cell;
import life.gui.Controller;
import life.util.FileInterface;
import life.util.LifeEvent;

public class ReplayLoaderThread extends AbstractFrequencyThread {

    private boolean initialized = false;
    private String replayPath;
    private LifeEvent current;
    private LinkedBlockingDeque<LifeEvent> requests;

    public ReplayLoaderThread(Controller controller, String fileName) {
        super(controller);
        maxFrequency = 300;
        currentFrequency = 3;
        replayPath = fileName;
    }

    public void initialize() {
        FileInterface descriptor = null;
        requests = new LinkedBlockingDeque<>();
        try {
            descriptor = new FileInterface(FileInterface.READ_MODE, replayPath);
            controller.board.injectBoard(descriptor.loadBoard(), 0, 0);
            synchronized (Controller.criticalZone) {
                Platform.runLater(() -> controller.display.displayBoard(controller.board));
            }
            LifeEvent buffer = descriptor.loadEvent();
            while (buffer != null) {
                requests.put(buffer);
                buffer = descriptor.loadEvent();
            }
            descriptor.close();
        } catch (Exception ex) {
            initialized = false;
            Platform.runLater(() -> {
                controller.showErrorMessage("Replay file load error", ex.getMessage());
                controller.releaseControl();
            });
            try {
                descriptor.close();
            } catch (IOException exception) {
                initialized = false;
                Platform.runLater(() ->
                        controller.showErrorMessage("Replay file load error", exception.getMessage()));
            }
        }
        initialized = true;
    }

    protected boolean process() throws Exception {
        if (!initialized) initialize();
        if (requests.isEmpty() && current == null) {
            Platform.runLater(() -> controller.releaseControl());
            initialized = false;
            return false;
        }
        if (current == null)
            current = requests.poll();
        switch (current.getType()) {
            case LifeEvent.TICK:
                synchronized (Controller.criticalZone) {
                    controller.board.update();
                    Platform.runLater(() -> controller.display.displayBoard(controller.board));
                }
                current.tick();
                if (current.getNumber() == 0) {
                    current = null;
                }
                break;
            case LifeEvent.BOT:
                try {
                    synchronized (Controller.criticalZone) {
                        controller.board.injectBoard(controller.bot.getBotUnit(current.getNumber()),
                                current.getRow(), current.getCol());
                        Platform.runLater(() -> controller.display.displayBoard(controller.board));
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        controller.releaseControl();
                        controller.showErrorMessage("Replay load error occurred", ex.getMessage());
                    });
                    initialized = false;
                    return false;
                }
                current = null;
                break;
            case LifeEvent.CLICK:
                synchronized (Controller.criticalZone) {
                    Cell cell = controller.board.getGrid().get(current.getRow()).get(current.getCol());
                    Color newColor = cell.getState() ? Color.WHITE : Color.STEELBLUE;
                    int position = current.getRow() * controller.board.getCols() + current.getCol();
                    if (!controller.engineThread.isAlive()) {
                        Rectangle currentRect = ((Rectangle) controller.display.getPane().getChildren().
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
