package life.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import life.util.Chronicle;
import life.util.MapObserve;
import life.util.ScalaStatistic;
import life.util.SceneSaver;
import scala.collection.JavaConversions;

/**
 * Table gui class
 */
public class TableController implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private TableView<MapObserve> mapTable;
    @FXML
    private TableColumn<MapObserve, Integer> numberColumn, ticksColumn, clicksColumn, botsColumn;

    public void showErrorMessage(String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File directory = new File(MainController.replayPath);
        if (!directory.canRead()) {
            showErrorMessage("Saves directory error", directory.getPath());
            return;
        }
        Chronicle[] temp = new Chronicle[directory.list().length];
        for (int i = 0; i < directory.list().length; i++) {
            try {
                temp[i] = new Chronicle(MainController.replayPath + directory.list()[i]);
            } catch (Exception ex) {
                showErrorMessage("Saves directory error", directory.getPath());
            }
        }
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("Index"));
        ticksColumn.setCellValueFactory(new PropertyValueFactory<>("Ticks"));
        clicksColumn.setCellValueFactory(new PropertyValueFactory<>("Clicks"));
        botsColumn.setCellValueFactory(new PropertyValueFactory<>("Bots"));
        ObservableList<MapObserve> usersData = FXCollections.observableArrayList(
                JavaConversions.seqAsJavaList(new ScalaStatistic().getStatistic(temp)));
        mapTable.setItems(usersData);
    }

    @FXML
    private void onBack(Event evt) {
        Stage stage;
        stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(SceneSaver.getInstance().getMainScene());
    }
}
