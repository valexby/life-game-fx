package life.Threads;

import javafx.application.Platform;
import life.Util.Chronicle;
import life.Util.LifeEvent;
import life.gui.Controller;

public class EngineThread extends AbstractFrequencyThread {
    private final static long engineMaxFrequency = 300;

    public EngineThread(Controller controller) {
        super(controller);
        maxFrequency = engineMaxFrequency;
    }

    public EngineThread(Controller controller, Chronicle chronicle) {
        super(controller, chronicle);
        maxFrequency = engineMaxFrequency;
    }

    LifeEvent process(boolean gather) {
        synchronized (controller.board) {
            Platform.runLater(() -> {
                controller.board.update();
                controller.display.displayBoard(controller.board);
            });
        }
        if (gather) {
            return new LifeEvent(LifeEvent.TICK, 0, 0, 0);
        }
        return null;
    }
}
