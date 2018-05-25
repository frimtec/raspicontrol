package ch.frimtec.raspicontrol.libraries.mockadapter;

import ch.frimtec.raspicontrol.libraries.fts.DigitalOutputPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import java.util.logging.Logger;

public class DigitalOutputPinMock implements DigitalOutputPin {
    private static final Logger logger = Logger.getLogger(DigitalOutputPinMock.class.getName());

    private final Pin gpioPin;
    private final PinState defaultState;

    public DigitalOutputPinMock(Pin gpioPin, PinState defaultState) {
        this.gpioPin = gpioPin;
        this.defaultState = defaultState;
    }

    @Override
    public void set(boolean state) {
        logger.info(String.format("Ouptut pin %s set to state: %s", this.gpioPin, state));
    }
}
