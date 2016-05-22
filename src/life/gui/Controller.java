package life.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import life.core.Board;
import life.threads.BotThread;
import life.threads.EngineThread;
import life.threads.GridLoaderThread;
import life.threads.GridSaverThread;
import life.threads.ReplayLoaderThread;
import life.threads.ReplaySaverThread;
import life.threads.SaveGenerator;
import life.threads.SavesListThread;
import life.util.Bot;
import life.util.Chronicle;
import life.util.Sorter;

public class Controller implements Initializable {

    public static final Object criticalZone = new Object(), criticalReplayZone = new Object();
    private final static int horizontalBorderSz = 243, verticalBorderSz = 15, cellSpacePx = 5, cellSizePx = 4;
    private final static String savePath = "saves/", replayPath = "replay/test";
    @FXML
    public ListView<String> savesList;
    public Board board;
    public DisplayDriver display;
    public Bot bot;
    public SavesListThread savesListThread = null;
    public Chronicle chronicle = null;
    public boolean replaySaveFlag = false;
    @FXML
    public Button replaySaveButton, replayShowButton, savesSortButton;
    ReplaySaverThread replaySaverThread = new ReplaySaverThread(this, replayPath);
    ReplayLoaderThread replayLoaderThread = new ReplayLoaderThread(this, replayPath);
    public EngineThread engineThread = new EngineThread(this);
    BotThread botThread = new BotThread(this);
    SaveGenerator saveGenerator = new SaveGenerator(this);
    @FXML
    private FlowPane base;
    @FXML
    private Button lifeButton, botButton, generateButton, cleanButton;
    @FXML
    private HBox rootBox;
    @FXML
    private Slider densitySlider, engineFreqSlider, botFreqSlider;
    @FXML
    private TitledPane saveMenu, loadMenu;
    @FXML
    private CheckBox saveGeneratorBox;
    @FXML
    private TextField saveField;
    @FXML
    private ChoiceBox<Sorter.SortMod> choiceMod;
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
        choiceMod.setItems(FXCollections.observableArrayList(
                Sorter.SortMod.NO_MOD, Sorter.SortMod.JAVA_MOD, Sorter.SortMod.SCALA_MOD));
        choiceMod.setValue(Sorter.SortMod.NO_MOD);
    }

    @FXML
    private void onLifeControl(Event evt) {
        if (engineThread.isAlive()) {
            lifeButton.setText("Run");
            engineThread.interrupt();
            if (saveGeneratorBox.isSelected()) {
                saveGenerator.interrupt();
            }
            if (botThread.isAlive()) {
                onBotControl(evt);
            }
            botButton.setDisable(true);
            saveGeneratorBox.setDisable(false);
            try {
                engineThread.join();
                botThread.join();
                if (saveGeneratorBox.isSelected()) {
                    saveGenerator.join();
                }
            } catch (InterruptedException ex) {
                showErrorMessage("Unexpected main thread kill", ex.getMessage());
            }
        } else {
            lifeButton.setText("Stop");
            engineThread = new EngineThread(this);
            if (saveGeneratorBox.isSelected()) {
                saveGenerator = new SaveGenerator(this);
            }
            engineThread.setFrequency(Math.round(engineFreqSlider.getValue()));
            engineThread.start();
            if (saveGeneratorBox.isSelected()) {
                saveGenerator.start();
            }
            botButton.setDisable(false);
            saveGeneratorBox.setDisable(true);
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
        synchronized (criticalZone) {
            board.generate(densitySlider.getValue());
        }
        if (!engineThread.isAlive()) {
            display.displayBoard(board);
        }
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
        if (engineThread.isAlive()) {
            onLifeControl(evt);
        }
        board.resetGrid();
        display.displayBoard(board);
    }

    @FXML
    private void onLoad(Event evt) {
        if (gridLoadThread != null && gridLoadThread.isAlive()) {
            return;
        }
        gridLoadThread = new GridLoaderThread(this, savesList.getSelectionModel().getSelectedItem());
        gridLoadThread.start();
    }

    @FXML
    private void onSave(Event evt) {
        if (gridSaveThread != null && gridSaveThread.isAlive()) {
            return;
        }
        String fileName = saveField.getText();
        gridSaveThread = new GridSaverThread(this, savePath + fileName);
        gridSaveThread.start();
    }

    @FXML
    private void onSaveDelete(Event evt) {
        if (!new File(savePath + savesList.getSelectionModel().getSelectedItem()).delete()) {
            showErrorMessage("Delete error", "Unable to delete save file");
        }
        String buffer = savesList.getSelectionModel().getSelectedItem();
        savesList.setEditable(false);
        savesList.getItems().remove(buffer);
        savesList.setEditable(true);
    }

    @FXML
    private void onReplaySave(Event evt) {
        replaySaveFlag = !replaySaveFlag;
        if (!replaySaveFlag) {
            replaySaveButton.setDisable(true);
            replayShowButton.setDisable(false);
            replaySaveButton.setText("Write");
            replaySaverThread.interrupt();
            setDisableNotReplayAble(false);
        } else {
            replaySaveButton.setText("Stop");
            setDisableNotReplayAble(true);
            replayShowButton.setDisable(true);
            replaySaverThread = new ReplaySaverThread(this, replayPath);
            chronicle = new Chronicle(replaySaverThread);
            replaySaverThread.start();
        }
    }

    @FXML
    private void onShowReplay(Event evt) {
        if (replayLoaderThread.isAlive()) {
            replayLoaderThread.interrupt();
            try {
                replayLoaderThread.join();
            } catch (InterruptedException ex) {
                showErrorMessage("Unexpected main thread kill", ex.getMessage());
            }
            releaseControl();
        } else {
            if (engineThread.isAlive()) {
                onLifeControl(evt);
            }
            setDisableNotReplayAble(true);
            lifeButton.setDisable(true);
            replaySaveButton.setDisable(true);
            replayShowButton.setText("Stop");
            replayLoaderThread = new ReplayLoaderThread(this, replayPath);
            replayLoaderThread.start();
        }
    }

    @FXML
    private void onSavesSort(Event evt) {
        File saveDirectory = new File(savePath);
        if (!saveDirectory.canRead()) {
            showErrorMessage("Saves directory error", saveDirectory.getPath());
            return;
        }
        savesSortButton.setText("Wait...");
        try {
            Sorter sorter = new Sorter(saveDirectory.list(), choiceMod.getValue());
            ObservableList<String> buffer = FXCollections.observableArrayList(sorter.filesSort());
            savesList.setItems(buffer);
        } catch (Exception ex) {
            showErrorMessage("Saves sort error", saveDirectory.getPath());
        }
        savesSortButton.setText("Sort Saves");
    }

    public void releaseControl() {
        setDisableNotReplayAble(false);
        lifeButton.setDisable(false);
        replaySaveButton.setDisable(false);
        replayShowButton.setText("Show");
    }

    private void setDisableNotReplayAble(boolean state) {
        generateButton.setDisable(state);
        cleanButton.setDisable(state);
        saveMenu.setDisable(state);
        loadMenu.setDisable(state);
    }

    private void createDisplay() {
        display = new DisplayDriver(this, cellSizePx, board);
        base.getChildren().clear();
        base.getChildren().add(new Group(display.getPane()));
    }

    private void resizeInterface(Number newValue, int borderSize, SizeGetter getter, SizeSetter setter) {
        int newSize = newValue.intValue() - borderSize;
        synchronized (criticalZone) {
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
            resizeInterface(newValue, horizontalBorderSz, getter, setter);
        });
        rootBox.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            SizeGetter getter = board::getRows;
            SizeSetter setter = board::setRows;
            resizeInterface(newValue, verticalBorderSz, getter, setter);
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