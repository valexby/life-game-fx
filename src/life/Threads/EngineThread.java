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

    protected boolean process() throws InterruptedException {
        synchronized (controller.board) {
            controller.board.update();
            Platform.runLater(() -> {
                controller.display.displayBoard(controller.board);
            });
        }
        if (controller.replaySaveFlag) {
            controller.chronicle.put(new LifeEvent(LifeEvent.TICK, 0, 0, 0));
        }
        return true;
    }
}
