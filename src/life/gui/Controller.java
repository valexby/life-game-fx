package life.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
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
import life.Threads.BotThread;
import life.Threads.EngineThread;
import life.Threads.GridLoaderThread;
import life.Threads.GridSaverThread;
import life.Threads.SavesListThread;
import life.Util.Bot;
import life.core.Board;

public class Controller implements Initializable {

    private final static int horizontalBorderSz = 243, verticalBorderSz = 15, cellSpacePx = 5, cellSizePx = 4;
    private final static String savePath = "saves/";
    @FXML
    public ListView<String> savesList;
    public Board board;
    public DisplayDriver display;
    public Bot bot;
    public SavesListThread savesListThread = null;
    EngineThread engineThread = new EngineThread(this);
    BotThread botThread = new BotThread(this);
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
    private GridLoaderThread gridLoadThread = null;
    private GridSaverThread gridSaveThread = null;

    /**
     * Prints dialog with error message
     */
    public void showErrorMessage(String headerText, String message) {
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
            bot = new Bot();
        } catch (Exception ex) {
            showErrorMessage("BotThread Error", ex.getMessage());
        }
        savesListThread = new SavesListThread(this);
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
            engineThread = new EngineThread(this);
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
            botThread = new BotThread(this);
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
        gridLoadThread = new GridLoaderThread(this, savesList.getSelectionModel().getSelectedItem());
        gridLoadThread.start();
    }

    @FXML
    private void onSave(Event evt) {
        if (gridSaveThread != null && gridSaveThread.isAlive()) return;
        String fileName = saveField.getText();
        gridSaveThread = new GridSaverThread(this, fileName);
        gridSaveThread.start();
    }

    @FXML
    private void onSaveDelete(Event evt) {
        new File(savePath + savesList.getSelectionModel().getSelectedItem()).delete();
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
}