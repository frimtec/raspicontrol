package ch.frimtec.raspicontrol.libraries.fts;

public class SimpleTwoDirectionEngine implements TwoDirectionEngine {
    private final OneShotActor powerSwitch;
    private final DigitalOutputPin directionSwitch;
    private final boolean inLevel;

    public SimpleTwoDirectionEngine(OneShotActor powerSwitch, DigitalOutputPin directionSwitch, boolean inLevel) {
        this.powerSwitch = powerSwitch;
        this.directionSwitch = directionSwitch;
        this.inLevel = inLevel;
    }

    @Override
    public void move(Direction direction) {
        switch (direction) {
            case IN:
                directionSwitch.set(inLevel);
                powerSwitch.setActivation(true);
                break;
            case OUT:
                directionSwitch.set(!inLevel);
                powerSwitch.setActivation(true);
                break;
            default:
                throw new IllegalStateException("Direction not supported: " + direction);
        }
    }
}
