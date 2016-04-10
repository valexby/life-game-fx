package life.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import life.core.Board;
import life.core.Cell;
import life.core.FileInterface;

public class Controller implements Initializable {

    private final static int horizontalBorderSz = 243, verticalBorderSz = 15, cellSpacePx = 4, cellSizePx = 3;
    private final static long maxFrequency = 300;

    @FXML
    private FlowPane base;
    @FXML
    private Button runButton, stopButton;

    @FXML
    private HBox rootBox;

    @FXML
    private Slider densitySlider, freqSlider;

    private Board board;

    private DisplayDriver display;

    private GridLoadThread gridLoadThread = null;

    private GridSaveThread gridSaveThread = null;

    EngineThread engineThread = new EngineThread();

    private class GridLoadThread extends Thread {
        @Override
        public void run() {
            try {
                Board temp = FileInterface.loadBoard("File");
                synchronized (board) {
                    board.injectBoard(temp, 0, 0);
                    display.displayBoard(board);
                }
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Load Error");
                alert.setHeaderText("Load error occurred");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
            return;
        }
    }

    private class GridSaveThread extends Thread {
        @Override
        public void run() {
            int cols, rows;
            ArrayList<Boolean> buffer;
            try {
                synchronized (board) {
                    rows = board.getRows();
                    cols = board.getCols();
                    buffer = new ArrayList<>(rows * cols);
                    for (ArrayList <Cell> i : board.getGrid())
                        for (Cell j : i)
                            buffer.add(j.getState());
                }
                FileInterface.saveGrid("File", buffer, rows, cols);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Error");
                alert.setHeaderText("Save error occurred");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
            return;
        }
    }

    class EngineThread extends Thread {

        private long currentFrequency;

        public void setFrequency(long newFrequency) {
            currentFrequency = newFrequency;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (board) {
                    board.update();
                    display.displayBoard(board);
                }
                try {
                    sleep(Math.round(maxFrequency / currentFrequency));
                } catch (InterruptedException ex) {
                    return;
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        board = new Board();
        attachResizeListeners();
    }

    @FXML
    private void onRun(Event evt) {
        toggleButtons(false);
        engineThread = new EngineThread();
        engineThread.setFrequency(Math.round(freqSlider.getValue()));
        engineThread.start();
    }

    @FXML
    private void onStop(Event evt) {
        toggleButtons(true);
        engineThread.interrupt();
    }

    @FXML
    private void onGenerate(Event evt) {
        synchronized (board) {
            board.generate(densitySlider.getValue());
        }
        if (!engineThread.isAlive())
            display.displayBoard(board);
    }

    @FXML
    private void onFreqChanged(Event evt) {
        engineThread.setFrequency(Math.round(freqSlider.getValue()));
    }

    @FXML
    private void onClean(Event evt) {
        if (engineThread.isAlive())
            onStop(evt);
        board.resetGrid();
        display.displayBoard(board);
    }

    @FXML
    private void onLoad(Event evt) {
        if (gridLoadThread != null && gridLoadThread.isAlive()) return;
        gridLoadThread = new GridLoadThread();
        gridLoadThread.start();
    }

    @FXML
    private void onSave(Event evt) {
        if (gridSaveThread != null && gridSaveThread.isAlive()) return;
        gridSaveThread = new GridSaveThread();
        gridSaveThread.start();
    }

    private void toggleButtons(boolean enable) {
        runButton.setDisable(!enable);
        stopButton.setDisable(enable);
    }

    private void createDisplay() {
        display = new DisplayDriver(cellSizePx, board);
        base.getChildren().clear();
        base.getChildren().add(new Group(display.getPane()));
    }

    @FunctionalInterface
    private interface SizeSetter {
        void resize(int size);
    }

    @FunctionalInterface
    private interface SizeGetter {
        int getSize();
    }

    private void resizeInterface(Number oldValue, Number newValue, int borderSize, SizeGetter getter, SizeSetter setter) {
        int newSize = newValue.intValue() - borderSize;
        synchronized (board) {
            if (newSize > 0 && Math.abs(newSize / cellSpacePx - getter.getSize()) > 0) {
                setter.resize(newSize / cellSpacePx);
                createDisplay();
            }
        }
    }

    private void attachResizeListeners() {
        rootBox.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            SizeGetter getter = board::getCols;
            SizeSetter setter = board::setCols;
            resizeInterface(oldValue, newValue, horizontalBorderSz, getter, setter);
        });
        rootBox.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            SizeGetter getter = board::getRows;
            SizeSetter setter = board::setRows;
            resizeInterface(oldValue, newValue, verticalBorderSz, getter, setter);
        });
    }
}