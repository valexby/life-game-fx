package life.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Spawner {
    private static ArrayList<String> dataList = null;
    private final static String dataPath = "data/", directoryReadError = "Unable to read data directory",
            uninitializedError = "Data directory is uninitialized";

    public static void init() throws Exception {
        if (dataList != null) return;
        File dataDirectory = new File(dataPath);
        if (!dataDirectory.canRead())
            throw new Exception(directoryReadError);
        dataList = new ArrayList<>(Arrays.asList(dataDirectory.list()));
    }

    public static Board spawn() throws Exception {
        if (dataList == null) throw new Exception(uninitializedError);
        int invaderIndex = (int) Math.round(Math.random() * (dataList.size() - 1));
        Board invader;
        try {
            invader = FileInterface.loadBoard(dataPath + dataList.get(invaderIndex));
        } catch (Exception ex) {
            throw new Exception(ex.getMessage() + '\n' + dataList.get(invaderIndex));
        }
        return invader;
    }
}
