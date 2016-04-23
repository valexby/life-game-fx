package life.Threads;

import javafx.application.Platform;
import life.core.Board;
import life.Util.FileInterface;
import life.gui.Controller;

public class GridLoaderThread extends AbstractControllerThread {
    private String fileName;
    private final static String savePath = "saves/";

    public GridLoaderThread(Controller controller, String fileName) {
        super(controller);
        this.fileName = fileName;
    }

    @Override
    public void run() {

        try {
            FileInterface descriptor = new FileInterface(FileInterface.READ_MODE, savePath + fileName);
            Platform.runLater(() -> controller.savesList.getSelectionModel().clearSelection());
            if (fileName == null)
                return;
            Board temp = descriptor.loadBoard();
            descriptor.close();
            synchronized (controller.board) {
                controller.board.injectBoard(temp, 0, 0);
                controller.display.displayBoard(controller.board);
            }
        } catch (Exception ex) {
            Platform.runLater(() -> controller.showErrorMessage("Load error occurred", ex.getMessage()));
        }
    }
}
