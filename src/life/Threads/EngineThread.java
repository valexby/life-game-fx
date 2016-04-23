package life.Threads;

import javafx.application.Platform;
import life.gui.Controller;

public class EngineThread extends AbstractFrequencyThread {
    private final static long engineMaxFrequency = 300;

    public EngineThread(Controller controller) {
        super(controller);
        maxFrequency = engineMaxFrequency;
    }

    void process() {
        synchronized (controller.board) {
            Platform.runLater(() -> {
                controller.board.update();
                controller.display.displayBoard(controller.board);
            });
        }
    }
}
