package life.threads;

import javafx.application.Platform;
import life.gui.Controller;
import life.util.LifeEvent;

public class EngineThread extends AbstractFrequencyThread {
    private final static long engineMaxFrequency = 300;

    public EngineThread(Controller controller) {
        super(controller);
        maxFrequency = engineMaxFrequency;
    }

    protected boolean process() throws InterruptedException {
        synchronized (Controller.criticalZone) {
            controller.board.update();
            Platform.runLater(() -> controller.display.displayBoard(controller.board));
        }
        if (controller.replaySaveFlag) {
            synchronized (Controller.criticalReplayZone) {
                controller.chronicle.put(new LifeEvent(LifeEvent.TICK, 0, 0, 0));
            }
        }
        return true;
    }
}
