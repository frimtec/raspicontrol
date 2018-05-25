package ch.frimtec.raspicontrol.libraries.fts;

import java.time.Duration;

public class AnalogSensor implements Sensor {
    private final Processor processor;
    private final AnalogInputPin pin;
    private final int bitSize;
    private final boolean inverse;
    private final ParameterAccessor<Integer> activationLevel;
    private final ParameterAccessor<Integer> deactivationLevel;
    private final ParameterAccessor<Duration> activationDelay;
    private final ParameterAccessor<Duration> deactivationDelay;

    private boolean activation;
    private int value;
    private long delayCounter;
    private int minValue;
    private int maxValue;

    public AnalogSensor(Processor processor, AnalogInputPin pin, int bitSize, boolean inverse, ParameterAccessor<Integer> activationLevel,
                        ParameterAccessor<Integer> deactivationLevel, ParameterAccessor<Duration> activationDelay, ParameterAccessor<Duration> deactivationDelay) {
        this.processor = processor;
        this.pin = pin;
        this.bitSize = bitSize;
        this.inverse = inverse;
        this.activationLevel = activationLevel;
        this.deactivationLevel = deactivationLevel;
        this.activationDelay = activationDelay;
        this.deactivationDelay = deactivationDelay;

        activation = false;
        delayCounter = this.processor.toCycles(this.activationDelay.getValue());
        int dacMaxValue = (int) Math.pow(2, (bitSize - 1)) - 1;
        this.minValue = dacMaxValue;
        maxValue = 0;
        processor.addRunnable(() -> {
            int adcValue = pin.get();
            value = inverse ? (dacMaxValue - adcValue) : adcValue;
            if (value > maxValue) {
                maxValue = value;
            }
            if (value < this.minValue) {
                this.minValue = value;
            }
            if (activation) {
                if (value < deactivationLevel.getValue()) {
                    delayCounter--;
                    if (delayCounter == 0) {
                        activation = false;
                        delayCounter = this.processor.toCycles(activationDelay.getValue());
                    }
                } else {
                    delayCounter = this.processor.toCycles(deactivationDelay.getValue());
                }
            } else {
                if (value > activationLevel.getValue()) {
                    delayCounter--;
                    if (delayCounter == 0) {
                        activation = true;
                        delayCounter = this.processor.toCycles(deactivationDelay.getValue());
                    }
                } else {
                    delayCounter = this.processor.toCycles(activationDelay.getValue());
                }
            }
        });
    }

    public int getValue() {
        return value;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public boolean getActivation() {
        return activation;
    }

    public boolean isOn() {
        return activation ? value > deactivationLevel.getValue() : value > activationLevel.getValue();
    }

    public long getDelayCounter() {
        return delayCounter;
    }
}
