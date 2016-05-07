package life.Util;

import java.util.concurrent.LinkedBlockingDeque;

import life.Threads.ReplaySaverThread;

public class Chronicle {
    private LinkedBlockingDeque<LifeEvent> requests;
    private ReplaySaverThread replaySaverThread;

    public Chronicle(ReplaySaverThread replaySaverThread) {
        requests = new LinkedBlockingDeque<LifeEvent>();
        this.replaySaverThread = replaySaverThread;
    }

    public void put(LifeEvent event) throws InterruptedException {
        if (event.type == LifeEvent.TICK && requests.size() != 0 && requests.getLast().type == LifeEvent.TICK) {
            requests.getLast().number++;
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
