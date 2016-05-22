package life.threads;

import javafx.application.Platform;
import life.core.Board;
import life.gui.Controller;
import life.util.LifeEvent;

public class BotThread extends AbstractFrequencyThread {
    private final static long botMaxFrequency = 3000;

    public BotThread(Controller controller) {
        super(controller);
        maxFrequency = botMaxFrequency;
    }

    protected boolean process() throws Exception {
        Board invader;
        try {
            invader = controller.bot.spawn();
        } catch (Exception ex) {
            Platform.runLater(() -> controller.showErrorMessage("BotThread error", ex.getMessage()));
            throw ex;
        }
        int rows, cols;
        rows = (int) Math.round(Math.random() * controller.board.getRows());
        cols = (int) Math.round(Math.random() * controller.board.getCols());
        synchronized (Controller.criticalZone) {
            controller.board.injectBoard(invader, rows, cols);
        }
        if (controller.replaySaveFlag) {
            synchronized (Controller.criticalReplayZone) {
                controller.chronicle.put(new LifeEvent(LifeEvent.BOT, rows, cols, controller.bot.lastSpawnedIndex()));
            }
        }
        return true;
    }
}

