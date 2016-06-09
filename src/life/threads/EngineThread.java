package life.threads;

import javafx.application.Platform;
import life.gui.MainController;
import life.util.LifeEvent;

/**
 * Thread class, provides game board update with setted frequency
 */
public class EngineThread extends AbstractFrequencyThread {
    private final static long engineMaxFrequency = 300;

    /**
     * Main controller
     * @param mainController controller to connect
     */
    public EngineThread(MainController mainController) {
        super(mainController);
        maxFrequency = engineMaxFrequency;
    }

    protected boolean process() throws InterruptedException {
        synchronized (MainController.criticalZone) {
            mainController.board.update();
            Platform.runLater(() -> mainController.display.displayBoard(mainController.board));
        }
        if (mainController.replaySaveFlag) {
            synchronized (MainController.criticalReplayZone) {
                mainController.chronicle.put(new LifeEvent(LifeEvent.TICK, 0, 0, 0));
            }
        }
        return true;
    }
}
