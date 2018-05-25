package ch.frimtec.raspicontrol.libraries.pi4jadapter;

import ch.frimtec.raspicontrol.libraries.fts.DigitalOutputPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class GpioDigitalOutputPin implements DigitalOutputPin {
    private final GpioPinDigitalOutput digitalOutput;

    public GpioDigitalOutputPin(GpioPinDigitalOutput digitalOutput) {
        this.digitalOutput = digitalOutput;
    }

    @Override
    public void set(boolean state) {
        this.digitalOutput.setState(state);
    }
}
