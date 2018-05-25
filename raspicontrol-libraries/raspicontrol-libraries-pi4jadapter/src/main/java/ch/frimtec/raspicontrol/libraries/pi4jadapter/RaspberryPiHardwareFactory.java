package ch.frimtec.raspicontrol.libraries.pi4jadapter;

import ch.frimtec.raspicontrol.libraries.fts.AnalogInputPin;
import ch.frimtec.raspicontrol.libraries.fts.DigitalOutputPin;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.system.SystemInfoFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Logger;

public class RaspberryPiHardwareFactory implements HardwareFactory {

    private static final Logger logger = Logger.getLogger(RaspberryPiHardwareFactory.class.getName());

    private static final String RASPERI_PI_HW_IDENTIFICATION = "BCM2835";

    private final HardwareFactory fallbackFactory;

    public RaspberryPiHardwareFactory(HardwareFactory fallbackFactory) {
        this.fallbackFactory = fallbackFactory;
    }

    @Override
    public I2CBus createI2cBus(int busNumber) {
        if (isRaspiHardware()) {
            try {
                return I2CFactory.getInstance(I2CBus.BUS_1);
            } catch (I2CFactory.UnsupportedBusNumberException e) {
                throw new IllegalStateException("Can not create I2C bus", e);
            } catch (IOException e) {
                throw new UncheckedIOException("Can not create I2C bus", e);
            }
        } else {
            logger.warning("Not running on raspberry pi hardware, using mock I2C bus");
            return this.fallbackFactory.createI2cBus(busNumber);
        }
    }

    @Override
    public DigitalOutputPin digitalOutputPin(Pin gpioPin, PinState defaultState) {
        if (isRaspiHardware()) {
            return new GpioDigitalOutputPin(GpioFactory.getInstance().provisionDigitalOutputPin(gpioPin, defaultState));
        } else {
            logger.warning("Not running on raspberry pi hardware, using mock for IO pin " + gpioPin);
            return this.fallbackFactory.digitalOutputPin(gpioPin, defaultState);
        }
    }

    @Override
    public AnalogInputPin createAnalogInputPin(Ads1115Device ads1115, Ads1115Device.Pin pin, Ads1115Device.ProgrammableGainAmplifierValue gainAmplifierValue) {
        if (isRaspiHardware()) {
            return new Ads1115AnalogInputPin(ads1115, pin, gainAmplifierValue);
        } else {
            logger.warning("Not running on raspberry pi hardware, using mock for analog input pin " + pin);
            return this.fallbackFactory.createAnalogInputPin(ads1115, pin, gainAmplifierValue);
        }
    }

    private static boolean isRaspiHardware() {
        try {
            return RASPERI_PI_HW_IDENTIFICATION.equals(SystemInfoFactory.getProvider().getHardware());
        } catch (Exception e) {
            return false;
        }
    }

}
