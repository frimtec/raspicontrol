package ch.frimtec.raspicontrol.libraries.fts;

public class Parameter<T extends Comparable<T>> implements ParameterAccessor<T> {

    private final String id;
    private final String name;
    private T value;
    private final T min;
    private final T max;

    public Parameter(String id, String name, T initalValue, T min, T max) {
        this.id = id;
        this.name = name;
        this.value = initalValue;
        this.min = min;
        this.max = max;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }
}
