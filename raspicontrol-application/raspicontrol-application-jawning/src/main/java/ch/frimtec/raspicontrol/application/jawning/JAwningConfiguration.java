package ch.frimtec.raspicontrol.application.jawning;

import ch.frimtec.raspicontrol.libraries.fts.*;
import ch.frimtec.raspicontrol.libraries.fts.Processor.EventHandler;
import ch.frimtec.raspicontrol.libraries.mockadapter.MockHardwareFactory;
import ch.frimtec.raspicontrol.libraries.pi4jadapter.Ads1115Device;
import ch.frimtec.raspicontrol.libraries.pi4jadapter.HardwareFactory;
import ch.frimtec.raspicontrol.libraries.pi4jadapter.RaspberryPiHardwareFactory;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;

import static ch.frimtec.raspicontrol.libraries.pi4jadapter.Ads1115Device.ProgrammableGainAmplifierValue.PGA_4_096V;

@Configuration
public class JAwningConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JAwningApplication.class);

    @Bean
    public Processor processor() {
        return new Processor(100, new EventHandler() {
            @Override
            public void cycleTimeOverrun(long overrunMillis) {
                logger.warn("Time slice overrun of {} ms", overrunMillis);
            }

            @Override
            public void shutdownByInterrupt() {
                logger.info("Shutdown interrupt received.");
            }
        });
    }

    @Bean
    public AwningController awningController() {
        return new AwningController(processor(), manualIn(), manualOut(), manualTimeout(), sun(), wind(), awning());
    }

    @Bean
    public SimpleTwoDirectionEngine awning() {
        return new SimpleTwoDirectionEngine(awningPowerSwitch(), awningDirectionSwitchPin(), false);
    }

    @Bean
    public OneShotActor awningPowerSwitch() {
        return new OneShotActor(processor(), awningPowerSwitchPin(), () -> Duration.ofSeconds(50));
    }

    @Bean
    public Ads1115Device ads1115Device() {
        try {
            return new Ads1115Device(i2cBus().getDevice(Ads1115Device.ADS1115_ADDRESS_0x48));
        } catch (IOException e) {
            throw new UncheckedIOException("Can not create I2C device", e);
        }
    }


    @Bean
    public HardwareFactory hardwareFactory() {
        return new RaspberryPiHardwareFactory(new MockHardwareFactory());
    }

    @Bean
    public I2CBus i2cBus() {
        return hardwareFactory().createI2cBus(I2CBus.BUS_1);
    }

    @Bean
    public Ads1115Device ads1115() {
        try {
            return new Ads1115Device(i2cBus().getDevice(Ads1115Device.ADS1115_ADDRESS_0x48));
        } catch (IOException e) {
            throw new UncheckedIOException("Can not create I2C bus", e);
        }
    }

    @Bean
    public DigitalOutputPin awningPowerSwitchPin() {
        return hardwareFactory().digitalOutputPin(RaspiPin.GPIO_28, PinState.HIGH);
    }

    private DigitalOutputPin awningDirectionSwitchPin() {
        return hardwareFactory().digitalOutputPin(RaspiPin.GPIO_29, PinState.HIGH);
    }

    @Bean
    public AnalogSensor sun() {
        return new AnalogSensor(processor(), sunInputPin(), 16, true, () -> 10000, () -> 5000, () -> Duration.ofMillis(100), () -> Duration.ofMillis(100));
    }

    @Bean
    public AnalogSensor wind() {
        return new AnalogSensor(processor(), windInputPin(), 16, false, () -> 10000, () -> 5000, () -> Duration.ofMinutes(10), () -> Duration.ofMinutes(10));
    }

    @Bean
    public AnalogInputPin sunInputPin() {
        return hardwareFactory().createAnalogInputPin(ads1115(), Ads1115Device.Pin.PIN1, PGA_4_096V);
    }

    @Bean
    public AnalogInputPin windInputPin() {
        return hardwareFactory().createAnalogInputPin(ads1115(), Ads1115Device.Pin.PIN2, PGA_4_096V);
    }

    @Bean
    public DigitalSensor manualIn() {
        return new DigitalSensor(processor(), () -> false, true, 3);
    }

    @Bean
    public DigitalSensor manualOut() {
        return new DigitalSensor(processor(), () -> false, true, 3);
    }

    @Bean
    public Parameter<Duration> manualTimeout() {
        return new Parameter<>("jawning.manual.timeout", "Manual timeout [min]", Duration.ofMinutes(60), Duration.ofMinutes(1), Duration.ofMinutes(300));
    }

}
