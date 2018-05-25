package ch.frimtec.raspicontrol.libraries.pi4jadapter;

import ch.frimtec.raspicontrol.libraries.fts.AnalogInputPin;
import ch.frimtec.raspicontrol.libraries.pi4jadapter.Ads1115Device.Pin;
import ch.frimtec.raspicontrol.libraries.pi4jadapter.Ads1115Device.ProgrammableGainAmplifierValue;

import java.io.IOException;
import java.io.UncheckedIOException;

import static ch.frimtec.raspicontrol.libraries.pi4jadapter.Ads1115Device.ProgrammableGainAmplifierValue.PGA_6_144V;

public class Ads1115AnalogInputPin implements AnalogInputPin {

    private final Ads1115Device.AdcPin pin;

    public Ads1115AnalogInputPin(Ads1115Device device, Pin pin) {
        this(device, pin, PGA_6_144V);
    }

    public Ads1115AnalogInputPin(Ads1115Device device, Pin pin, ProgrammableGainAmplifierValue gainAmplifierValue) {
        this.pin = device.openAdcPin(pin, gainAmplifierValue);
    }

    @Override
    public int get() {
        try {
            return pin.getImmediateValue();
        } catch (IOException e) {
            throw new UncheckedIOException("Can not read ADS1115 pin " + this.pin, e);
        }
    }
}
