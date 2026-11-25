package gui;

import disk.Direction;

/**
 * Sample inputs for the "Load Sample Data" buttons. These are small practice
 * problems. Replace them with the facilitator's practice problem sets once
 * they are released (Week 8).
 */
final class SampleData {

    /** { PID, arrival time, burst time } */
    static final String[][] CPU_ROWS = {
        {"P1", "0", "5"},
        {"P2", "1", "3"},
        {"P3", "2", "1"},
    };
    static final int QUANTUM = 2;

    static final String DISK_REQUESTS = "98, 183, 37, 122, 14, 124, 65, 67";
    static final int DISK_HEAD = 53;
    static final int DISK_SIZE = 200;
    static final Direction DISK_DIRECTION = Direction.UP;

    private SampleData() { }
}
