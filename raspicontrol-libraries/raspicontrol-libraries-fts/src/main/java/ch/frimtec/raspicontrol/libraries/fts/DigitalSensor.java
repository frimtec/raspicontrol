package ch.frimtec.raspicontrol.libraries.fts;

public class DigitalSensor implements Sensor {

    private final DigitalInputPin pin;
    private final int filterCycles;
    private final boolean activeLevel;

    private boolean activation = false;
    private int filterCounter = 0;

    public DigitalSensor(Processor processor, DigitalInputPin pin, boolean activeLevel, int filterCycles) {
        this.pin = pin;
        this.activeLevel = activeLevel;
        this.filterCycles = filterCycles;
        processor.addRunnable(() -> {
            boolean pinState = pin.get();
            if (!activation && pinState) {
                filterCounter++;
                if (filterCounter > filterCycles) {
                    activation = true;
                    filterCounter = 0;
                }
            } else if (activation && !pinState) {
                filterCounter++;
                if (filterCounter > filterCycles) {
                    activation = false;
                    filterCounter = 0;
                }
            } else {
                filterCounter = 0;
            }
        });
    }

    public boolean getActivation() {
        return activation;
    }
}
