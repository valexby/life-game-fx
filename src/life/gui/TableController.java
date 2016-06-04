package life.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.StringJoiner;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import life.util.Chronicle;
import life.util.MapObserve;
import life.util.ScalaStatistic;
import life.util.SceneSaver;
import scala.Tuple3;
import scala.collection.JavaConversions;
import scala.collection.JavaConversions.*;

public class TableController implements Initializable {

    public void showErrorMessage(String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private Button backButton;

    @FXML
    private TableView mapTable;

    @FXML
    private TableColumn<MapObserve, Integer> numberColumn, ticksColumn, clicksColumn, botsColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stack<Chronicle> buffer = new Stack<>();
        Tuple3<Integer, Integer, Integer>[] map;
        File directory = new File(MainController.replayPath);
        if (!directory.canRead()) {
            showErrorMessage("Saves directory error", directory.getPath());
            return;
        }
        Chronicle[] temp = new Chronicle[directory.list().length];
        for (int i = 0;i< directory.list().length;i++) {
            try {
                temp[i] = new Chronicle(MainController.replayPath+directory.list()[i]);
            } catch (Exception ex) {
                showErrorMessage("Saves directory error", directory.getPath());
            }
        }
        numberColumn.setCellValueFactory(x -> x.getValue().indexProperty());
        ticksColumn.setCellValueFactory(x -> x.getValue().ticksProperty());
        clicksColumn.setCellValueFactory(x -> x.getValue().clicksProperty());
        botsColumn.setCellValueFactory(x -> x.getValue().botsProperty());
        mapTable.setItems(FXCollections.observableArrayList(
                JavaConversions.seqAsJavaList(new ScalaStatistic().getStatistic(temp))));
    }

    @FXML
    private void onBack(Event evt) {
        Stage stage;
        Parent root;
        stage = (Stage)backButton.getScene().getWindow();
        stage.setScene(SceneSaver.getInstance().getMainScene());
    }
}
