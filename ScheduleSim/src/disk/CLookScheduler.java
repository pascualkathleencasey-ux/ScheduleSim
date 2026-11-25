package disk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * C-LOOK (circular LOOK).
 *
 * Owner: ____________________ (replace with the member's name)
 *
 * How it works: the head moves in the initial direction serving requests only
 * up to the LAST PENDING REQUEST (it does not go to the disk end). It then
 * jumps straight to the farthest pending request on the other side and keeps
 * moving in the same direction.
 *
 * Conventions (project sheet 4.3):
 *  - The return jump is counted in total head movement and shown in the path.
 *  - Duplicate requests are served one after another (0 movement).
 *  - A request exactly at the head position is served first, with 0 movement.
 */
public class CLookScheduler implements DiskScheduler {

    @Override
    public String getName() {
        return "C-LOOK";
    }

    @Override
    public DiskResult schedule(List<Integer> requests, int head, int diskSize,
                               Direction direction) {
        List<Integer> sorted = new ArrayList<Integer>(requests);
        Collections.sort(sorted);

        DiskPathBuilder path = new DiskPathBuilder(head);

        if (direction == Direction.UP) {
            // Serve everything at or above the head, ascending, up to the last request.
            for (int i = 0; i < sorted.size(); i++) {
                if (sorted.get(i) >= head) {
                    path.service(sorted.get(i));
                }
            }
            // Jump to the lowest pending request, then continue upward.
            boolean jumped = false;
            for (int i = 0; i < sorted.size(); i++) {
                if (sorted.get(i) < head) {
                    if (!jumped) {
                        path.jumpAndService(sorted.get(i));
                        jumped = true;
                    } else {
                        path.service(sorted.get(i));
                    }
                }
            }
        } else {
            // Serve everything at or below the head, descending, down to the last request.
            for (int i = sorted.size() - 1; i >= 0; i--) {
                if (sorted.get(i) <= head) {
                    path.service(sorted.get(i));
                }
            }
            // Jump to the highest pending request, then continue downward.
            boolean jumped = false;
            for (int i = sorted.size() - 1; i >= 0; i--) {
                if (sorted.get(i) > head) {
                    if (!jumped) {
                        path.jumpAndService(sorted.get(i));
                        jumped = true;
                    } else {
                        path.service(sorted.get(i));
                    }
                }
            }
        }
        return path.build(getName(), head, diskSize, direction, requests.size());
    }
}
