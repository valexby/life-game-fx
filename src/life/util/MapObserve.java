package life.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scala.Tuple3;

public class MapObserve {
    private final StringProperty ticks, clicks, bots, index;

    public MapObserve(Tuple3<Integer, Integer, Integer> data, int index) {
        ticks = new SimpleStringProperty(data._1().toString());
        clicks = new SimpleStringProperty(data._2().toString());
        bots = new SimpleStringProperty(data._3().toString());
        this.index = new SimpleStringProperty(Integer.toString(index));
    }

    public StringProperty ticksProperty() {
        return ticks;
    }


    public StringProperty clicksProperty() {
        return clicks;
    }

    public StringProperty botsProperty() {
        return bots;
    }

    public StringProperty indexProperty() {
        return index;
    }
}