package life.util;

import javafx.scene.Scene;

public class SceneSaver {
    private static SceneSaver ourInstance = new SceneSaver();

    public static SceneSaver getInstance() {
        return ourInstance;
    }

    private Scene mainScene, tableScene;

    public Scene getMainScene() {
        return mainScene;
    }

    public Scene getTableScene() {
        return tableScene;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setTableScene(Scene tableScene) {
        this.tableScene = tableScene;
    }

    private SceneSaver() {
    }
}
