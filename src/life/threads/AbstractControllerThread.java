package life.threads;

import life.gui.MainController;

public abstract class AbstractControllerThread extends Thread {
    protected volatile MainController mainController;

    public AbstractControllerThread(MainController mainController) {
        this.mainController = mainController;
    }
}
