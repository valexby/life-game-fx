package sample.core;

/**
 * Created by valex on 23.3.16.
 */
public class Board {
    private Cell[][] grid;
    private int rows, cols;

    public Board() {
        cols = rows = 0;
        grid = new Cell[0][0];
    }
    
    public void generate(double probState) {
        for (Cell[] i : grid)
            for (Cell j : i) {
                if (j == null) j = new Cell();
                if (Math.random() <= probState) {
                    j.setNewState(true);
                    j.updateState();
                }
            }
    }

    public void changeCols(int newCols) {
        for (int i = 0; i < grid.length; i++) {
            grid[i] = java.util.Arrays.copyOf(grid[i], newCols);
            if (newCols > cols)
                for (int j = cols; j < newCols; j++)
                    grid[i][j] = new Cell();
        }
        cols = newCols;
    }

    public void changeRows(int newRows) {
        grid = java.util.Arrays.copyOf(grid, newRows);
        if (newRows > rows) {
            for (int i = rows; i < newRows; i++) {
                grid[i] = new Cell[cols];
                for (int j = 0; j < cols; j++)
                    grid[i][j] = new Cell();
            }
        }
        rows = newRows;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public void resetGrid() {
        for (Cell[] i : grid)
            for (Cell j : i) {
                j.setNewState(false);
                j.updateState();
            }
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public void update() {
        int i, j, around;
        for (i = 0; i < grid.length; i++)
            for (j = 0; j < grid[i].length; j++) {
                around = liveAround(i, j);
                if (!(grid[i][j].getState()) && around == 3)
                    grid[i][j].setNewState(true);
                if (grid[i][j].getState() && around != 2 && around != 3)
                    grid[i][j].setNewState(false);
            }
        commit();
    }

    private int liveAround(int row, int col) {
        int curRow, curCol, i, j, result = grid[row][col].getState() ? -1 : 0;
        for (i = -1; i < 2; i++)
            for (j = -1; j < 2; j++) {
                curRow = row + i;
                curCol = col + j;
                if (curRow < 0) curRow = rows - 1;
                if (curCol < 0) curCol = cols - 1;
                if (curRow == rows) curRow = 0;
                if (curCol == cols) curCol = 0;
                if (grid[curRow][curCol].getState())
                    result++;
            }
        return result;
    }

    private void commit() {
        for (Cell[] i : grid)
            for (Cell j : i)
                j.updateState();
    }


}