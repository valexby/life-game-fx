package life.Threads;

import java.util.ArrayList;

import javafx.application.Platform;
import life.Util.FileInterface;
import life.gui.Controller;

public class GridSaverThread extends AbstractControllerThread {
    private String fileName;
    private final static String savePath = "saves/";

    public GridSaverThread(Controller controller, String fileName) {
        super(controller);
        this.fileName = fileName;
    }

    @Override
    public void run() {
        int cols, rows;
        ArrayList<Boolean> buffer;
        try {
            FileInterface descriptor = new FileInterface(FileInterface.WRITE_MODE, savePath + fileName);
            synchronized (controller.board) {
                rows = controller.board.getRows();
                cols = controller.board.getCols();
                buffer = new ArrayList<>(rows * cols);
                controller.board.getGrid().stream().forEach(i -> i.forEach(j -> buffer.add(j.getState())));
            }
            descriptor.saveGrid(buffer, rows, cols);
            descriptor.close();
            if (controller.savesListThread.isAlive())
            {
                controller.savesListThread.join();
                controller.savesListThread = new SavesListThread(controller);
            }
            controller.savesListThread.run();
        } catch (Exception ex) {
            Platform.runLater(() -> controller.showErrorMessage("Save error occurred", ex.getMessage()));
        }
    }
}
