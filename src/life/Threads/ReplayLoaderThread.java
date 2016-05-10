package life.Threads;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

import javafx.application.Platform;
import life.Util.Chronicle;
import life.Util.FileInterface;
import life.Util.LifeEvent;
import life.gui.Controller;

public class ReplayLoaderThread extends AbstractFrequencyThread {

    private boolean initialized = false;
    private String replayPath;
    private LifeEvent current;
    private LinkedBlockingDeque<LifeEvent> requests;

    public ReplayLoaderThread(Controller controller, String fileName) {
        super(controller);
        maxFrequency = 300;
        currentFrequency = 1;
        replayPath = fileName;
    }

    public ReplayLoaderThread(Controller controller, Chronicle chronicle) {
        super(controller, chronicle);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize() {
        FileInterface descriptor = null;
        requests = new LinkedBlockingDeque<>();
        try {
            descriptor = new FileInterface(FileInterface.READ_MODE, replayPath);
            controller.board.injectBoard(descriptor.loadBoard(), 0, 0);
            synchronized (controller.board) {
                Platform.runLater(() -> {
                    controller.display.displayBoard(controller.board);
                });
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
                Platform.runLater(() -> {
                    controller.showErrorMessage("Replay file load error", exception.getMessage());
                });
            }
        }
        initialized = true;
    }

    protected boolean process() throws Exception {
        if (!initialized) initialize();
        if (requests.isEmpty() && current == null) {
            Platform.runLater(() -> {
                controller.releaseControl();
            });
            initialized = false;
            return false;
        }
        if (current == null)
            current = requests.poll();
        switch (current.getType()) {
            case LifeEvent.TICK:
                synchronized (controller.board) {
                    controller.board.update();
                    Platform.runLater(() -> {
                        controller.display.displayBoard(controller.board);
                    });
                }
                current.tick();
                if (current.getNumber() == 0) {
                    current = null;
                }
                break;
            case LifeEvent.BOT:
                try {
                    synchronized (controller.board) {
                        controller.board.injectBoard(controller.bot.getBotUnit(current.getNumber()),
                                current.getRow(), current.getCol());
                        Platform.runLater(() -> {
                            controller.display.displayBoard(controller.board);
                        });
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
        }
        return true;
    }

}
