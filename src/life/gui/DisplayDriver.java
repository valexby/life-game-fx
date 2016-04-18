package life.gui;

/**
 * Created by valex on 23.3.16.
 */

import java.util.ArrayList;

import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import life.core.Board;
import life.core.Cell;

public class DisplayDriver {
    private TilePane tilePane;

    public DisplayDriver(int cellSizePx, Board board) {
        ArrayList<ArrayList<Cell>> grid = board.getGrid();
        tilePane = new TilePane(1, 1);
        tilePane.setPrefRows(board.getRows());
        tilePane.setPrefColumns(board.getCols());

        grid.stream().forEach(i -> i.stream().forEach(j -> {
            Color color = j.getState() ? Color.STEELBLUE : Color.WHITE;
            Rectangle rect = new Rectangle(cellSizePx, cellSizePx, color);
            tilePane.getChildren().add(rect);
            attachListeners(rect, j);
        }));
    }

    public void displayBoard(Board board) {
        ArrayList<ArrayList<Cell>> grid = board.getGrid();
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                Rectangle rect = (Rectangle) tilePane.getChildren().get(i * board.getCols() + j);
                rect.setFill(grid.get(i).get(j).getState() ? Color.STEELBLUE : Color.WHITE);
            }
        }
    }

    public TilePane getPane() {
        return tilePane;
    }

    private void attachListeners(Rectangle rect, Cell cell) {

        rect.setOnMouseClicked(event -> {
            rect.setFill(cell.getState() ? Color.WHITE : Color.STEELBLUE);
            cell.setNewState(!cell.getState());
            cell.updateState();
        });
    }
}