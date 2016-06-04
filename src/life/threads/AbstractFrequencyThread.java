package life.threads;

import javafx.application.Platform;
import life.gui.MainController;

public abstract class AbstractFrequencyThread extends AbstractControllerThread {
    protected long currentFrequency, maxFrequency;

    public AbstractFrequencyThread(MainController mainController) {
        super(mainController);
    }

    public void setFrequency(long newFrequency) {
        currentFrequency = newFrequency;
    }

    protected abstract boolean process() throws Exception;

    @Override
    public void run() {
        while (true) {
            try {
                if (!process())
                    return;
            } catch (Exception ex) {
                Platform.runLater(() -> mainController.showErrorMessage("Thread error occurred", ex.getMessage()));
                break;
            }
            try {
                sleep(Math.round(maxFrequency / currentFrequency));
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
}
