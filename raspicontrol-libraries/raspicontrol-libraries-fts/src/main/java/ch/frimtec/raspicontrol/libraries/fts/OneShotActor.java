package ch.frimtec.raspicontrol.libraries.fts;

import java.time.Duration;

public class OneShotActor {

    private final Processor processor;
    private final DigitalOutputPin pin;
    private final ParameterAccessor<Duration> oneShootDelay;

    private long oneShootCounter = 0;
    private boolean active = false;

    public OneShotActor(Processor processor, DigitalOutputPin pin, ParameterAccessor<Duration> oneShootDelay) {
        this.processor = processor;
        this.pin = pin;
        this.oneShootDelay = oneShootDelay;
        setPinState(false);

        processor.addRunnable(() -> {
            if (oneShootCounter > 0) {
                oneShootCounter--;
                if (oneShootCounter == 0) {
                    setActivation(false);
                }
            }
        });
    }

    public void setActivation(boolean activation) {
        long delay = this.processor.toCycles(oneShootDelay.getValue());
        if (activation && delay > 0) {
            oneShootCounter = delay;
        } else {
            oneShootCounter = 0;
        }
        active = activation;
        setPinState(activation);
    }

    public boolean isActive() {
        return active;
    }

    public void setPinState(boolean state) {
        pin.set(state);
    }
}
