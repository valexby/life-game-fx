package life.threads;

import life.gui.Controller;

public class SaveGenerator extends AbstractControllerThread {
    private final static long maxFrequency = 100;
    private final static String savePath = "saves/";

    public SaveGenerator(Controller controller) {
        super(controller);
    }

    @Override
    public void run() {
        int newSaveName = 1;
        while (true) {
            GridSaverThread saver = new GridSaverThread(controller, savePath + String.valueOf(newSaveName));
            newSaveName++;
            saver.run();
            try {
                saver.join();
                sleep(maxFrequency);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
}
