package life.Threads;

import life.gui.Controller;

public abstract class AbstractFrequencyThread extends AbstractControllerThread {
    protected long currentFrequency, maxFrequency;

    public AbstractFrequencyThread(Controller controller) {
        super(controller);
    }

    public void setFrequency(long newFrequency) {
        currentFrequency = newFrequency;
    }

    abstract void process() throws Exception;

    @Override
    public void run() {
        while (true) {
            try {
                process();
            } catch (Exception ex) {
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
