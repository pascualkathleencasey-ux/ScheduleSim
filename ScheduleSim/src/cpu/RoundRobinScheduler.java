package cpu;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

/**
 * Round Robin (preemptive, fixed time quantum).
 *
 * Owner: ____________________ (replace with the member's name)
 *
 * How it works: processes wait in a FIFO ready queue. The front process runs
 * for min(quantum, remaining). If it still has work left it goes to the back
 * of the queue.
 *
 * Conventions (project sheet 4.3):
 *  - If a new arrival and the preempted process reach the queue at the same
 *    instant, the new arrival is queued first.
 *  - Arrivals at the same time enter the queue by lower PID.
 *  - If the queue is empty, the CPU is idle until the next arrival.
 *  - Context-switch time is 0.
 *
 * The Gantt chart keeps every quantum as its own block.
 */
public class RoundRobinScheduler implements CPUScheduler {

    @Override
    public String getName() {
        return "Round Robin (RR)";
    }

    @Override
    public boolean needsTimeQuantum() {
        return true;
    }

    @Override
    public CPUResult schedule(List<SimProcess> input, int timeQuantum) {
        if (timeQuantum <= 0) {
            throw new IllegalArgumentException("Time quantum must be greater than 0.");
        }

        List<SimProcess> procs = SimProcess.copyAll(input);
        List<SimProcess> byArrival = new ArrayList<SimProcess>(procs);
        Collections.sort(byArrival, new Comparator<SimProcess>() {
            @Override
            public int compare(SimProcess a, SimProcess b) {
                if (a.getArrival() != b.getArrival()) {
                    return Integer.compare(a.getArrival(), b.getArrival());
                }
                return Integer.compare(a.getOrder(), b.getOrder());
            }
        });

        Deque<SimProcess> queue = new ArrayDeque<SimProcess>();
        GanttBuilder gantt = new GanttBuilder(false);

        int time = 0;
        int nextArrival = 0;   // index into byArrival of the next process to admit
        int finished = 0;

        nextArrival = admitArrivals(byArrival, nextArrival, time, queue);

        while (finished < procs.size()) {
            if (queue.isEmpty()) {
                // Everything unfinished has not arrived yet: idle until it does.
                int arrivalTime = byArrival.get(nextArrival).getArrival();
                gantt.add(GanttSlice.IDLE, time, arrivalTime);
                time = arrivalTime;
                nextArrival = admitArrivals(byArrival, nextArrival, time, queue);
                continue;
            }

            SimProcess current = queue.pollFirst();
            current.markFirstStart(time);

            int slice = Math.min(timeQuantum, current.getRemaining());
            gantt.add(current.getPid(), time, time + slice);
            time += slice;
            current.run(slice);

            // New arrivals are queued BEFORE the preempted process.
            nextArrival = admitArrivals(byArrival, nextArrival, time, queue);

            if (current.getRemaining() == 0) {
                current.setCompletion(time);
                finished++;
            } else {
                queue.addLast(current);
            }
        }
        return new CPUResult(getName(), procs, gantt.build());
    }

    /** Moves every process with arrival <= time into the queue; returns the new index. */
    private int admitArrivals(List<SimProcess> byArrival, int index, int time,
                              Deque<SimProcess> queue) {
        while (index < byArrival.size() && byArrival.get(index).getArrival() <= time) {
            queue.addLast(byArrival.get(index));
            index++;
        }
        return index;
    }
}
