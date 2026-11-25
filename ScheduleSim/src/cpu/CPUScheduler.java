package cpu;

import java.util.List;

/**
 * Shared interface for every CPU scheduling algorithm. The GUI only talks to
 * this interface, so a new algorithm plugs in without changing the screens.
 */
public interface CPUScheduler {

    /** Name shown on screens, e.g. "Shortest Remaining Time First (SRTF)". */
    String getName();

    /** True if the algorithm needs a time quantum (Round Robin). */
    boolean needsTimeQuantum();

    /**
     * Runs the algorithm. The input list is not modified.
     *
     * @param processes   validated input (arrival >= 0, burst > 0)
     * @param timeQuantum used only when needsTimeQuantum() is true (must be > 0)
     */
    CPUResult schedule(List<SimProcess> processes, int timeQuantum);
}
