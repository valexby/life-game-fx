package life.util;

import java.util.Arrays;
import java.util.Collections;

import javafx.scene.control.Alert;

public class Sorter {


    private final static String savePath = "saves/", tempPath = "temp/";
    private String[] baseList;
    private SaveCarrier[] resultList;
    private SortMod sortMod;
    public Sorter(String[] baseList, SortMod sortMod) throws Exception {
        this.baseList = baseList;
        this.sortMod = sortMod;
        Collections.sort(Arrays.asList(this.baseList));
        mutate();
    }

    private void mutate() throws Exception {
        resultList = new SaveCarrier[baseList.length];
        FileInterface fileInterface;
        for (int i = 0; i < baseList.length; i++) {
            fileInterface = new FileInterface(FileInterface.READ_MODE, savePath + baseList[i]);
            resultList[i] = new SaveCarrier();
            resultList[i].coefficient = fileInterface.loadBoard().beautyCount();
            resultList[i].name = baseList[i];
            fileInterface.close();
        }
    }

    public String[] filesSort() {
        String[] buffer = new String[resultList.length];
        long fullTime = System.nanoTime();
        switch (sortMod) {
            case NO_MOD:
                Collections.sort(Arrays.asList(resultList));
                break;
            case JAVA_MOD:
                JavaQuickSort.quickSort(resultList);
                break;
            case SCALA_MOD:
                new ScalaSort().sort(resultList);
                break;
        }
        fullTime = System.nanoTime() - fullTime;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = resultList[i].name;
        }
        Alert infoMessage = new Alert(Alert.AlertType.INFORMATION);
        infoMessage.setTitle("Done");
        infoMessage.setHeaderText("Sort is done");
        infoMessage.setContentText("Sort time is " + fullTime / 1000 + "ms");
        infoMessage.show();
        return buffer;
    }

    public enum SortMod {NO_MOD, JAVA_MOD, SCALA_MOD}
}
