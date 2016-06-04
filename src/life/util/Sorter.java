package life.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.scene.control.Alert;

public class Sorter {


    private final static String savePath = "saves/", tempPath = "temp/";
    private String[] baseList;
    private SaveCarrier[] resultList;

    public Sorter(String[] baseList) throws Exception {
        this.baseList = baseList;
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
        long defTime = 0, javaTime = 0, scalaTime = 0;
        ScalaSort scalaSort = new ScalaSort();
        List<SaveCarrier> defList = Arrays.asList(resultList);

        defTime = System.nanoTime();
        Collections.sort(defList);
        defTime = System.nanoTime() - defTime;

        javaTime = System.nanoTime();
        JavaQuickSort.quickSort(resultList);
        javaTime = System.nanoTime() - javaTime;

        scalaTime = System.nanoTime();
        scalaSort.sort(resultList);
        scalaTime = System.nanoTime() - scalaTime;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = resultList[i].name;
        }
        Alert infoMessage = new Alert(Alert.AlertType.INFORMATION);
        infoMessage.setTitle("Done");
        infoMessage.setHeaderText("Sort is done");
        infoMessage.setContentText("Def time is " + defTime / 1000 + "ms\n" +
                "Java time is " + javaTime / 1000 + "ms\n" +
                "Scala time is" + scalaTime / 1000 + "ms");
        infoMessage.show();
        return buffer;
    }
}
