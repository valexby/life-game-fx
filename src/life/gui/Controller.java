package life.gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import life.core.Board;
import life.core.Cell;
import life.core.FileInterface;

public class Controller implements Initializable {

    private final static int horizontalBorderSz = 243, verticalBorderSz = 15, cellSpacePx = 4, cellSizePx = 3;
    private final static long maxFrequency = 300;
    private final static String savePath = "saves";

    @FXML
    private FlowPane base;
    @FXML
    private Button runButton, stopButton;

    @FXML
    private HBox rootBox;

    @FXML
    private Slider densitySlider, freqSlider;

    @FXML
    private TextField saveField;

    @FXML
    private ListView<String> savesList;

    private Board board;

    private DisplayDriver display;

    private GridLoadThread gridLoadThread = null;

    private GridSaveThread gridSaveThread = null;

    private SavesListThread savesListThread = null;

    EngineThread engineThread = new EngineThread();

    private class GridLoadThread extends Thread {

        private String fileName;

        public GridLoadThread(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void run() {

            try {
                Platform.runLater(() -> savesList.getSelectionModel().clearSelection());
                if (fileName == null)
                    return;
                Board temp = FileInterface.loadBoard(savePath + '/' + fileName);
                synchronized (board) {
                    board.injectBoard(temp, 0, 0);
                    display.displayBoard(board);
                }
            } catch (Exception ex) {
                Platform.runLater(() -> showErrorMessage("Load error occurred", ex.getMessage()));
            }
        }
    }

    private class SavesListThread extends Thread {
        @Override
        public void run() {
            File saveDir = new File(savePath);
            if (!saveDir.canRead()) {
                if (!saveDir.mkdir())
                    Platform.runLater(() -> showErrorMessage("Saves directory error", saveDir.getPath()));
                return;
            }
            ObservableList<String> buffer = FXCollections.observableArrayList();
            buffer.addAll(saveDir.list());
            Collections.sort(buffer);
            Platform.runLater(() -> savesList.setItems(buffer));
        }
    }

    private class GridSaveThread extends Thread {

        private String fileName;

        public GridSaveThread(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void run() {
            int cols, rows;
            ArrayList<Boolean> buffer;
            try {
                synchronized (board) {
                    rows = board.getRows();
                    cols = board.getCols();
                    buffer = new ArrayList<>(rows * cols);
                    for (ArrayList<Cell> i : board.getGrid())
                        for (Cell j : i)
                            buffer.add(j.getState());
                }
                FileInterface.saveGrid(savePath + '/' + fileName, buffer, rows, cols);
                savesListThread.run();
            } catch (Exception ex) {
                Platform.runLater(() -> showErrorMessage("Save error occurred", ex.getMessage()));
            }
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

    private void showErrorMessage(String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        board = new Board();
        attachResizeListeners();
        savesListThread = new SavesListThread();
        savesListThread.run();
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
        gridLoadThread = new GridLoadThread(savesList.getSelectionModel().getSelectedItem());
        gridLoadThread.start();
    }

    @FXML
    private void onSave(Event evt) {
        if (gridSaveThread != null && gridSaveThread.isAlive()) return;
        String fileName = saveField.getText();
        gridSaveThread = new GridSaveThread(fileName);
        gridSaveThread.start();
    }

    @FXML
    private void onSaveDelete(Event evt) {
        new File(savePath + '/' + savesList.getSelectionModel().getSelectedItem()).delete();
        String buffer = savesList.getSelectionModel().getSelectedItem();
        savesList.setEditable(false);
        savesList.getItems().remove(buffer);
        savesList.setEditable(true);
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