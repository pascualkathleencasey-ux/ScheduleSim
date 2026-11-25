package cpu;

import java.util.Collections;
import java.util.List;

/**
 * Everything a CPU scheduling run produces: the Gantt chart, the per-process
 * times, the three averages, CPU utilization and throughput.
 */
public final class CPUResult {
    private final String algorithm;
    private final List<SimProcess> processes;
    private final List<GanttSlice> gantt;
    private final int totalTime;
    private final int busyTime;

    CPUResult(String algorithm, List<SimProcess> processes, List<GanttSlice> gantt) {
        this.algorithm = algorithm;
        this.processes = Collections.unmodifiableList(processes);
        this.gantt = Collections.unmodifiableList(gantt);

        int lastCompletion = 0;
        for (SimProcess p : processes) {
            lastCompletion = Math.max(lastCompletion, p.getCompletion());
        }
        int busy = 0;
        for (GanttSlice s : gantt) {
            if (!s.isIdle()) {
                busy += s.getLength();
            }
        }
        this.totalTime = lastCompletion;
        this.busyTime = busy;
    }

    public String getAlgorithm() { return algorithm; }
    /** Processes in the order they were entered, with CT/TAT/WT/RT filled in. */
    public List<SimProcess> getProcesses() { return processes; }
    public List<GanttSlice> getGantt() { return gantt; }
    public int getTotalTime() { return totalTime; }
    public int getBusyTime() { return busyTime; }

    public double getAverageTurnaround() {
        double sum = 0;
        for (SimProcess p : processes) sum += p.getTurnaround();
        return sum / processes.size();
    }

    public double getAverageWaiting() {
        double sum = 0;
        for (SimProcess p : processes) sum += p.getWaiting();
        return sum / processes.size();
    }

    public double getAverageResponse() {
        double sum = 0;
        for (SimProcess p : processes) sum += p.getResponse();
        return sum / processes.size();
    }

    /** Utilization = busy time / total time x 100. */
    public double getCpuUtilization() {
        return totalTime == 0 ? 0 : (busyTime * 100.0) / totalTime;
    }

    /** Throughput = processes / total time. */
    public double getThroughput() {
        return totalTime == 0 ? 0 : (double) processes.size() / totalTime;
    }
}
