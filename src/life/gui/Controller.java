package life.gui;

/**
 * Created by valex on 23.3.16.
 */

import java.beans.Expression;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
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
import javafx.util.Duration;
import life.core.Board;
import life.core.FileInterface;

public class Controller implements Initializable {

    final static private int horizontalBorderSz = 243, verticalBorderSz = 15, cellSpacePx = 4, cellSizePx = 3,
            maxFrequency = 300;

    @FXML
    private FlowPane base;
    @FXML
    private Button runButton, stopButton;

    @FXML
    private HBox rootBox;

    @FXML
    private Slider densitySlider, freqSlider;

    private Board board;

    private FileInterface fileInterface;

    private DisplayDriver display;

    private Timeline loop = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        board = new Board();
        fileInterface = new FileInterface();
        createDisplay();
        attachResizeListeners();
    }

    @FXML
    private void onRun(Event evt) {
        toggleButtons(false);
        loopStart(null);
    }

    @FXML
    private void onStop(Event evt) {
        toggleButtons(true);
        loop.stop();
    }

    @FXML
    private void onGenerate(Event evt) {
        board.generate(densitySlider.getValue());
        display.displayBoard(board);
    }

    @FXML
    private void onFreqChanged(Event evt) {
        if (loop.getStatus() == Animation.Status.RUNNING)
            loopStart(loop);
    }

    @FXML
    private void onClean(Event evt) {
        if (loop != null && loop.getStatus() == Animation.Status.RUNNING)
            onStop(evt);
        board.resetGrid();
        display.displayBoard(board);
    }

    @FXML
    private void onLoad(Event evt) {
        try {
            board = fileInterface.loadBoard("File");
            createDisplay();
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText("Load error occurred");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        createDisplay();
    }

    @FXML
    private void onSave(Event evt) {
        try { fileInterface.saveGrid("File", board.getGrid()); }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("Save error occurred");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private void toggleButtons(boolean enable) {
        runButton.setDisable(!enable);
        stopButton.setDisable(enable);
    }

    private void loopStart(Timeline oldLoop) {
        loop = new Timeline(new KeyFrame(Duration.millis(maxFrequency / freqSlider.getValue()), event -> {
            board.update();
            display.displayBoard(board);
        }));

        loop.setCycleCount(Animation.INDEFINITE);
        if (oldLoop != null) oldLoop.stop();
        loop.play();
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

    private void resizeInterface(Number oldValue, Number newValue, int borderSize, SizeGetter getter, SizeSetter setter){
        int newSize = newValue.intValue() - borderSize;
        if (newSize > 0 && Math.abs(newSize / cellSpacePx - getter.getSize()) > 0) {
            setter.resize(newSize / cellSpacePx);
            display = new DisplayDriver(cellSizePx, board);

            base.getChildren().clear();
            base.getChildren().add(new Group(display.getPane()));
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