package life.threads;

import java.util.ArrayList;

import javafx.application.Platform;
import life.gui.MainController;
import life.util.FileInterface;

/**
 * Thread class, which provides asynchronous function to save game board in file.
 */
public class GridSaverThread extends AbstractControllerThread {
    private String fileName;

    /**
     * Main constructor
     * @param mainController controller to connect with this thread
     * @param fileName name of save file
     */
    public GridSaverThread(MainController mainController, String fileName) {
        super(mainController);
        this.fileName = fileName;
    }

    @Override
    public void run() {
        int cols, rows;
        ArrayList<Boolean> buffer;
        try {
            FileInterface descriptor = new FileInterface(FileInterface.WRITE_MODE, fileName);
            synchronized (MainController.criticalZone) {
                rows = mainController.board.getRows();
                cols = mainController.board.getCols();
                buffer = new ArrayList<>(rows * cols);
                mainController.board.getGrid().stream().forEach(i -> i.forEach(j -> buffer.add(j.getState())));
            }
            descriptor.saveGrid(buffer, rows, cols);
            descriptor.close();
            if (mainController.savesListThread.isAlive()) {
                mainController.savesListThread.join();
                mainController.savesListThread = new SavesListThread(mainController,
                        MainController.savePath, mainController.savesList);
            }
            mainController.savesListThread.run();
        } catch (Exception ex) {
            Platform.runLater(() -> mainController.showErrorMessage("Save error occurred", ex.getMessage()));
        }
    }
}
