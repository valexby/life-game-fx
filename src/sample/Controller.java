package sample;

/**
 * Created by valex on 23.3.16.
 */

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
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import sample.core.Board;

public class Controller implements Initializable {

    final static private int horBoardSz = 243, verBoardSz = 15, cellSpacePx = 35, cellSizePx = 30,
            maxFreqency = 300;

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

    private Timeline loop = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        board = new Board();
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

    private void toggleButtons(boolean enable) {
        runButton.setDisable(!enable);

        stopButton.setDisable(enable);
    }

    private void loopStart(Timeline oldLoop){
        loop = new Timeline(new KeyFrame(Duration.millis(maxFreqency / freqSlider.getValue()), event -> {
            board.update();
            display.displayBoard(board);
        }));

        loop.setCycleCount(200);
        if (oldLoop!=null) oldLoop.stop();
        loop.play();
    }

    private void createDisplay() {
        display = new DisplayDriver(cellSizePx, board);

        base.getChildren().clear();
        base.getChildren().add(new Group(display.getPane()));
    }

    private void attachResizeListeners() {
        ChangeListener<Number> widthListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //Проверка, влезет ли еще столбец ячеек на панель
                int newWidth = newValue.intValue() - horBoardSz;
                if (newWidth > 0 && Math.abs(newWidth / cellSpacePx - board.getCols()) > 0) {
                    board.changeCols(newWidth / cellSpacePx);
                    display = new DisplayDriver(cellSizePx, board);

                    base.getChildren().clear();
                    base.getChildren().add(new Group(display.getPane()));
                }
            }
        };
        ChangeListener<Number> heightListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //Проверка, влезет ли еще ряд ячеек на панель
                int newHeight = newValue.intValue() - verBoardSz;
                if (newHeight > 0 && Math.abs(newHeight / cellSpacePx - board.getRows()) > 0) {
                    board.changeRows(newHeight / cellSpacePx);
                    display = new DisplayDriver(cellSizePx, board);

                    base.getChildren().clear();
                    base.getChildren().add(new Group(display.getPane()));
                }
            }
        };
        rootBox.widthProperty().addListener(widthListener);
        rootBox.heightProperty().addListener(heightListener);
    }
}