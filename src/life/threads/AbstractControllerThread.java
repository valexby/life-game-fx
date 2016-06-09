package life.threads;

import life.gui.MainController;
/**
 * Abstract thread class. Base for all threads, who needs access to gui controller.*/
public abstract class AbstractControllerThread extends Thread {
    protected volatile MainController mainController;

    /**
     * Main controller
     * @param mainController controller to connect
     */
    public AbstractControllerThread(MainController mainController) {
        this.mainController = mainController;
    }
}
