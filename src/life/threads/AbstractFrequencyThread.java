package life.threads;

import javafx.application.Platform;
import life.gui.Controller;
import life.util.Chronicle;

public abstract class AbstractFrequencyThread extends AbstractControllerThread {
    protected long currentFrequency, maxFrequency;
    protected Chronicle chronicle;

    public AbstractFrequencyThread(Controller controller) {
        super(controller);
    }

    public AbstractFrequencyThread(Controller controller, Chronicle chronicle) {
        super(controller);
        this.chronicle = chronicle;
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
                Platform.runLater(() -> controller.showErrorMessage("Thread error occurred", ex.getMessage()));
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
