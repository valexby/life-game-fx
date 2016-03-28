package sample.core;

/**
 * Created by valex on 23.3.16.
 */
public class Board {
    private Cell[][] grid;
    private int height;
    private int width;

    public Board(Cell[][] grid) {
        this.grid = grid;
        height = width = grid.length;
    }

    public Board(int height, int width, double probState) {
        this.height = height;
        this.width = width;
        grid = new Cell[height][width];
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[i].length; j++)
                grid[i][j] = null;
        generate(probState);
    }

    public void generate(double probState) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == null) grid[i][j] = new Cell();
                if (Math.random() <= probState) {
                    grid[i][j].setNewState(true);
                    grid[i][j].updateState();
                }
            }
        }
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public int getSize() {
        return width;
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
                if (curRow < 0) curRow = height - 1;
                if (curCol < 0) curCol = width - 1;
                if (curRow == height) curRow = 0;
                if (curCol == width) curCol = 0;
                if (grid[curRow][curCol].getState())
                    result++;
            }
        return result;
    }

    private void commit() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].updateState();
            }
        }
    }


}