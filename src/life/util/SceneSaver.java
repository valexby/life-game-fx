package life.util;

import javafx.scene.Scene;

public class SceneSaver {
    private static SceneSaver ourInstance = new SceneSaver();
    private Scene mainScene, tableScene;

    private SceneSaver() {
    }

    public static SceneSaver getInstance() {
        return ourInstance;
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public Scene getTableScene() {
        return tableScene;
    }

    public void setTableScene(Scene tableScene) {
        this.tableScene = tableScene;
    }
}
