package sample;

/**
 * Created by valex on 23.3.16.
 */
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import sample.core.Board;

public class Controller implements Initializable {

    private final int DEFAULT_SIZE = 15;
    private final double DEFAULT_PROB = 0.3;

    @FXML
    private FlowPane base;
    @FXML
    private Button runButton, stopButton;

    @FXML
    private HBox rootBox;

    @FXML
    private TextField densityField, speedField;

    private Board board;

    private DisplayDriver display;

    private Timeline loop = null;

    private int windowWidth = 750;
    private int cellSizePx = 30;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createBoard(DEFAULT_SIZE, DEFAULT_PROB);
        attachResizeListener();
    }

    @FXML
    private void onRun(Event evt) {
        toggleButtons(false);
        double speed = Double.parseDouble(speedField.getText());
        loop = new Timeline(new KeyFrame(Duration.millis(speed), event -> {
            board.update();
            display.displayBoard(board);
        }));

        loop.setCycleCount(1000);
        loop.play();
    }

    @FXML
    private void onStop(Event evt) {
        toggleButtons(true);
        loop.stop();
    }

    @FXML
    private void onGenerate(Event evt) {
        board.generate(Double.parseDouble(densityField.getText()) / 100);
        display.displayBoard(board);
    }

    private void toggleButtons(boolean enable) {
        runButton.setDisable(!enable);

        stopButton.setDisable(enable);
    }

    private void createBoard(int size, double prob) {
        board = new Board(size, size, prob);
        createDisplay();
    }

    private void createDisplay() {
        display = new DisplayDriver(board.getSize(), cellSizePx, board);

        base.getChildren().clear();
        base.getChildren().add(new Group(display.getPane()));
    }

    private void attachResizeListener() {
        ChangeListener<Number> sizeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int newWidth = newValue.intValue();
                if (newWidth > 250 && Math.abs(newWidth - windowWidth) >= 50) {
                    windowWidth = newWidth;
                    cellSizePx = newWidth / 25;
                    createDisplay();
                }
            }
        };
        rootBox.widthProperty().addListener(sizeListener);
    }
}