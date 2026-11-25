package cpu;

import java.util.ArrayList;
import java.util.List;

/**
 * One process in a CPU scheduling run.
 *
 * Inputs (pid, arrival, burst) are fixed. The remaining time, first CPU time
 * and completion time are filled in by a scheduler. Schedulers always work on
 * copies (see {@link #copyAll(List)}) so the caller's list is never changed.
 *
 * The "order" field is the position in the input list. Because processes are
 * entered as P1, P2, P3..., it is used for the "lower PID" tie-break.
 */
public final class SimProcess {
    private final String pid;
    private final int arrival;
    private final int burst;

    private int order;
    private int remaining;
    private int firstStart = -1;
    private int completion = -1;

    public SimProcess(String pid, int arrival, int burst) {
        this.pid = pid;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining = burst;
    }

    /** Fresh copies (results cleared) in the same order, with order indexes set. */
    static List<SimProcess> copyAll(List<SimProcess> source) {
        List<SimProcess> copies = new ArrayList<SimProcess>();
        for (int i = 0; i < source.size(); i++) {
            SimProcess p = source.get(i);
            SimProcess copy = new SimProcess(p.pid, p.arrival, p.burst);
            copy.order = i;
            copies.add(copy);
        }
        return copies;
    }

    // ----- inputs -----
    public String getPid() { return pid; }
    public int getArrival() { return arrival; }
    public int getBurst() { return burst; }

    // ----- scheduler state (package-private: only schedulers change it) -----
    int getOrder() { return order; }
    int getRemaining() { return remaining; }
    int getFirstStart() { return firstStart; }

    void run(int units) { remaining -= units; }
    void markFirstStart(int time) { if (firstStart < 0) firstStart = time; }
    void setCompletion(int time) { completion = time; }

    // ----- results -----
    public int getCompletion() { return completion; }
    /** Turnaround = Completion - Arrival. */
    public int getTurnaround() { return completion - arrival; }
    /** Waiting = Turnaround - Burst. */
    public int getWaiting() { return getTurnaround() - burst; }
    /** Response = First CPU time - Arrival. */
    public int getResponse() { return firstStart - arrival; }
}
