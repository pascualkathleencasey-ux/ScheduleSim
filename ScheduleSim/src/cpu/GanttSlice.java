package cpu;

/** One block of the Gantt chart: a process (or idle time) from start to end. */
public final class GanttSlice {
    public static final String IDLE = "IDLE";

    private final String pid;
    private final int start;
    private final int end;

    public GanttSlice(String pid, int start, int end) {
        this.pid = pid;
        this.start = start;
        this.end = end;
    }

    public String getPid() { return pid; }
    public int getStart() { return start; }
    public int getEnd() { return end; }
    public int getLength() { return end - start; }
    public boolean isIdle() { return IDLE.equals(pid); }
}
