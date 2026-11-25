package cpu;

import java.util.Comparator;
import java.util.List;

/**
 * Shortest Remaining Time First (preemptive SJF).
 *
 * Owner: ____________________ (replace with the member's name)
 *
 * How it works: the clock advances one time unit at a time. Among the
 * processes that have arrived and are unfinished, the one with the smallest
 * remaining time runs.
 *
 * Conventions (project sheet 4.3):
 *  - Ties on remaining time go to the earlier arrival, then the lower PID.
 *  - A newcomer preempts only if its remaining time is strictly less than the
 *    running process's remaining time.
 *  - If nobody is ready, the CPU is idle until the next arrival.
 *  - Context-switch time is 0.
 */
public class SRTFScheduler implements CPUScheduler {

    private static final Comparator<SimProcess> SHORTEST_REMAINING_FIRST =
            new Comparator<SimProcess>() {
                @Override
                public int compare(SimProcess a, SimProcess b) {
                    if (a.getRemaining() != b.getRemaining()) {
                        return Integer.compare(a.getRemaining(), b.getRemaining());
                    }
                    if (a.getArrival() != b.getArrival()) {
                        return Integer.compare(a.getArrival(), b.getArrival());
                    }
                    return Integer.compare(a.getOrder(), b.getOrder());
                }
            };

    @Override
    public String getName() {
        return "Shortest Remaining Time First (SRTF)";
    }

    @Override
    public boolean needsTimeQuantum() {
        return false;
    }

    @Override
    public CPUResult schedule(List<SimProcess> input, int timeQuantum) {
        List<SimProcess> procs = SimProcess.copyAll(input);
        GanttBuilder gantt = new GanttBuilder(true);

        int time = 0;
        int finished = 0;
        SimProcess running = null;

        while (finished < procs.size()) {
            SimProcess next = pickNext(procs, time, running);

            if (next == null) {
                // Nobody is ready: idle until the next arrival.
                int nextArrival = earliestUnfinishedArrival(procs);
                gantt.add(GanttSlice.IDLE, time, nextArrival);
                time = nextArrival;
                running = null;
                continue;
            }

            next.markFirstStart(time);
            gantt.add(next.getPid(), time, time + 1);
            next.run(1);
            time++;
            running = next;

            if (next.getRemaining() == 0) {
                next.setCompletion(time);
                finished++;
                running = null;
            }
        }
        return new CPUResult(getName(), procs, gantt.build());
    }

    /** Best ready process, but the running one is kept unless beaten strictly. */
    private SimProcess pickNext(List<SimProcess> procs, int time, SimProcess running) {
        SimProcess best = null;
        for (SimProcess p : procs) {
            if (p.getRemaining() > 0 && p.getArrival() <= time) {
                if (best == null || SHORTEST_REMAINING_FIRST.compare(p, best) < 0) {
                    best = p;
                }
            }
        }
        if (best != null && running != null && running.getRemaining() > 0
                && best.getRemaining() >= running.getRemaining()) {
            return running; // no strictly shorter newcomer, so no preemption
        }
        return best;
    }

    private int earliestUnfinishedArrival(List<SimProcess> procs) {
        int earliest = Integer.MAX_VALUE;
        for (SimProcess p : procs) {
            if (p.getRemaining() > 0) {
                earliest = Math.min(earliest, p.getArrival());
            }
        }
        return earliest;
    }
}
