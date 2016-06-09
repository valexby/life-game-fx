package life.threads;

import javafx.application.Platform;
import life.core.Board;
import life.gui.MainController;
import life.util.LifeEvent;

/**
 * Artificial intelligence thread. Spawns figures from data/ to game board.
 */
public class BotThread extends AbstractFrequencyThread {
    private final static long botMaxFrequency = 3000;

    /**
     *
     * @param mainController controller with board? where new figures will appear.
     */
    public BotThread(MainController mainController) {
        super(mainController);
        maxFrequency = botMaxFrequency;
    }

    protected boolean process() throws Exception {
        Board invader;
        try {
            invader = mainController.bot.spawn();
        } catch (Exception ex) {
            Platform.runLater(() -> mainController.showErrorMessage("BotThread error", ex.getMessage()));
            throw ex;
        }
        int rows, cols;
        rows = (int) Math.round(Math.random() * mainController.board.getRows());
        cols = (int) Math.round(Math.random() * mainController.board.getCols());
        synchronized (MainController.criticalZone) {
            mainController.board.injectBoard(invader, rows, cols);
        }
        if (mainController.replaySaveFlag) {
            synchronized (MainController.criticalReplayZone) {
                mainController.chronicle.put(new LifeEvent(LifeEvent.BOT, rows, cols, mainController.bot.lastSpawnedIndex()));
            }
        }
        return true;
    }
}

