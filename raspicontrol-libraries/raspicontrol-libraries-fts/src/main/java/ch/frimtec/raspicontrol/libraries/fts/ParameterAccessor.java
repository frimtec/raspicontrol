package ch.frimtec.raspicontrol.libraries.fts;

public interface ParameterAccessor <T extends Comparable<T>> {
    T getValue();
}
