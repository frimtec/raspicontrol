package ch.frimtec.raspicontrol.libraries.fts;

public class Timer implements Sensor {

    private long counter = 0;

    public Timer(Processor processor) {
        processor.addRunnable(() -> {
            if (counter > 0) {
                counter--;
            }
        });
    }

    public void start(long cycles) {
        counter = cycles;
    }

    public boolean getActivation() {
        return counter == 0;
    }
}
