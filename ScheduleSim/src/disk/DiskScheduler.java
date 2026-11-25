package disk;

import java.util.List;

/**
 * Shared interface for every disk scheduling algorithm. The GUI only talks to
 * this interface.
 */
public interface DiskScheduler {

    /** Name shown on screens, e.g. "SCAN". */
    String getName();

    /**
     * Runs the algorithm. The request list is not modified.
     *
     * @param requests  requested cylinders (each between 0 and diskSize - 1)
     * @param head      initial head position (between 0 and diskSize - 1)
     * @param diskSize  number of cylinders N; cylinders run from 0 to N - 1
     * @param direction initial direction of the head
     */
    DiskResult schedule(List<Integer> requests, int head, int diskSize, Direction direction);
}
