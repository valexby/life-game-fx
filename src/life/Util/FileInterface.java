package life.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import life.core.Board;

public class FileInterface {
    public final static byte READ_MODE = 0, WRITE_MODE = 1;
    private final static String badDataMessage = "Bad data file", wrongFileMode = "Wrong file Mode";
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private byte fileMode = 2;

    public FileInterface(byte fileMode, String fileName) throws IOException {
        this.fileMode = fileMode;
        if (fileMode == READ_MODE) {
            FileInputStream fileStream = new FileInputStream(fileName);
            inputStream = new DataInputStream(fileStream);
        } else if (fileMode == WRITE_MODE) {
            FileOutputStream fileStream = new FileOutputStream(fileName);
            outputStream = new DataOutputStream(fileStream);
        }
    }

    public void saveBoard(Board board) throws Exception {
        if (fileMode != WRITE_MODE) {
            throw new Exception(wrongFileMode);
        }
        int rows = board.getRows(), cols = board.getCols();
        ArrayList<Boolean> buffer;
        buffer = new ArrayList<>(rows * cols);
        board.getGrid().stream().forEach(i -> i.forEach(j -> buffer.add(j.getState())));
        saveGrid(buffer, rows, cols);
    }

    public void saveGrid(ArrayList<Boolean> gridMap, int rows, int cols) throws Exception {
        if (fileMode != WRITE_MODE) {
            throw new Exception(wrongFileMode);
        }
        outputStream.writeInt(rows);
        outputStream.writeInt(cols);
        for (Boolean i : gridMap)
            outputStream.writeBoolean(i);
    }

    public Board loadBoard() throws Exception {
        if (fileMode != READ_MODE) {
            throw new Exception(wrongFileMode);
        }
        int cols, rows;
        Board result = new Board();
        rows = inputStream.readInt();
        cols = inputStream.readInt();
        if (rows < 0 || cols < 0) {
            throw new Exception(badDataMessage);
        }
        result.setRows(rows);
        result.setCols(cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.getGrid().get(i).get(j).setNewState(inputStream.readBoolean());
            }
        }
        result.commit();
        return result;
    }

    public void close() throws IOException {
        if (fileMode == READ_MODE) {
            inputStream.close();
        } else if (fileMode == WRITE_MODE) {
            outputStream.close();
        }
        fileMode = 2;
    }
}
