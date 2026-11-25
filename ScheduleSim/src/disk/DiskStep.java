package disk;

/** One move of the disk head, from one cylinder to another. */
public final class DiskStep {

    /** Why the head moved. */
    public enum Type {
        SERVICE("Serve request"),
        SWEEP_TO_END("Move to disk end"),
        JUMP("Return jump");

        private final String label;

        Type(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final int from;
    private final int to;
    private final Type type;

    public DiskStep(int from, int to, Type type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public int getFrom() { return from; }
    public int getTo() { return to; }
    public Type getType() { return type; }

    /** Head movement for this step: |to - from|. */
    public int getDistance() { return Math.abs(to - from); }
}
