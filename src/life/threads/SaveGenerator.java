package life.threads;


import javafx.application.Platform;
import life.gui.MainController;

public class SaveGenerator extends AbstractControllerThread {
    private final static long maxFrequency = 100;
    private final static String savePath = "saves/";

    public SaveGenerator(MainController mainController) {
        super(mainController);
    }

    @Override
    public void run() {
        int newSaveName = 1;
        while (newSaveName < 101) {
            GridSaverThread saver = new GridSaverThread(mainController, savePath + String.valueOf(newSaveName));
            newSaveName++;
            saver.run();
            try {
                saver.join();
                sleep(maxFrequency);
            } catch (InterruptedException ex) {
                return;
            }
        }
        Platform.runLater(() -> mainController.showErrorMessage("done", "done"));
    }
}
