package enums;

public enum Processor {
    TIME("time"),
    SIMPLE("simple"),
    TABLE("table");

    private String label;

    Processor(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}
