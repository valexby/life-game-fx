package sample;

/**
 * Created by valex on 23.3.16.
 */

import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.core.Board;
import sample.core.Cell;

public class DisplayDriver {
    private int sz;
    private TilePane tilePane;

    public DisplayDriver(int cellSizePx, Board board) {
        Cell[][] grid = board.getGrid();
        sz = board.getCols();
        tilePane = new TilePane(5, 5);
        tilePane.setPrefRows(board.getRows());
        tilePane.setPrefColumns(board.getCols());

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                Color color = grid[i][j].getState() ? Color.STEELBLUE : Color.WHITE;
                Rectangle rect = new Rectangle(cellSizePx, cellSizePx, color);
                tilePane.getChildren().add(rect);

                attachListeners(rect, grid[i][j]);
            }
        }
    }

    public void displayBoard(Board board) {
        Cell[][] grid = board.getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Rectangle rect = (Rectangle) tilePane.getChildren().get(i * sz + j);
                rect.setFill(grid[i][j].getState() ? Color.STEELBLUE : Color.WHITE);
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