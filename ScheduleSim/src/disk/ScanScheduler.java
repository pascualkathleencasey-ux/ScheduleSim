package disk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SCAN (elevator algorithm).
 *
 * Owner: ____________________ (replace with the member's name)
 *
 * How it works: the head moves in the initial direction serving every request
 * on the way, travels to the PHYSICAL END of the disk (cylinder N-1 going up,
 * 0 going down), then reverses and serves the remaining requests.
 *
 * Conventions (project sheet 4.3):
 *  - Cylinders run from 0 to N-1.
 *  - Head movement = sum of absolute differences between successive positions.
 *  - Duplicate requests are served one after another (0 movement).
 *  - A request exactly at the head position is served first, with 0 movement.
 */
public class ScanScheduler implements DiskScheduler {

    @Override
    public String getName() {
        return "SCAN";
    }

    @Override
    public DiskResult schedule(List<Integer> requests, int head, int diskSize,
                               Direction direction) {
        List<Integer> sorted = new ArrayList<Integer>(requests);
        Collections.sort(sorted);

        DiskPathBuilder path = new DiskPathBuilder(head);

        if (direction == Direction.UP) {
            // Serve everything at or above the head, in ascending order.
            for (int i = 0; i < sorted.size(); i++) {
                if (sorted.get(i) >= head) {
                    path.service(sorted.get(i));
                }
            }
            path.sweepTo(diskSize - 1);
            // Reverse: serve everything below the head, in descending order.
            for (int i = sorted.size() - 1; i >= 0; i--) {
                if (sorted.get(i) < head) {
                    path.service(sorted.get(i));
                }
            }
        } else {
            // Serve everything at or below the head, in descending order.
            for (int i = sorted.size() - 1; i >= 0; i--) {
                if (sorted.get(i) <= head) {
                    path.service(sorted.get(i));
                }
            }
            path.sweepTo(0);
            // Reverse: serve everything above the head, in ascending order.
            for (int i = 0; i < sorted.size(); i++) {
                if (sorted.get(i) > head) {
                    path.service(sorted.get(i));
                }
            }
        }
        return path.build(getName(), head, diskSize, direction, requests.size());
    }
}
