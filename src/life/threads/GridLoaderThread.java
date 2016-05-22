package life.threads;

import javafx.application.Platform;
import life.core.Board;
import life.gui.Controller;
import life.util.FileInterface;

public class GridLoaderThread extends AbstractControllerThread {
    private final static String savePath = "saves/";
    private String fileName;

    public GridLoaderThread(Controller controller, String fileName) {
        super(controller);
        this.fileName = fileName;
    }

    @Override
    public void run() {

        try {
            FileInterface descriptor = new FileInterface(FileInterface.READ_MODE, savePath + fileName);
            Platform.runLater(() -> controller.savesList.getSelectionModel().clearSelection());
            if (fileName == null) {
                return;
            }
            Board temp = descriptor.loadBoard();
            descriptor.close();
            synchronized (Controller.criticalZone) {
                controller.board.injectBoard(temp, 0, 0);
                Platform.runLater(() -> controller.display.displayBoard(controller.board));
            }
        } catch (Exception ex) {
            Platform.runLater(() -> controller.showErrorMessage("Load error occurred", ex.getMessage()));
        }
    }
}
