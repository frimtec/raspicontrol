package ch.frimtec.raspicontrol.libraries.mockadapter;

import ch.frimtec.raspicontrol.libraries.fts.AnalogInputPin;
import ch.frimtec.raspicontrol.libraries.fts.DigitalOutputPin;
import ch.frimtec.raspicontrol.libraries.pi4jadapter.Ads1115Device;
import ch.frimtec.raspicontrol.libraries.pi4jadapter.HardwareFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.i2c.I2CBus;

import java.util.logging.Logger;

public class MockHardwareFactory implements HardwareFactory {

    private static final Logger logger = Logger.getLogger(MockHardwareFactory.class.getName());

    @Override
    public I2CBus createI2cBus(int busNumber) {
        return new I2cBusMock(busNumber);
    }

    @Override
    public DigitalOutputPin digitalOutputPin(Pin gpioPin, PinState defaultState) {
        return new DigitalOutputPinMock(gpioPin, defaultState);
    }

    @Override
    public AnalogInputPin createAnalogInputPin(Ads1115Device ads1115, Ads1115Device.Pin pin, Ads1115Device.ProgrammableGainAmplifierValue gainAmplifierValue) {
        return new AnalogInputPinMock(pin.name());
    }
}
