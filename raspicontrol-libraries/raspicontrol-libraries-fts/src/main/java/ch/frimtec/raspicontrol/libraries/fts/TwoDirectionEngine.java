package ch.frimtec.raspicontrol.libraries.fts;

public interface TwoDirectionEngine {
    enum Direction {
        IN,
        OUT
    }

    void move(Direction direction);
}
