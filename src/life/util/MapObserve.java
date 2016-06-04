package life.util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import scala.Tuple3;

public class MapObserve {
    private final IntegerProperty ticks, clicks, bots, index;

    public MapObserve(Tuple3<Integer, Integer, Integer> data, int index) {
        ticks = new SimpleIntegerProperty(data._1());
        clicks = new SimpleIntegerProperty(data._2());
        bots = new SimpleIntegerProperty(data._3());
        this.index = new SimpleIntegerProperty(index);
    }

    public IntegerProperty ticksProperty() {
        return ticks;
    }

    public IntegerProperty clicksProperty() {
        return clicks;
    }

    public IntegerProperty botsProperty() {
        return bots;
    }

    public IntegerProperty indexProperty() {
        return index;
    }
}