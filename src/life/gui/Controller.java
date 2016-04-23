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
import life.core.FileInterface;
import life.core.Spawner;

public class Controller implements Initializable {

    private final static int horizontalBorderSz = 243, verticalBorderSz = 15, cellSpacePx = 5, cellSizePx = 4;
    private final static long engineMaxFrequency = 300, botMaxFrequency = 3000;
    private final static String savePath = "saves";
    EngineThread engineThread = new EngineThread();
    BotThread botThread = new BotThread();
    @FXML
    private FlowPane base;
    @FXML
    private Button lifeButton, botButton;
    @FXML
    private HBox rootBox;
    @FXML
    private Slider densitySlider, engineFreqSlider, botFreqSlider;
    @FXML
    private TextField saveField;
    @FXML
    private ListView<String> savesList;
    private Board board;
    private DisplayDriver display;
    private GridLoadThread gridLoadThread = null;
    private GridSaveThread gridSaveThread = null;
    private SavesListThread savesListThread = null;

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
        try {
            Spawner.init();
        } catch (Exception ex) {
            showErrorMessage("Bot Error", ex.getMessage());
        }
        savesListThread = new SavesListThread();
        savesListThread.run();
    }

    @FXML
    private void onLifeControl(Event evt) {
        if (engineThread.isAlive()) {
            lifeButton.setText("Run");
            engineThread.interrupt();
            if (botThread.isAlive()) onBotControl(evt);
            botButton.setDisable(true);
        } else {
            lifeButton.setText("Stop");
            engineThread = new EngineThread();
            engineThread.setFrequency(Math.round(engineFreqSlider.getValue()));
            engineThread.start();
            botButton.setDisable(false);
        }
    }

    @FXML
    private void onBotControl(Event evt) {
        if (botThread.isAlive()) {
            botButton.setText("Run Bot");
            botThread.interrupt();
        } else {
            botButton.setText("Stop Bot");
            botThread = new BotThread();
            botThread.setFrequency(Math.round(botFreqSlider.getValue()));
            botThread.start();
        }
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
    private void onEngineFreqChanged(Event evt) {
        engineThread.setFrequency(Math.round(engineFreqSlider.getValue()));
    }

    @FXML
    private void onBotFreqChanged(Event evt) {
        botThread.setFrequency(Math.round(botFreqSlider.getValue()));
    }

    @FXML
    private void onClean(Event evt) {
        if (engineThread.isAlive())
            onLifeControl(evt);
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

    private void createDisplay() {
        display = new DisplayDriver(cellSizePx, board);
        base.getChildren().clear();
        base.getChildren().add(new Group(display.getPane()));
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

    @FunctionalInterface
    private interface SizeSetter {
        void resize(int size);
    }

    @FunctionalInterface
    private interface SizeGetter {
        int getSize();
    }

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
            File saveDirectory = new File(savePath);
            if (!saveDirectory.canRead()) {
                if (!saveDirectory.mkdir())
                    Platform.runLater(() -> showErrorMessage("Saves directory error", saveDirectory.getPath()));
                return;
            }
            ObservableList<String> buffer = FXCollections.observableArrayList();
            buffer.addAll(saveDirectory.list());
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
                    board.getGrid().stream().forEach(i -> i.forEach(j -> buffer.add(j.getState())));
                }
                FileInterface.saveGrid(savePath + '/' + fileName, buffer, rows, cols);
                savesListThread.run();
            } catch (Exception ex) {
                Platform.runLater(() -> showErrorMessage("Save error occurred", ex.getMessage()));
            }
        }
    }

    abstract class AbstractFrequencyThread extends Thread {
        protected long currentFrequency = 1, maxFrequency;

        public void setFrequency(long newFrequency) {
            currentFrequency = newFrequency;
        }

        abstract void process() throws Exception;

        @Override
        public void run() {
            while (true) {
                try {
                    process();
                } catch (Exception ex) {
                    break;
                }
                try {
                    sleep(Math.round(maxFrequency / currentFrequency));
                } catch (InterruptedException ex) {
                    return;
                }
            }
        }
    }

    class EngineThread extends AbstractFrequencyThread {
        public EngineThread() {
            maxFrequency = engineMaxFrequency;
        }

        void process() {
            synchronized (board) {
                Platform.runLater(() -> {
                    board.update();
                    display.displayBoard(board);
                });
            }
        }
    }

    class BotThread extends AbstractFrequencyThread {
        public BotThread() {
            maxFrequency = botMaxFrequency;
        }

        void process() throws Exception {
            Board invader;
            try {
                invader = Spawner.spawn();
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    showErrorMessage("Bot error", ex.getMessage());
                });
                throw ex;
            }
            int rows, cols;
            rows = (int) Math.round(Math.random() * board.getRows());
            cols = (int) Math.round(Math.random() * board.getCols());
            synchronized (board) {
                board.injectBoard(invader, rows, cols);
            }
        }
    }
}