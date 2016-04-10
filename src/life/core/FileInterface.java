package life.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileInterface {

    private final static String badDataMessage = "Bad data file";

    public static void saveGrid(String fileName, ArrayList<Boolean> gridMap, int rows, int cols) throws IOException {
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            DataOutputStream dataStream = new DataOutputStream(outputStream);
            dataStream.writeInt(rows);
            dataStream.writeInt(cols);
            for (Boolean i : gridMap)
                dataStream.writeBoolean(i);
            dataStream.close();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }

    public static Board loadBoard(String fileName) throws Exception {
        int buffer;
        Board result = new Board();
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            DataInputStream dataStream = new DataInputStream(inputStream);
            buffer = dataStream.readInt();
            if (buffer < 0)
                throw new Exception(badDataMessage);
            result.setRows(buffer);
            buffer = dataStream.readInt();
            if (buffer < 0)
                throw new Exception(badDataMessage);
            result.setCols(buffer);
            for (int i = 0; i < result.getRows(); i++)
                for (int j = 0; j < result.getCols(); j++)
                    result.getGrid().get(i).get(j).setNewState(dataStream.readBoolean());
            result.commit();
            dataStream.close();
            inputStream.close();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        return result;
    }
}
