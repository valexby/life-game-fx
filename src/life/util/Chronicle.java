package life.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.zip.Inflater;

import life.core.Board;
import life.threads.ReplaySaverThread;
import scala.Tuple3;

public class Chronicle {
    private LinkedBlockingDeque<LifeEvent> requests;
    private ReplaySaverThread replaySaverThread;
    private Board firstFrame;

    public Chronicle(ReplaySaverThread replaySaverThread) {
        requests = new LinkedBlockingDeque<>();
        this.replaySaverThread = replaySaverThread;
    }

    public Chronicle(String replayPath) throws Exception {
        FileInterface descriptor;
        requests = new LinkedBlockingDeque<>();
        descriptor = new FileInterface(FileInterface.READ_MODE, replayPath);
        firstFrame = descriptor.loadBoard();
        LifeEvent buffer = descriptor.loadEvent();
        while (buffer != null) {
            requests.put(buffer);
            buffer = descriptor.loadEvent();
        }
        descriptor.close();
    }

    public Tuple3<Integer, Integer, Integer> getMap() {
        int ticks = 0, clicks = 0, bots = 0;
        for (LifeEvent i : requests) {
            switch (i.getType()) {
                case LifeEvent.TICK:
                    ticks+=i.getNumber();
                    break;
                case LifeEvent.CLICK:
                    clicks++;
                    break;
                case LifeEvent.BOT:
                    bots++;
                    break;
            }
        }
        return new Tuple3<>(ticks, clicks, bots);
    }

    public Board getBoard() {
        return firstFrame;
    }

    public void put(LifeEvent event) throws InterruptedException {
        if (event.getType() == LifeEvent.TICK && requests.size() != 0 && requests.getLast().getType() == LifeEvent.TICK) {
            requests.getLast().untick();
        } else {
            requests.put(event);
            if (!replaySaverThread.isAlive()) {
                replaySaverThread.notify();
            }
        }
    }

    synchronized public LifeEvent poll() {
        return requests.poll();
    }

    public LifeEvent get() {
        return requests.getFirst();
    }

    synchronized public boolean isEmpty() {
        return requests.size() == 0;
    }
}
