package disk;

/** Initial direction of the disk head. */
public enum Direction {
    UP("Up (toward higher cylinders)"),
    DOWN("Down (toward lower cylinders)");

    private final String label;

    Direction(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
