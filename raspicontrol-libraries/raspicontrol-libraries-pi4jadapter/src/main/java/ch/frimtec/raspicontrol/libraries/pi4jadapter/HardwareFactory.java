package ch.frimtec.raspicontrol.libraries.pi4jadapter;

import ch.frimtec.raspicontrol.libraries.fts.AnalogInputPin;
import ch.frimtec.raspicontrol.libraries.fts.DigitalOutputPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.i2c.I2CBus;

public interface HardwareFactory {

    I2CBus createI2cBus(int busNumber);

    DigitalOutputPin digitalOutputPin(Pin gpioPin, PinState defaultState);

    AnalogInputPin createAnalogInputPin(Ads1115Device ads1115, Ads1115Device.Pin pin, Ads1115Device.ProgrammableGainAmplifierValue gainAmplifierValue);
}
