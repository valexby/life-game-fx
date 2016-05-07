package life.Threads;

import javafx.application.Platform;
import life.Util.Chronicle;
import life.Util.LifeEvent;
import life.gui.Controller;

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

    abstract LifeEvent process(boolean gather) throws Exception;

    @Override
    public void run() {
        while (true) {
            try {
                if (controller.replaySaveFlag) {
                    controller.chronicle.put(process(true));
                } else {
                    process(false);
                }
            } catch (Exception ex) {
                Platform.runLater(() -> controller.showErrorMessage("Thread error occurred", ex.getMessage()));
            }
            try {
                sleep(Math.round(maxFrequency / currentFrequency));
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
}
