package life.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import life.core.Board;

public class Bot {
    private final static String directoryReadError = "Unable to read data directory";
    private ArrayList<String> dataList = null;
    private String dataPath = "data/";
    private int invaderIndex = -1;


    public Bot() throws Exception {
        File dataDirectory = new File(dataPath);
        if (!dataDirectory.canRead()) {
            throw new Exception(directoryReadError);
        }
        dataList = new ArrayList<>(Arrays.asList(dataDirectory.list()));
    }

    public int lastSpawnedIndex() {
        return invaderIndex;
    }

    public Board spawn() throws Exception {
        invaderIndex = (int) Math.round(Math.random() * (dataList.size() - 1));
        FileInterface descriptor = new FileInterface(FileInterface.READ_MODE, dataPath + dataList.get(invaderIndex));
        Board invader;
        try {
            invader = descriptor.loadBoard();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage() + '\n' + dataList.get(invaderIndex));
        }
        return invader;
    }
}
