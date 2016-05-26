package life.threads;

import javafx.application.Platform;
import life.core.Board;
import life.gui.MainController;
import life.util.FileInterface;

public class GridLoaderThread extends AbstractControllerThread {
    private final static String savePath = "saves/";
    private String fileName;

    public GridLoaderThread(MainController mainController, String fileName) {
        super(mainController);
        this.fileName = fileName;
    }

    @Override
    public void run() {

        try {
            FileInterface descriptor = new FileInterface(FileInterface.READ_MODE, savePath + fileName);
            Platform.runLater(() -> mainController.savesList.getSelectionModel().clearSelection());
            if (fileName == null) {
                return;
            }
            Board temp = descriptor.loadBoard();
            descriptor.close();
            synchronized (MainController.criticalZone) {
                mainController.board.injectBoard(temp, 0, 0);
                Platform.runLater(() -> mainController.display.displayBoard(mainController.board));
            }
        } catch (Exception ex) {
            Platform.runLater(() -> mainController.showErrorMessage("Load error occurred", ex.getMessage()));
        }
    }
}
