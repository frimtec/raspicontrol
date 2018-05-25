package ch.frimtec.raspicontrol.libraries.fts;

@FunctionalInterface
public interface DigitalOutputPin {
    void set(boolean state);
}
