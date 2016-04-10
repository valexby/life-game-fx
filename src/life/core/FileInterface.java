package life.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;

/**
 * Created by valex on 19.4.16.
 */
public class FileInterface {

    private final static String badDataMessage = "Bad data file";

    public void saveGrid(String fileName, ArrayList< ArrayList<Cell> > gridToSave) throws IOException {
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            DataOutputStream dataStream = new DataOutputStream(outputStream);
            dataStream.writeInt(gridToSave.size());
            dataStream.writeInt(gridToSave.get(0).size());
            for (int i=0;i<gridToSave.size();i++)
                for (int j=0;j<gridToSave.get(0).size();j++)
                    dataStream.writeBoolean(gridToSave.get(i).get(j).getState());
            dataStream.close();
            outputStream.close();
        }
        catch (IOException ex) {
            throw new IOException(ex);
        }
    }

    public Board loadBoard(String fileName) throws Exception {
        int buffer;
        Board result = new Board();
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            DataInputStream dataStream = new DataInputStream(inputStream);
            buffer = dataStream.readInt();
            if (buffer<0)
                throw new Exception(badDataMessage);
            result.setRows(buffer);
            buffer = dataStream.readInt();
            if (buffer<0)
                throw new Exception(badDataMessage);
            result.setCols(buffer);

//            result.getGrid().stream().forEach(i -> i.stream().map(j -> {
//                try { return new Cell(dataStream.readBoolean());  }
//                catch (IOException ex) { throw new UncheckedIOException(ex);  }
//            }));
            for (int i=0;i<result.getRows();i++)
                for (int j=0;j<result.getCols();j++)
                    result.getGrid().get(i).get(j).setNewState(dataStream.readBoolean());
            result.commit();
            dataStream.close();
            inputStream.close();
        }
        catch (Exception ex) {
            throw new Exception(ex);
        }
        return result;
    }
}
