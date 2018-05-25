package ch.frimtec.raspicontrol.libraries.fts;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Processor {

    private final List<FixedTimeSlicedRunnable> runnables = new ArrayList<>();
    private final AtomicBoolean goDown = new AtomicBoolean(false);
    private final int cycleTimeMillis;
    private final EventHandler eventHandler;

    public interface EventHandler {
        void cycleTimeOverrun(long overrunMillis);

        void shutdownByInterrupt();
    }

    public Processor(int cycleTimeMillis, EventHandler eventHandler) {
        this.cycleTimeMillis = cycleTimeMillis;
        this.eventHandler = eventHandler;
    }

    public void run() {
        while (!goDown.get()) {
            long cycleStartTime = System.currentTimeMillis();
            runnables.forEach(FixedTimeSlicedRunnable::cycle);
            long cycleEndTime = System.currentTimeMillis();
            long sleepTime = cycleTimeMillis - millisBetween(cycleStartTime, cycleEndTime);
            try {
                if (sleepTime >= 0) {
                    Thread.sleep(sleepTime);
                } else {
                    eventHandler.cycleTimeOverrun(sleepTime * -1);
                }
            } catch (InterruptedException e) {
                eventHandler.shutdownByInterrupt();
                goDown.set(true);
                Thread.currentThread().interrupt();
            }
        }
    }

    static long millisBetween(long startTime, long endTime) {
        return endTime - startTime;
    }

    public int getCycleTime() {
        return cycleTimeMillis;
    }

    public void goDown() {
        goDown.set(true);
    }

    public void addRunnable(FixedTimeSlicedRunnable runnable) {
        runnables.add(runnable);
    }

    public long toCycles(Duration duration) {
        return duration.toMillis() / this.cycleTimeMillis;
    }
}
