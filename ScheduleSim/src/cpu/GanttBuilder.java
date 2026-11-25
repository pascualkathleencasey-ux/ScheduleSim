package cpu;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects Gantt slices. When merging is on, back-to-back slices of the same
 * process become one block (SRTF). With merging off, every slice is kept, so
 * Round Robin shows each quantum separately.
 */
final class GanttBuilder {
    private final List<GanttSlice> slices = new ArrayList<GanttSlice>();
    private final boolean mergeAdjacent;

    GanttBuilder(boolean mergeAdjacent) {
        this.mergeAdjacent = mergeAdjacent;
    }

    void add(String pid, int start, int end) {
        if (end <= start) {
            return;
        }
        if (mergeAdjacent && !slices.isEmpty()) {
            GanttSlice last = slices.get(slices.size() - 1);
            if (last.getPid().equals(pid) && last.getEnd() == start) {
                slices.set(slices.size() - 1, new GanttSlice(pid, last.getStart(), end));
                return;
            }
        }
        slices.add(new GanttSlice(pid, start, end));
    }

    List<GanttSlice> build() {
        return slices;
    }
}
