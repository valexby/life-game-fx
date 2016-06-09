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
        this.index = new SimpleIntegerProperty(index + 1);
    }

    public int getTicks() {
        return ticks.get();
    }

    public int getClicks() {
        return clicks.get();
    }

    public int getBots() {
        return bots.get();
    }

    public int getIndex() {
        return index.get();
    }
}