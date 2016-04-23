package life.gui;

import java.util.ArrayList;

import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import life.core.Board;
import life.core.Cell;

/**
 * Interface between abstract board model and physical graphic interface
 * */
public class DisplayDriver {
    private TilePane tilePane;

    /**
     * @param cellSizePx size of cell in pixels
     * @param board abstract model to draw
     * */
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

    /**
     * Draw board to screen
     * @param board board to draw
     * */
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

    /**
     * Attach listeners to play board elements
     * @param rect element to attach
     * @param cell cell represents by rectangle element
     * */
    private void attachListeners(Rectangle rect, Cell cell) {

        rect.setOnMouseClicked(event -> {
            rect.setFill(cell.getState() ? Color.WHITE : Color.STEELBLUE);
            cell.setNewState(!cell.getState());
            cell.updateState();
        });
    }
}