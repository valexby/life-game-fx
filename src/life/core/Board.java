package life.core;

import java.util.ArrayList;

/**
 * Model of abstract game board.
 * */
public class Board {
    private ArrayList<ArrayList<Cell>> grid;
    private int rows, cols;

    public Board() {
        cols = rows = 0;
        grid = new ArrayList<>();
    }

    /**
     * Generate cells on game board
     * @param  probState cell's probability to be alive
     * */
    public void generate(double probState) {
        grid.parallelStream()
                .forEach(i -> i.parallelStream()
                        .forEach(j -> {
                            if (j == null) j = new Cell();
                            if (Math.random() <= probState) {
                                j.setNewState(true);
                                j.updateState();
                            }
                        }));
    }

    public ArrayList<ArrayList<Cell>> getGrid() {
        return grid;
    }
    /**
     * Kill all cells on board
     * */
    public void resetGrid() {
        grid.parallelStream().forEach(i -> i.parallelStream().forEach(j -> {
            j.setNewState(false);
            j.updateState();
        }));
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    /**
     * Horizontal board resize
     * @param newCols new cols count
     * */
    public void setCols(int newCols) {
        if (cols == newCols) return;
        if (newCols > cols)
            grid.parallelStream().forEach(i -> {
                for (int j = cols; j < newCols; j++)
                    i.add(new Cell());
            });
        else
            grid.parallelStream().forEach(i -> {
                for (int j = cols - 1; j >= newCols; j--)
                    i.remove(j);
            });
        cols = newCols;
    }
    /**
     * Vertical board resize
     * @param newRows new rows count
     * */
    public void setRows(int newRows) {
        if (rows == newRows) return;
        if (newRows > rows) {
            for (int i = rows; i < newRows; i++) {
                grid.add(new ArrayList<>());
                for (int j = 0; j < cols; j++)
                    grid.get(i).add(new Cell());
            }
        } else
            for (int i = rows - 1; i >= newRows; i--)
                grid.remove(i);
        rows = newRows;
    }

    /**
     * Inject other board in current board
     * @param invader board to inject
     * @param colPos col index of invader position in current board
     * @param rowPos row index of invader position in current board
     * */
    public void injectBoard(Board invader, int rowPos, int colPos) {
        if (invader == null) return;
        if (invader.getRows() > rows || invader.getCols() > cols)
            return;
        for (int i = 0; i < invader.getRows(); i++)
            for (int j = 0; j < invader.getCols(); j++)
                grid.get((rowPos + i < rows) ? rowPos + i : rowPos + i - rows).
                        get((colPos + j < cols) ? colPos + j : colPos + j - cols).
                        setNewState(invader.getGrid().get(i).get(j).getState());
        commit();
    }

    /**
     * Compute next cell's condition
     * */
    public void update() {
        int i, j, around;
        for (i = 0; i < rows; i++)
            for (j = 0; j < cols; j++) {
                around = liveAround(i, j);
                if (!(grid.get(i).get(j).getState()) && around == 3)
                    grid.get(i).get(j).setNewState(true);
                if (grid.get(i).get(j).getState() && around != 2 && around != 3)
                    grid.get(i).get(j).setNewState(false);
            }
        commit();
    }

    /**
     * Seek for live cells around current cell
     * @param row current cell's row index
     * @param col current cell's col index
     * @return count of live cells around
     * */
    private int liveAround(int row, int col) { //считает количество живых клеток по соседству
        int currentRow, currentCol, i, j, result = grid.get(row).get(col).getState() ? -1 : 0; //не включаем текущую
        for (i = -1; i < 2; i++) //считает поличество живых клеток в квадрате 3х3 с центров в [row][col]
            for (j = -1; j < 2; j++) {
                currentRow = row + i;
                currentCol = col + j;
                if (currentRow < 0) currentRow = rows - 1;
                if (currentCol < 0) currentCol = cols - 1;
                if (currentRow == rows) currentRow = 0;
                if (currentCol == cols) currentCol = 0;
                if (grid.get(currentRow).get(currentCol).getState())
                    result++;
            }
        return result;
    }

    /**
     * Set's to all cells their new state
     * */
    public void commit() {
        grid.parallelStream().forEach(i -> i.parallelStream()
                .forEach(j -> j.updateState()));
    }
}