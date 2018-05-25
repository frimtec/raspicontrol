package ch.frimtec.raspicontrol.application.jawning;

import ch.frimtec.raspicontrol.libraries.fts.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static ch.frimtec.raspicontrol.application.jawning.AwningController.State.*;
import static ch.frimtec.raspicontrol.libraries.fts.TwoDirectionEngine.Direction.IN;
import static ch.frimtec.raspicontrol.libraries.fts.TwoDirectionEngine.Direction.OUT;

public class AwningController {
    private static final Logger logger = LoggerFactory.getLogger(AwningController.class);

    enum State {
        INIT,
        AUTOMATIC_IN,
        AUTOMATIC_OUT,
        MANUAL_IN,
        MANUAL_OUT
    }

    private final Processor processor;
    private final DigitalSensor manualIn;
    private final DigitalSensor manualOut;
    private final Sensor sun;
    private final Sensor wind;

    private final TwoDirectionEngine awning;

    private final ParameterAccessor<Duration> manualTimeout;

    private final Timer timer;
    private State currentState = INIT;

    public AwningController(Processor processor, DigitalSensor manualIn, DigitalSensor manualOut, ParameterAccessor<Duration> manualTimeout,
                            Sensor sun, Sensor wind, TwoDirectionEngine awning) {
        this.processor = processor;
        this.manualIn = manualIn;
        this.manualTimeout = manualTimeout;
        this.manualOut = manualOut;
        this.sun = sun;
        this.wind = wind;
        this.awning = awning;
        this.timer = new Timer(processor);

        processor.addRunnable((FixedTimeSlicedRunnable) () -> {
            long manualTimeoutValue = this.processor.toCycles(this.manualTimeout.getValue());
            switch (currentState) {
                case INIT:
                    this.awning.move(IN);
                    changeState(AUTOMATIC_IN);
                    break;
                case AUTOMATIC_IN:
                    if (!this.wind.getActivation() && this.sun.getActivation()) {
                        this.awning.move(OUT);
                        changeState(AUTOMATIC_OUT);
                    } else if (!this.wind.getActivation() && this.manualOut.getActivation()) {
                        this.awning.move(OUT);
                        timer.start(manualTimeoutValue);
                        changeState(MANUAL_OUT);
                    } else if (this.manualIn.getActivation()) {
                        timer.start(manualTimeoutValue);
                        changeState(MANUAL_IN);
                    }
                    break;
                case AUTOMATIC_OUT:
                    if (this.wind.getActivation() || !this.sun.getActivation()) {
                        this.awning.move(IN);
                        changeState(AUTOMATIC_IN);
                    } else if (this.manualIn.getActivation()) {
                        this.awning.move(IN);
                        timer.start(manualTimeoutValue);
                        changeState(MANUAL_IN);
                    } else if (this.manualOut.getActivation()) {
                        timer.start(manualTimeoutValue);
                        changeState(MANUAL_OUT);
                    }
                    break;
                case MANUAL_IN:
                    if (this.manualOut.getActivation()) {
                        this.awning.move(OUT);
                        timer.start(manualTimeoutValue);
                        changeState(MANUAL_OUT);
                    } else if (this.manualIn.getActivation()) {
                        timer.start(manualTimeoutValue);
                    } else if (timer.getActivation()) {
                        changeState(AUTOMATIC_IN);
                    }
                    break;
                case MANUAL_OUT:
                    if (this.wind.getActivation()) {
                        this.awning.move(IN);
                        changeState(AUTOMATIC_IN);
                    } else if (this.manualIn.getActivation()) {
                        this.awning.move(IN);
                        timer.start(manualTimeoutValue);
                        changeState(MANUAL_IN);
                    } else if (this.manualOut.getActivation()) {
                        timer.start(manualTimeoutValue);
                    } else if (timer.getActivation()) {
                        changeState(AUTOMATIC_OUT);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown stage: " + currentState);
            }
        });
    }

    private void changeState(State newState) {
        logger.info("State change from {} to {}", this.currentState, newState);
        currentState = newState;
    }
}
