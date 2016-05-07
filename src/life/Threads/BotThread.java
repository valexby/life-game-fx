package life.Threads;

import javafx.application.Platform;
import life.Util.Chronicle;
import life.Util.LifeEvent;
import life.core.Board;
import life.gui.Controller;

public class BotThread extends AbstractFrequencyThread {
    private final static long botMaxFrequency = 3000;

    public BotThread(Controller controller) {
        super(controller);
        maxFrequency = botMaxFrequency;
    }

    public BotThread(Controller controller, Chronicle chronicle) {
        super(controller, chronicle);
        maxFrequency = botMaxFrequency;
    }

    LifeEvent process(boolean gather) throws Exception {
        Board invader;
        try {
            invader = controller.bot.spawn();
        } catch (Exception ex) {
            Platform.runLater(() -> {
                controller.showErrorMessage("BotThread error", ex.getMessage());
            });
            throw ex;
        }
        int rows, cols;
        rows = (int) Math.round(Math.random() * controller.board.getRows());
        cols = (int) Math.round(Math.random() * controller.board.getCols());
        synchronized (controller.board) {
            controller.board.injectBoard(invader, rows, cols);
        }
        if (gather) {
            return new LifeEvent(LifeEvent.BOT, rows, cols, controller.bot.lastSpawnedIndex());
        }
        return null;
    }
}

