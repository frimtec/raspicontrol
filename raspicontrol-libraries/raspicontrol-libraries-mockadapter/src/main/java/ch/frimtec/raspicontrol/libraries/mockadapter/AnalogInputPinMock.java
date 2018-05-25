package ch.frimtec.raspicontrol.libraries.mockadapter;

import ch.frimtec.raspicontrol.libraries.fts.AnalogInputPin;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class AnalogInputPinMock implements AnalogInputPin {
    private static final Logger logger = Logger.getLogger(AnalogInputPinMock.class.getName());

    private final String pin;

    public AnalogInputPinMock(String pin) {
        this.pin = pin;
    }

    @Override
    public int get() {
        int nextInt = ThreadLocalRandom.current().nextInt(0,(int)Math.pow(2, 15));
        logger.info(String.format("Analog input pin %s value: %d", this.pin, nextInt));
        return nextInt;
    }
}
